package com.sundaytools.app.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import kotlin.math.abs

/**
 * Owns the WindowManager LayoutParams and drag gesture handling for the
 * floating bubble. Kept separate from [OverlayBubbleService] so the service
 * stays focused on capture/OCR orchestration.
 */
class BubbleViewController(
    private val context: Context,
    private val windowManager: WindowManager,
    private val lifecycleOwner: LifecycleOwner,
    private val viewModelStoreOwner: ViewModelStoreOwner,
    private val savedStateRegistryOwner: SavedStateRegistryOwner,
    private val darkTheme: Boolean,
    private val onTap: () -> Unit,
    private val onRequestRemove: () -> Unit
) {
    private val bubbleSizePx = (56 * context.resources.displayMetrics.density).toInt()

    private val removeTarget by lazy {
        BubbleRemoveTargetController(
            context = context,
            windowManager = windowManager,
            lifecycleOwner = lifecycleOwner,
            viewModelStoreOwner = viewModelStoreOwner,
            savedStateRegistryOwner = savedStateRegistryOwner,
            darkTheme = darkTheme
        )
    }

    private val layoutParams = WindowManager.LayoutParams(
        bubbleSizePx,
        bubbleSizePx,
        overlayWindowType(),
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT
    ).apply {
        gravity = Gravity.TOP or Gravity.START
        x = 0
        y = 300
    }

    private val isBusy = mutableStateOf(false)

    private val composeView = ComposeView(context).apply {
        setViewTreeLifecycleOwner(lifecycleOwner)
        setViewTreeViewModelStoreOwner(viewModelStoreOwner)
        setViewTreeSavedStateRegistryOwner(savedStateRegistryOwner)
        setContent {
            SundayToolsTheme(darkTheme = darkTheme) {
                BubbleContent(isBusy.value)
            }
        }
    }

    private var downRawX = 0f
    private var downRawY = 0f
    private var downParamX = 0
    private var downParamY = 0
    private var isDragging = false

    init {
        composeView.setOnTouchListener { _, event ->
            handleTouch(event)
        }
    }

    private fun handleTouch(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downRawX = event.rawX
                downRawY = event.rawY
                downParamX = layoutParams.x
                downParamY = layoutParams.y
                isDragging = false
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = event.rawX - downRawX
                val deltaY = event.rawY - downRawY
                if (abs(deltaX) > TOUCH_SLOP || abs(deltaY) > TOUCH_SLOP) {
                    if (!isDragging) {
                        isDragging = true
                        removeTarget.show()
                    }
                }
                if (isDragging) {
                    layoutParams.x = downParamX + deltaX.toInt()
                    layoutParams.y = downParamY + deltaY.toInt()
                    runCatching { windowManager.updateViewLayout(composeView, layoutParams) }
                    removeTarget.setHighlighted(isOverRemoveTarget())
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                if (isDragging) {
                    val droppedOnTarget = isOverRemoveTarget()
                    removeTarget.hide()
                    if (droppedOnTarget) {
                        onRequestRemove()
                    }
                } else {
                    onTap()
                }
                return true
            }
        }
        return false
    }

    fun show() {
        runCatching { windowManager.addView(composeView, layoutParams) }
    }

    fun hide() {
        runCatching { windowManager.removeView(composeView) }
        removeTarget.hide()
    }

    fun setBusy(busy: Boolean) {
        isBusy.value = busy
    }

    /** Current on-screen position of the bubble, used to anchor the scan-tag popup menu. */
    fun currentPosition(): Pair<Int, Int> = layoutParams.x to layoutParams.y

    fun bubbleSize(): Int = bubbleSizePx

    /** Whether the bubble's current center is close enough to the remove target to count as a drop. */
    private fun isOverRemoveTarget(): Boolean {
        val bubbleCenterX = layoutParams.x + bubbleSizePx / 2
        val bubbleCenterY = layoutParams.y + bubbleSizePx / 2
        val (targetX, targetY) = removeTarget.centerXY()
        val dx = (bubbleCenterX - targetX).toDouble()
        val dy = (bubbleCenterY - targetY).toDouble()
        val distance = kotlin.math.sqrt(dx * dx + dy * dy)
        return distance <= removeTarget.hitRadiusPx()
    }

    companion object {
        private const val TOUCH_SLOP = 12

        fun overlayWindowType(): Int =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            }
    }
}

@androidx.compose.runtime.Composable
private fun BubbleContent(busy: Boolean) {
    val scale by animateFloatAsState(if (busy) 0.85f else 1f, tween(150), label = "bubbleScale")
    Box(
        modifier = Modifier
            .size(56.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        if (busy) {
            CircularProgressIndicator(
                modifier = Modifier.size(28.dp),
                color = Color.White,
                strokeWidth = 3.dp
            )
        } else {
            Icon(
                imageVector = Icons.Filled.CameraAlt,
                contentDescription = "Capture player tag",
                tint = Color.White
            )
        }
    }
}
