package com.example.aroura.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.aroura.ui.theme.OffWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun BreathingScreen(onClose: () -> Unit) {
    // 1. Fullscreen Background
    // Muted teal, Soft green, Light cyan gradient
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF4DB6AC), // Pastel Teal / Muted Teal
            Color(0xFFA5D6A7), // Soft Green
            Color(0xFFE0F7FA)  // Light Cyan
        )
    )

    var isPlaying by remember { mutableStateOf(true) }
    var isFavorite by remember { mutableStateOf(false) }
    var isSoundOn by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        // 2. Close Button (Top Left)
        Box(
            modifier = Modifier
                .padding(top = 48.dp, start = 24.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.2f))
                .clickable { onClose() }
                .align(Alignment.TopStart),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = OffWhite,
                modifier = Modifier.size(20.dp)
            )
        }

        // 3. Central Breathing Visual (MAIN FOCUS) & 4. Breathing Text
        BreathingAnimation(isPlaying = isPlaying)

        // 5. Bottom Controls (Centered)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Favorite Button
            ControlCircleButton(
                icon = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                isSelected = isFavorite,
                onClick = { isFavorite = !isFavorite }
            )

            // Pause / Play (Center, Larger)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.2f))
                    .clickable { isPlaying = !isPlaying },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) CustomIcons.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = OffWhite,
                    modifier = Modifier.size(40.dp)
                )
            }

            // Sound Toggle
            ControlCircleButton(
                icon = if (isSoundOn) SoundOnIcon() else SoundOffIcon(),
                isSelected = isSoundOn,
                onClick = { isSoundOn = !isSoundOn }
            )
        }
    }
}

@Composable
fun ControlCircleButton(
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.2f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = OffWhite,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun BreathingAnimation(isPlaying: Boolean) {
    // Phase State
    var currentPhaseText by remember { mutableStateOf("Breathe In") }
    
    // Angle: 0f to 360f
    // We treat -90deg (Top) as 0 progress for visual simplicity in logic, or just rotate the canvas.
    // Let's say:
    // Dot 1: Top (Start) -> 0 degrees (visually -90)
    // Dot 2: 120 degrees
    // Dot 3: 240 degrees
    
    val angleAnim = remember { Animatable(0f) }

    // Loop Logic
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (true) {
                // Phase 1: Inhale (Dot 1 -> Dot 2)
                // 0 -> 120
                currentPhaseText = "Breathe In"
                angleAnim.animateTo(
                    targetValue = 120f,
                    animationSpec = tween(durationMillis = 4000, easing = LinearEasing)
                )

                // Phase 2: Hold (Dot 2 -> Dot 3)
                // 120 -> 240
                currentPhaseText = "Hold"
                angleAnim.animateTo(
                    targetValue = 240f,
                    animationSpec = tween(durationMillis = 4000, easing = LinearEasing)
                )

                // Phase 3: Exhale (Dot 3 -> Dot 1)
                // 240 -> 360
                currentPhaseText = "Breathe Out"
                angleAnim.animateTo(
                    targetValue = 360f,
                    animationSpec = tween(durationMillis = 6000, easing = LinearEasing)
                )

                // Reset instantly to 0 to loop
                angleAnim.snapTo(0f)
            }
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        val circleRadius = 120.dp
        
        // Static Circle & Fixed Dots
        Canvas(modifier = Modifier.size(circleRadius * 2)) {
            // 1. Static Ring
            drawCircle(
                color = OffWhite.copy(alpha = 0.3f),
                style = Stroke(width = 2.dp.toPx())
            )
            
            // 2. Fixed Dots (at 0, 120, 240 degrees)
            // 0 degrees is Top (-90 in trig)
            val phases = listOf(0f, 120f, 240f)
            val r = size.minDimension / 2
            
            phases.forEach { deg ->
                val rad = Math.toRadians(deg.toDouble() - 90.0) // Shift -90 to start at top
                val x = center.x + r * cos(rad).toFloat()
                val y = center.y + r * sin(rad).toFloat()
                
                drawCircle(
                    color = OffWhite.copy(alpha = 0.6f),
                    radius = 4.dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }

        // Moving Dot
        Canvas(modifier = Modifier.size(circleRadius * 2)) {
            val r = size.minDimension / 2
            val currentDeg = angleAnim.value - 90f // Shift -90 to start at top
            val rad = Math.toRadians(currentDeg.toDouble())
            
            val x = center.x + r * cos(rad).toFloat()
            val y = center.y + r * sin(rad).toFloat()

            // Glow Effect for moving dot
            drawCircle(
                color = OffWhite.copy(alpha = 0.4f),
                radius = 12.dp.toPx(),
                center = Offset(x, y)
            )
            // Core of moving dot
            drawCircle(
                color = OffWhite,
                radius = 6.dp.toPx(),
                center = Offset(x, y)
            )
        }

        // Center Text
        Text(
            text = currentPhaseText,
            style = MaterialTheme.typography.headlineLarge,
            color = OffWhite,
            fontWeight = FontWeight.Light
        )
    }
}

object CustomIcons {
    val Pause: ImageVector
        get() = ImageVector.Builder(
            name = "Pause",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(6f, 19f)
                horizontalLineTo(10f)
                verticalLineTo(5f)
                horizontalLineTo(6f)
                close()
                moveTo(14f, 5f)
                verticalLineTo(19f)
                horizontalLineTo(18f)
                verticalLineTo(5f)
                close()
            }
        }.build()
}

@Composable
fun SoundOnIcon(): ImageVector = Icons.Default.Notifications
@Composable
fun SoundOffIcon(): ImageVector = Icons.Default.Close // Fallback since NotificationsOff is missing