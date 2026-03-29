package com.fasttrack.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fasttrack.app.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CircularTimer(
    elapsedMillis: Long,
    targetMillis: Long,
    isRunning: Boolean,
    currentStageName: String,
    currentStageEmoji: String,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 16.dp,
) {
    val progress = if (targetMillis > 0) {
        (elapsedMillis.toFloat() / targetMillis.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 500, easing = EaseInOutCubic),
        label = "progress"
    )

    val gradientStart = if (isDarkTheme) TimerGradientStartDark else TimerGradientStart
    val gradientEnd = if (isDarkTheme) TimerGradientEndDark else TimerGradientEnd
    val trackColor = if (isDarkTheme) TimerTrackDark else TimerTrackLight

    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val elapsedSeconds = (elapsedMillis / 1000).toInt()
    val hours = elapsedSeconds / 3600
    val minutes = (elapsedSeconds % 3600) / 60
    val seconds = elapsedSeconds % 60
    val timeText = String.format("%02d:%02d:%02d", hours, minutes, seconds)

    val targetHours = (targetMillis / 1000 / 3600).toInt()
    val targetMinutes = ((targetMillis / 1000) % 3600 / 60).toInt()
    val targetText = String.format("%02d:%02d:00", targetHours, targetMinutes)

    Box(contentAlignment = Alignment.Center, modifier = modifier.aspectRatio(1f)) {
        Canvas(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            val stroke = strokeWidth.toPx()
            val diameter = size.minDimension - stroke
            val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
            val arcSize = Size(diameter, diameter)

            // Background track
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            // Progress arc with gradient
            if (animatedProgress > 0f) {
                val sweepAngle = animatedProgress * 360f
                val brush = Brush.sweepGradient(
                    colors = listOf(gradientStart, gradientEnd, gradientStart),
                    center = Offset(size.width / 2f, size.height / 2f)
                )
                drawArc(
                    brush = brush,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )

                // Glowing endpoint dot
                if (isRunning) {
                    val angleRad = Math.toRadians((-90.0 + sweepAngle))
                    val radius = diameter / 2f
                    val dotX = (size.width / 2f + radius * cos(angleRad)).toFloat()
                    val dotY = (size.height / 2f + radius * sin(angleRad)).toFloat()
                    drawCircle(color = gradientEnd.copy(alpha = glowAlpha), radius = stroke * 1.5f, center = Offset(dotX, dotY))
                    drawCircle(color = gradientEnd, radius = stroke * 0.6f, center = Offset(dotX, dotY))
                }
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = timeText,
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Light, letterSpacing = 2.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "of $targetText", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (isRunning && currentStageName.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "$currentStageEmoji $currentStageName",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
