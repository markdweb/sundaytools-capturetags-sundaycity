package com.sundaytools.app.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
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
 * Messenger-style "drag here to close" target that appears at the bottom
 * center of the screen while the bubble is being dragged. Dropping the
 * bubble on top of it stops the floating bubble entirely, same idea as
 * dragging a Messenger chat head down to the "X" to remove it.
 */
class BubbleRemoveTargetController(
    private val context: Context,
    private val windowManager: WindowManager,
    lifecycleOwner: LifecycleOwner,
    viewModelStoreOwner: ViewModelStoreOwner,
    savedStateRegistryOwner: SavedStateRegistryOwner,
    private val darkTheme: Boolean
) {
    private val sizePx = (72 * context.resources.displayMetrics.density).toInt()
    private val bottomMarginPx = (32 * context.resources.displayMetrics.density).toInt()

    private val layoutParams = WindowManager.LayoutParams(
        sizePx,
        sizePx,
        BubbleViewController.overlayWindowType(),
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        PixelFormat.TRANSLUCENT
    ).apply {
        gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        y = bottomMarginPx
    }

    private val highlightedState = mutableStateOf(false)

    private val composeView = ComposeView(context).apply {
        setViewTreeLifecycleOwner(lifecycleOwner)
        setViewTreeViewModelStoreOwner(viewModelStoreOwner)
        setViewTreeSavedStateRegistryOwner(savedStateRegistryOwner)
        setContent {
            SundayToolsTheme(darkTheme = darkTheme) {
                RemoveTargetContent(highlighted = highlightedState.value)
            }
        }
    }

    private var isShown = false

    fun show() {
        if (isShown) return
        isShown = true
        runCatching { windowManager.addView(composeView, layoutParams) }
    }

    fun hide() {
        if (!isShown) return
        isShown = false
        highlightedState.value = false
        runCatching { windowManager.removeView(composeView) }
    }

    fun setHighlighted(highlighted: Boolean) {
        highlightedState.value = highlighted
    }

    /** Approximate center of the target in absolute screen pixel coordinates. */
    fun centerXY(): Pair<Int, Int> {
        val metrics = context.resources.displayMetrics
        val centerX = metrics.widthPixels / 2
        val centerY = metrics.heightPixels - bottomMarginPx - sizePx / 2
        return centerX to centerY
    }

    /** Distance from the target center within which a drop counts as "on target". */
    fun hitRadiusPx(): Int = sizePx
}

@Composable
private fun RemoveTargetContent(highlighted: Boolean) {
    val scale = if (highlighted) 1.2f else 1f
    Box(
        modifier = Modifier
            .size(72.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(if (highlighted) Color(0xFFE94560) else Color(0x99000000)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = "Drag here to close the bubble",
            tint = Color.White
        )
    }
}
