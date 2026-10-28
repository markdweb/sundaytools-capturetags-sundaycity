package com.sundaytools.app.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.sundaytools.app.ui.theme.SundayToolsTheme

/**
 * Small popup shown right next to the bubble when it's tapped, giving the
 * user an explicit "Scan Tag" button instead of capturing immediately on
 * tap, plus a "Set Capture Area" shortcut to restrict OCR to a specific
 * region of the screen (so it doesn't pick up timers/currency/chat text
 * that happens to look like a tag).
 *
 * The screen-capture permission (MediaProjection "Entire Screen" prompt) is
 * still only requested once per bubble session -- this popup does not add
 * any extra permission step, it just adds a confirm button before the
 * existing capture flow runs.
 */
class BubbleMenuController(
    private val context: Context,
    private val windowManager: WindowManager,
    lifecycleOwner: LifecycleOwner,
    viewModelStoreOwner: ViewModelStoreOwner,
    savedStateRegistryOwner: SavedStateRegistryOwner,
    anchorX: Int,
    anchorY: Int,
    bubbleSizePx: Int,
    private val onScanTag: () -> Unit,
    private val onSetCaptureArea: () -> Unit,
    private val darkTheme: Boolean
) {
    private val layoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        BubbleViewController.overlayWindowType(),
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT
    ).apply {
        gravity = Gravity.TOP or Gravity.START
        // Anchor just below-right of the bubble; clamp to the visible screen
        // width so the popup never renders partly off-screen.
        val metrics = context.resources.displayMetrics
        val estimatedWidthPx = (220 * metrics.density).toInt()
        x = anchorX.coerceAtMost(metrics.widthPixels - estimatedWidthPx).coerceAtLeast(0)
        y = anchorY + bubbleSizePx + (8 * metrics.density).toInt()
    }

    private val composeView = ComposeView(context).apply {
        setViewTreeLifecycleOwner(lifecycleOwner)
        setViewTreeViewModelStoreOwner(viewModelStoreOwner)
        setViewTreeSavedStateRegistryOwner(savedStateRegistryOwner)
        setContent {
            SundayToolsTheme(darkTheme = darkTheme) {
                BubbleMenuContent(onScanTag = onScanTag, onSetCaptureArea = onSetCaptureArea)
            }
        }
    }

    fun show() {
        runCatching { windowManager.addView(composeView, layoutParams) }
    }

    fun dismiss() {
        runCatching { windowManager.removeView(composeView) }
    }
}

@Composable
private fun BubbleMenuContent(onScanTag: () -> Unit, onSetCaptureArea: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Button(
                onClick = onScanTag,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = Icons.Filled.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 6.dp)
                )
                Text("Scan Tag")
            }

            OutlinedButton(
                onClick = onSetCaptureArea,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.CropFree,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 6.dp)
                )
                Text("Set Capture Area")
            }
        }
    }
}
