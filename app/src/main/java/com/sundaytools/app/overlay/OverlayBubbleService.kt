package com.sundaytools.app.overlay

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.IBinder
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import com.sundaytools.app.MainActivity
import com.sundaytools.app.R
import com.sundaytools.app.capture.ScreenCaptureManager
import com.sundaytools.app.capture.ScreenCapturePermissionActivity
import com.sundaytools.app.network.BlacklistApiService
import com.sundaytools.app.ocr.ScannedPlayer
import com.sundaytools.app.ocr.TagOcrProcessor
import com.sundaytools.app.ocr.displayLabel
import com.sundaytools.app.repository.PlayerTagRepository
import com.sundaytools.app.repository.SaveTagResult
import com.sundaytools.app.repository.SettingsRepository
import com.sundaytools.app.utils.ClipboardUtils
import com.sundaytools.app.utils.FeedbackUtils
import com.sundaytools.app.utils.WarningMessageUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Foreground service that owns the entire floating-bubble experience:
 * - Draws a draggable Messenger-style bubble via WindowManager, with a
 *   drag-to-close target at the bottom of the screen.
 * - On "Scan Tag", triggers a single-frame screen capture + OCR pass that
 *   pulls out both the player's name and tag.
 * - Shows a Blacklist Warning if the tag was already scanned before, or the
 *   normal save/copy/cancel dialog otherwise.
 *
 * The bubble and popups are all plain Android views hosted directly in
 * WindowManager (rather than an Activity), which is what allows them to float
 * above other apps, similar to Facebook Messenger's Chat Heads.
 */
