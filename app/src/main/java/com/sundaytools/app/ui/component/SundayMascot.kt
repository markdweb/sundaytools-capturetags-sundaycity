package com.sundaytools.app.ui.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * An original mascot for SundayTools -- a friendly round "bubble" character
 * (a nod to the floating scan bubble) with a gentle idle bounce animation.
 * Drawn entirely with Compose Canvas, so no image/GIF assets are needed.
 */
@Composable
fun SundayMascot(modifier: Modifier = Modifier, size: Dp = 96.dp) {
    val infiniteTransition = rememberInfiniteTransition(label = "mascot")
    val bounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val bodyRadius = w * 0.42f
        val centerX = w / 2f
        val centerY = h / 2f + (bounce - 0.5f) * (h * 0.05f)

        // Body
        drawCircle(
            color = Color(0xFF95D5B2),
            radius = bodyRadius,
            center = Offset(centerX, centerY)
        )
        // Face highlight
        drawCircle(
            color = Color(0xFFEFFFF6),
            radius = bodyRadius * 0.82f,
            center = Offset(centerX, centerY + bodyRadius * 0.08f)
        )
        // Eyes
        val eyeOffsetX = bodyRadius * 0.34f
        val eyeY = centerY - bodyRadius * 0.05f
        val eyeRadius = bodyRadius * 0.16f
        drawCircle(Color(0xFF1B4332), eyeRadius, Offset(centerX - eyeOffsetX, eyeY))
        drawCircle(Color(0xFF1B4332), eyeRadius, Offset(centerX + eyeOffsetX, eyeY))
        // Cheeks
        drawCircle(
            color = Color(0xFFE9C46A).copy(alpha = 0.5f),
            radius = bodyRadius * 0.14f,
            center = Offset(centerX - eyeOffsetX * 1.6f, eyeY + bodyRadius * 0.28f)
        )
        drawCircle(
            color = Color(0xFFE9C46A).copy(alpha = 0.5f),
            radius = bodyRadius * 0.14f,
            center = Offset(centerX + eyeOffsetX * 1.6f, eyeY + bodyRadius * 0.28f)
        )
        // Smile
        drawArc(
            color = Color(0xFF1B4332),
            startAngle = 20f,
            sweepAngle = 140f,
            useCenter = false,
            style = Stroke(width = bodyRadius * 0.08f),
            topLeft = Offset(centerX - bodyRadius * 0.32f, eyeY + bodyRadius * 0.12f),
            size = Size(bodyRadius * 0.64f, bodyRadius * 0.5f)
        )
        // Little bubble above the head, nodding to the floating scan bubble
        drawCircle(
            color = Color(0xFFE9C46A),
            radius = bodyRadius * 0.12f,
            center = Offset(centerX, centerY - bodyRadius * 1.05f)
        )
    }
}
