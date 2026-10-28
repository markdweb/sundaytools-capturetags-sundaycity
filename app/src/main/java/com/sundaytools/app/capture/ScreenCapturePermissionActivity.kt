package com.sundaytools.app.capture

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import com.sundaytools.app.overlay.OverlayBubbleService

/**
 * Invisible activity whose sole purpose is to call
 * [MediaProjectionManager.createScreenCaptureIntent] and hand the resulting
 * permission grant back to [OverlayBubbleService]. The MediaProjection API
 * requires an Activity to launch this system permission dialog; a Service
 * cannot request it directly.
 */
class ScreenCapturePermissionActivity : Activity() {

    private lateinit var projectionManager: MediaProjectionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        projectionManager =
            getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(projectionManager.createScreenCaptureIntent(), REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                OverlayBubbleService.onScreenCapturePermissionGranted(this, resultCode, data)
            } else {
                OverlayBubbleService.onScreenCapturePermissionDenied(this)
            }
        }
        finish()
    }

    companion object {
        private const val REQUEST_CODE = 4201
    }
}