@AndroidEntryPoint
class OverlayBubbleService : Service(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    @Inject lateinit var tagRepository: PlayerTagRepository
    @Inject lateinit var settingsRepository: SettingsRepository
    @Inject lateinit var ocrProcessor: TagOcrProcessor
    @Inject lateinit var blacklistApiService: BlacklistApiService

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private lateinit var windowManager: WindowManager
    private var bubbleController: BubbleViewController? = null
    private var menuController: BubbleMenuController? = null
    private var regionSelectController: RegionSelectController? = null
    private var dialogController: SaveDialogController? = null
    private var blacklistWarningController: BlacklistWarningController? = null

    private var screenCaptureManager: ScreenCaptureManager? = null
    private var pendingCaptureAfterPermission = false

    /**
     * Guards the whole "Scan Tag" flow (permission request through to the
     * save/warning popup appearing) against being re-entered by a second,
     * impatient tap -- that double-triggering was what caused the system
     * "Entire Screen" permission chooser to show up repeatedly on first use.
     */
    private var isProcessingCapture = false

    // Lifecycle plumbing so Compose views can be hosted from a Service.
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val viewModelStoreImpl = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = viewModelStoreImpl
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        startForegroundCompat(useMediaProjectionType = false)
        showBubble()
    }

    /**
     * Android 14+ (API 34) requires the service to already be running as a
     * foreground service of type mediaProjection *before* MediaProjection is
     * requested/used -- but the OS also refuses to grant the mediaProjection
     * type until the user has actually completed the screen-capture consent
     * dialog. So we start as a plain "specialUse" service when the bubble
     * first appears (no projection yet), then re-promote to mediaProjection
     * right after permission is granted, just before creating
     * [ScreenCaptureManager]. ServiceCompat handles API-level differences.
     */
    private fun startForegroundCompat(useMediaProjectionType: Boolean) {
        val notification = buildNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val type = if (useMediaProjectionType) {
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
            } else {
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            }
            androidx.core.app.ServiceCompat.startForeground(this, NOTIFICATION_ID, notification, type)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_CAPTURE_PERMISSION_GRANTED -> {
                val resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, 0)
                val resultData = intent.getParcelableExtra<Intent>(EXTRA_RESULT_DATA)
                if (resultData != null) {
                    startForegroundCompat(useMediaProjectionType = true)
                    screenCaptureManager = ScreenCaptureManager(this, resultCode, resultData)
                    if (pendingCaptureAfterPermission) {
                        pendingCaptureAfterPermission = false
                        performCapture()
                    } else {
                        clearCaptureBusyState()
                    }
                } else {
                    // Permission flow returned without data -- don't leave the
                    // bubble stuck in a busy state waiting for a capture that
                    // will never happen.
                    pendingCaptureAfterPermission = false
                    clearCaptureBusyState()
                }
            }
            ACTION_CAPTURE_PERMISSION_DENIED -> {
                pendingCaptureAfterPermission = false
                clearCaptureBusyState()
            }
            ACTION_STOP -> {
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun clearCaptureBusyState() {
        isProcessingCapture = false
        bubbleController?.setBusy(false)
    }

    private fun buildNotification(): android.app.Notification {
        val channelId = "sundaytools_overlay_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                channelId,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            )
            manager.createNotificationChannel(channel)
        }

        val openAppIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = PendingIntent.getService(
            this, 0,
            Intent(this, OverlayBubbleService::class.java).setAction(ACTION_STOP),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.notification_content_text))
            .setContentIntent(openAppIntent)
            .addAction(0, "Stop", stopIntent)
            .setOngoing(true)
            .build()
    }

    private fun isDarkTheme(): Boolean = settingsRepository.getSettings().darkMode

    private fun showBubble() {
        bubbleController = BubbleViewController(
            context = this,
            windowManager = windowManager,
            lifecycleOwner = this,
            viewModelStoreOwner = this,
            savedStateRegistryOwner = this,
            darkTheme = isDarkTheme(),
            onTap = { onBubbleTapped() },
            onRequestRemove = { stopSelf() }
        )
        bubbleController?.show()
    }

    private fun onBubbleTapped() {
        val existingMenu = menuController
        if (existingMenu != null) {
            existingMenu.dismiss()
            menuController = null
            return
        }
        val controller = bubbleController ?: return
        val (x, y) = controller.currentPosition()
        menuController = BubbleMenuController(
            context = this,
            windowManager = windowManager,
            lifecycleOwner = this,
            viewModelStoreOwner = this,
            savedStateRegistryOwner = this,
            anchorX = x,
            anchorY = y,
            bubbleSizePx = controller.bubbleSize(),
            onScanTag = {
                menuController?.dismiss()
                menuController = null
                onScanTagRequested()
            },
            onSetCaptureArea = {
                menuController?.dismiss()
                menuController = null
                showRegionSelector()
            },
            darkTheme = isDarkTheme()
        )
        menuController?.show()
    }

    private fun showRegionSelector() {
        regionSelectController?.dismiss()
        regionSelectController = RegionSelectController(
            context = this,
            windowManager = windowManager,
            lifecycleOwner = this,
            viewModelStoreOwner = this,
            savedStateRegistryOwner = this,
            onSave = { left, top, right, bottom ->
                settingsRepository.setCaptureRegion(left, top, right, bottom)
                regionSelectController = null
            },
            onCancel = { regionSelectController = null },
            darkTheme = isDarkTheme()
        )
        regionSelectController?.show()
    }

    /**
     * Runs after the user explicitly taps "Scan Tag" in the popup menu.
     * Guarded by [isProcessingCapture] so a second, impatient tap while a
     * capture (or its permission request) is already in flight is simply
     * ignored, instead of kicking off a second overlapping flow.
     */
    private fun onScanTagRequested() {
        if (isProcessingCapture) return
        isProcessingCapture = true
        bubbleController?.setBusy(true)

        val manager = screenCaptureManager
        if (manager == null) {
            requestScreenCapturePermission()
            return
        }
        performCapture()
    }

    private fun requestScreenCapturePermission() {
        pendingCaptureAfterPermission = true
        val intent = Intent(this, ScreenCapturePermissionActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun performCapture() {
        serviceScope.launch {
            val rawBitmap: Bitmap? = screenCaptureManager?.captureFrame()
            if (rawBitmap == null) {
                clearCaptureBusyState()
                return@launch
            }

            val settings = settingsRepository.getSettings()
            val bitmap = if (settings.hasCaptureRegion) {
                cropToRegion(rawBitmap, settings.regionLeft, settings.regionTop, settings.regionRight, settings.regionBottom)
            } else {
                rawBitmap
            }

            val scanned: ScannedPlayer? = ocrProcessor.extractPlayerInfo(bitmap)
            if (bitmap !== rawBitmap) {
                rawBitmap.recycle()
            }
            bitmap.recycle()

            if (scanned == null) {
                clearCaptureBusyState()
                return@launch
            }

            if (settings.autoCopyEnabled) {
                ClipboardUtils.copyToClipboard(this@OverlayBubbleService, scanned.displayLabel)
            }
            if (settings.vibrateOnScan) {
                FeedbackUtils.vibrate(this@OverlayBubbleService)
            }
            if (settings.soundOnScan) {
                FeedbackUtils.playScanSound()
            }

            // A tag that's already on record (any previous day, on this device
            // OR on the shared Google Sheet) is treated as blacklisted: show
            // the warning popup instead of the normal save flow. The local
            // check runs first since it's instant; the remote check only
            // fires when needed and never blocks capture if the network is
            // unavailable -- it just falls back to the normal save flow.
            val isBlacklistedLocally = tagRepository.isBlacklisted(scanned.tag)
            val isBlacklistedRemotely = !isBlacklistedLocally &&
                settings.cloudSyncEnabled &&
                settings.backendUrl.isNotBlank() &&
                checkRemoteBlacklist(scanned.tag)

            clearCaptureBusyState()

            if (isBlacklistedLocally || isBlacklistedRemotely) {
                showBlacklistWarning(scanned, settings.blacklistWarningTemplate)
            } else {
                showSaveDialog(scanned)
            }
        }
    }

    private suspend fun checkRemoteBlacklist(tag: String): Boolean {
        return when (val outcome = blacklistApiService.checkTag(tag)) {
            is BlacklistApiService.ApiOutcome.Success -> outcome.value
            is BlacklistApiService.ApiOutcome.Failure -> false
        }
    }

    /** Crops [source] to the rectangle defined by the given fractions (0..1) of its width/height. */
    private fun cropToRegion(
        source: Bitmap,
        leftFrac: Float,
        topFrac: Float,
        rightFrac: Float,
        bottomFrac: Float
    ): Bitmap {
        val left = (leftFrac * source.width).toInt().coerceIn(0, source.width - 1)
        val top = (topFrac * source.height).toInt().coerceIn(0, source.height - 1)
        val right = (rightFrac * source.width).toInt().coerceIn(left + 1, source.width)
        val bottom = (bottomFrac * source.height).toInt().coerceIn(top + 1, source.height)
        return Bitmap.createBitmap(source, left, top, right - left, bottom - top)
    }

    /** Dismisses both popup types -- called before showing either one, so a stale popup never lingers behind a new one. */
    private fun dismissCapturePopups() {
        dialogController?.dismiss()
        dialogController = null
        blacklistWarningController?.dismiss()
        blacklistWarningController = null
    }

    private fun showSaveDialog(scanned: ScannedPlayer) {
        dismissCapturePopups()
        dialogController = SaveDialogController(
            context = this,
            windowManager = windowManager,
            lifecycleOwner = this,
            viewModelStoreOwner = this,
            savedStateRegistryOwner = this,
            playerName = scanned.name,
            playerTag = scanned.tag,
            onSave = { editedName, status -> onSaveTag(scanned.copy(name = editedName), status) },
            onCopyAgain = { currentName ->
                val label = if (currentName != null) "$currentName - ${scanned.tag}" else scanned.tag
                ClipboardUtils.copyToClipboard(this, label)
                android.widget.Toast.makeText(this, "Copied: $label", android.widget.Toast.LENGTH_SHORT).show()
            },
            onDismiss = { dialogController = null },
            darkTheme = isDarkTheme()
        )
        dialogController?.show()
    }

    private fun showBlacklistWarning(scanned: ScannedPlayer, template: String) {
        dismissCapturePopups()
        blacklistWarningController = BlacklistWarningController(
            context = this,
            windowManager = windowManager,
            lifecycleOwner = this,
            viewModelStoreOwner = this,
            savedStateRegistryOwner = this,
            playerName = scanned.name,
            playerTag = scanned.tag,
            onCopyWarning = {
                val message = WarningMessageUtils.buildBlacklistWarning(template, scanned.name, scanned.tag)
                ClipboardUtils.copyToClipboard(this, message)
                android.widget.Toast.makeText(this, "Warning message copied", android.widget.Toast.LENGTH_SHORT).show()
            },
            onDismiss = { blacklistWarningController = null },
            darkTheme = isDarkTheme()
        )
        blacklistWarningController?.show()
    }

    private fun onSaveTag(scanned: ScannedPlayer, status: String) {
        serviceScope.launch {
            val settings = settingsRepository.getSettings()
            val result = tagRepository.saveTag(scanned.name, scanned.tag, status)
            val label = if (scanned.name != null) "${scanned.name} - ${scanned.tag}" else scanned.tag
            val message = when (result) {
                is SaveTagResult.Saved -> "Saved: $label ($status)"
                SaveTagResult.AlreadySavedToday -> "Already saved today: $label"
            }
            android.widget.Toast.makeText(this@OverlayBubbleService, message, android.widget.Toast.LENGTH_SHORT).show()
            dialogController?.dismiss()
            dialogController = null

            // Sync to the shared Google Sheets database (best-effort: local
            // save above already succeeded regardless of network state).
            if (result is SaveTagResult.Saved && settings.cloudSyncEnabled && settings.backendUrl.isNotBlank()) {
                syncEntryToCloud(result.entity.id, scanned, status, settings.addedByName)
            }
        }
    }

    private fun syncEntryToCloud(localId: Long, scanned: ScannedPlayer, status: String, addedBy: String) {
        serviceScope.launch {
            when (val outcome = blacklistApiService.addEntry(
                tag = scanned.tag,
                name = scanned.name,
                status = status,
                reason = "",
                addedBy = addedBy
            )) {
                is BlacklistApiService.ApiOutcome.Success -> {
                    val remoteId = outcome.value.id?.toString()
                    if (outcome.value.success && remoteId != null) {
                        tagRepository.setRemoteId(localId, remoteId)
                    } else if (!outcome.value.success) {
                        // e.g. "This tag is already blacklisted" on the sheet -- not
                        // an error worth interrupting the user over, just log it.
                        android.util.Log.w(TAG, "Cloud sync skipped: ${outcome.value.message}")
                    }
                }
                is BlacklistApiService.ApiOutcome.Failure -> {
                    android.util.Log.w(TAG, "Cloud sync failed: ${outcome.message}")
                }
            }
        }
    }

    override fun onDestroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        bubbleController?.hide()
        menuController?.dismiss()
        regionSelectController?.dismiss()
        dialogController?.dismiss()
        blacklistWarningController?.dismiss()
        screenCaptureManager?.release()
        ocrProcessor.close()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val TAG = "OverlayBubbleService"
        private const val NOTIFICATION_ID = 4200
        private const val ACTION_CAPTURE_PERMISSION_GRANTED =
            "com.sundaytools.app.action.CAPTURE_PERMISSION_GRANTED"
        private const val ACTION_CAPTURE_PERMISSION_DENIED =
            "com.sundaytools.app.action.CAPTURE_PERMISSION_DENIED"
        private const val ACTION_STOP = "com.sundaytools.app.action.STOP"
        private const val EXTRA_RESULT_CODE = "extra_result_code"
        private const val EXTRA_RESULT_DATA = "extra_result_data"

        fun start(context: Context) {
            val intent = Intent(context, OverlayBubbleService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun onScreenCapturePermissionGranted(context: Context, resultCode: Int, data: Intent) {
            val intent = Intent(context, OverlayBubbleService::class.java)
                .setAction(ACTION_CAPTURE_PERMISSION_GRANTED)
                .putExtra(EXTRA_RESULT_CODE, resultCode)
                .putExtra(EXTRA_RESULT_DATA, data)
            context.startService(intent)
        }

        fun onScreenCapturePermissionDenied(context: Context) {
            val intent = Intent(context, OverlayBubbleService::class.java)
                .setAction(ACTION_CAPTURE_PERMISSION_DENIED)
            context.startService(intent)
        }
    }
}
