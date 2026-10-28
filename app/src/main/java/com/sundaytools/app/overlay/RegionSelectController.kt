package com.sundaytools.app.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.view.WindowManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
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
 * Full-screen overlay that lets the user mark a rectangular "capture area" by
 * tapping its top-left corner, then its bottom-right corner, over the actual
 * game underneath. OCR is then restricted to that region on every future
 * scan instead of scanning the whole screen, which avoids picking up
 * unrelated on-screen text (timers, currency, chat) that happens to match
 * the tag pattern.
 */
class RegionSelectController(
    context: Context,
    private val windowManager: WindowManager,
    lifecycleOwner: LifecycleOwner,
    viewModelStoreOwner: ViewModelStoreOwner,
    savedStateRegistryOwner: SavedStateRegistryOwner,
    onSave: (leftFrac: Float, topFrac: Float, rightFrac: Float, bottomFrac: Float) -> Unit,
    onCancel: () -> Unit,
    private val darkTheme: Boolean
) {
    private val layoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT,
        BubbleViewController.overlayWindowType(),
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT
    )

    private val composeView = ComposeView(context).apply {
        setViewTreeLifecycleOwner(lifecycleOwner)
        setViewTreeViewModelStoreOwner(viewModelStoreOwner)
        setViewTreeSavedStateRegistryOwner(savedStateRegistryOwner)
        setContent {
            SundayToolsTheme(darkTheme = darkTheme) {
                RegionSelectContent(
                    onCancel = { dismiss(); onCancel() },
                    onSaveComplete = { l, t, r, b -> dismiss(); onSave(l, t, r, b) }
                )
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
private fun RegionSelectContent(
    onCancel: () -> Unit,
    onSaveComplete: (Float, Float, Float, Float) -> Unit
) {
    var firstPoint by remember { mutableStateOf<Offset?>(null) }
    var secondPoint by remember { mutableStateOf<Offset?>(null) }
    var canvasWidth by remember { mutableStateOf(1f) }
    var canvasHeight by remember { mutableStateOf(1f) }

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        if (firstPoint == null) {
                            firstPoint = tapOffset
                        } else {
                            secondPoint = tapOffset
                        }
                    }
                }
        ) {
            canvasWidth = size.width
            canvasHeight = size.height
            val fp = firstPoint
            val sp = secondPoint
            if (fp != null) {
                drawCircle(color = Color(0xFFE94560), radius = 14f, center = fp)
            }
            if (fp != null && sp != null) {
                drawCircle(color = Color(0xFFE94560), radius = 14f, center = sp)
                val left = minOf(fp.x, sp.x)
                val top = minOf(fp.y, sp.y)
                val right = maxOf(fp.x, sp.x)
                val bottom = maxOf(fp.y, sp.y)
                drawRect(
                    color = Color(0xFFE94560),
                    topLeft = Offset(left, top),
                    size = Size(right - left, bottom - top),
                    style = Stroke(width = 4f)
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp, start = 16.dp, end = 16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Text(
                    text = when {
                        firstPoint == null -> "Tap the TOP-LEFT corner of where the player tag shows up"
                        secondPoint == null -> "Now tap the BOTTOM-RIGHT corner"
                        else -> "Area marked. Tap Save to confirm, or Reset to try again."
                    },
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
                Text("Cancel")
            }
            OutlinedButton(
                onClick = { firstPoint = null; secondPoint = null },
                modifier = Modifier.weight(1f)
            ) {
                Text("Reset")
            }
            Button(
                onClick = {
                    val fp = firstPoint
                    val sp = secondPoint
                    if (fp != null && sp != null && canvasWidth > 0 && canvasHeight > 0) {
                        val left = minOf(fp.x, sp.x) / canvasWidth
                        val top = minOf(fp.y, sp.y) / canvasHeight
                        val right = maxOf(fp.x, sp.x) / canvasWidth
                        val bottom = maxOf(fp.y, sp.y) / canvasHeight
                        onSaveComplete(left, top, right, bottom)
                    }
                },
                enabled = firstPoint != null && secondPoint != null,
                modifier = Modifier.weight(1f)
            ) {
                Text("Save")
            }
        }
    }
}
