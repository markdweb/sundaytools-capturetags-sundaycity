package com.sundaytools.app.capture

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.WindowManager
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Owns the [MediaProjection] session created from a one-time user permission
 * grant, and performs on-demand single-frame captures.
 *
 * Two important constraints on modern Android versions drive this design:
 *
 * 1. [MediaProjection.createVirtualDisplay] can only be called ONCE per granted
 *    MediaProjection instance -- calling it again on a second tap throws a
 *    SecurityException ("Don't re-use the resultData..."). So the VirtualDisplay
 *    is created lazily on the first capture and kept alive for this manager's
 *    whole lifetime, instead of being recreated per tap.
 * 2. If nothing drains the ImageReader between captures, the VirtualDisplay's
 *    small buffer queue fills up and screen mirroring effectively stalls, so
 *    the *next* capture returns a stale, outdated frame instead of the current
 *    screen. To avoid that, a single listener stays attached for the whole
 *    session: frames are continuously drained and dropped in the background,
 *    and only the frame that arrives *after* a capture is explicitly requested
 *    is kept and turned into a Bitmap.
 */
class ScreenCaptureManager(
    private val context: Context,
    resultCode: Int,
    resultData: Intent
) {
    private val projectionManager =
        context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

    private val mediaProjection: MediaProjection =
        projectionManager.getMediaProjection(resultCode, resultData)

    private val mainHandler = Handler(Looper.getMainLooper())

    private var isProjectionActive = true

    private var imageReader: ImageReader? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var captureWidth = 0
    private var captureHeight = 0

    /** Set only while a caller is actively waiting for a captured frame. */
    private var pendingContinuation: CancellableContinuation<Bitmap?>? = null

    /**
     * Android 14+ requires a callback to be registered before the projection
     * can be used to create a VirtualDisplay, so the app can react if the
     * system revokes/stops the projection (e.g. user stops sharing from the
     * system status bar).
     */
    private val projectionCallback = object : MediaProjection.Callback() {
        override fun onStop() {
            isProjectionActive = false
            teardownVirtualDisplay()
        }
    }

    init {
        mediaProjection.registerCallback(projectionCallback, mainHandler)
    }

    /**
     * Creates the long-lived VirtualDisplay + ImageReader pair exactly once,
     * and attaches a single listener that keeps draining frames for the whole
     * session -- consuming/dropping them when no capture is pending, or
     * turning them into a Bitmap when one is.
     */
    private fun ensureVirtualDisplay() {
        if (virtualDisplay != null) return

        val metrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getRealMetrics(metrics)

        captureWidth = metrics.widthPixels
        captureHeight = metrics.heightPixels
        val density = metrics.densityDpi

        val reader = ImageReader.newInstance(captureWidth, captureHeight, PixelFormat.RGBA_8888, 2)
        imageReader = reader

        reader.setOnImageAvailableListener({ r -> onFrameAvailable(r) }, mainHandler)

        virtualDisplay = mediaProjection.createVirtualDisplay(
            "SundayToolsCapture",
            captureWidth,
            captureHeight,
            density,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            reader.surface,
            null,
            mainHandler
        )
    }

    private fun onFrameAvailable(reader: ImageReader) {
        val image = runCatching { reader.acquireLatestImage() }.getOrNull() ?: return
        val continuation = pendingContinuation
        if (continuation == null) {
            // No capture requested right now -- just drop the frame so the
            // buffer keeps flowing and mirroring never stalls.
            image.close()
            return
        }
        pendingContinuation = null
        try {
            val bitmap = imageToBitmap(image)
            if (continuation.isActive) {
                continuation.resume(bitmap)
            }
        } finally {
            image.close()
        }
    }

    private fun imageToBitmap(image: Image): Bitmap {
        val width = captureWidth
        val height = captureHeight
        val planes = image.planes
        val buffer = planes[0].buffer
        val pixelStride = planes[0].pixelStride
        val rowStride = planes[0].rowStride
        val rowPadding = rowStride - pixelStride * width

        val bitmap = Bitmap.createBitmap(
            width + rowPadding / pixelStride,
            height,
            Bitmap.Config.ARGB_8888
        )
        bitmap.copyPixelsFromBuffer(buffer)

        return Bitmap.createBitmap(bitmap, 0, 0, width, height)
    }

    /**
     * Captures a single fresh frame of the full device screen and returns it as
     * a Bitmap, reusing the same long-lived VirtualDisplay/ImageReader.
     */
    suspend fun captureFrame(): Bitmap? = suspendCancellableCoroutine { continuation ->
        if (!isProjectionActive) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        ensureVirtualDisplay()
        if (imageReader == null) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }

        pendingContinuation = continuation
        continuation.invokeOnCancellation {
            if (pendingContinuation === continuation) {
                pendingContinuation = null
            }
        }
    }

    private fun teardownVirtualDisplay() {
        virtualDisplay?.release()
        virtualDisplay = null
        imageReader?.setOnImageAvailableListener(null, null)
        imageReader?.close()
        imageReader = null
    }

    /** Releases the MediaProjection session entirely, e.g. when the overlay is dismissed. */
    fun release() {
        runCatching { mediaProjection.unregisterCallback(projectionCallback) }
        teardownVirtualDisplay()
        mediaProjection.stop()
    }
}
