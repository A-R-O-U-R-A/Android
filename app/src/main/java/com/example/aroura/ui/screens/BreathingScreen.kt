package com.example.aroura.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import com.example.aroura.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

/**
 * Breathing Screen - Premium Redesign
 * 
 * Features:
 * - Triangular breathing with smooth dot movement
 * - Entrance animations for controls
 * - Premium control buttons with scale feedback
 * - Breathing glow effect
 */
@Composable
fun BreathingScreen(onClose: () -> Unit) {
    // Premium calming gradient
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF2A4A4F), // Deep teal
            Color(0xFF3D6B6B), // Muted teal
            Color(0xFF4A8080)  // Lighter teal
        )
    )

    var isPlaying by remember { mutableStateOf(true) }
    var isFavorite by remember { mutableStateOf(false) }
    var isSoundOn by remember { mutableStateOf(true) }
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        // Subtle radial glow overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MutedTeal.copy(alpha = 0.15f),
                            Color.Transparent
                        ),
                        radius = 600f
                    )
                )
        )
        
        // Close Button with entrance animation
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(300)),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 48.dp, start = ArouraSpacing.screenHorizontal.dp)
        ) {
            PremiumControlButton(
                icon = Icons.Default.Close,
                isSelected = false,
                size = 44.dp,
                onClick = onClose
            )
        }

        // Central Breathing Visual
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(500, delayMillis = 100)) + scaleIn(
                initialScale = 0.9f,
                animationSpec = tween(500, delayMillis = 100, easing = EaseOutCubic)
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            BreathingAnimation(isPlaying = isPlaying)
        }

        // Bottom Controls with staggered entrance
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(400, delayMillis = 300)) + slideInVertically(
                initialOffsetY = { 50 },
                animationSpec = tween(400, delayMillis = 300, easing = EaseOutCubic)
            ),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Row(
                modifier = Modifier.padding(bottom = 60.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ArouraSpacing.xl.dp)
            ) {
                // Favorite Button
                PremiumControlButton(
                    icon = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    isSelected = isFavorite,
                    onClick = { isFavorite = !isFavorite }
                )

                // Play/Pause (Center, Larger)
                PremiumControlButton(
                    icon = if (isPlaying) CustomIcons.Pause else Icons.Default.PlayArrow,
                    isSelected = false,
                    size = 80.dp,
                    iconSize = 40.dp,
                    onClick = { isPlaying = !isPlaying }
                )

                // Sound Toggle
                PremiumControlButton(
                    icon = if (isSoundOn) SoundOnIcon() else SoundOffIcon(),
                    isSelected = isSoundOn,
                    onClick = { isSoundOn = !isSoundOn }
                )
            }
        }
    }
}

@Composable
private fun PremiumControlButton(
    icon: ImageVector,
    isSelected: Boolean,
    size: androidx.compose.ui.unit.Dp = 56.dp,
    iconSize: androidx.compose.ui.unit.Dp = 24.dp,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "controlScale"
    )
    
    val bgAlpha by animateFloatAsState(
        targetValue = if (isSelected) 0.35f else 0.2f,
        animationSpec = tween(200),
        label = "bgAlpha"
    )

    Box(
        modifier = Modifier
            .size(size)
            .scale(scale)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = bgAlpha))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = OffWhite,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Composable
fun BreathingAnimation(isPlaying: Boolean) {
    var currentPhaseText by remember { mutableStateOf("Breathe In") }
    val angleAnim = remember { Animatable(0f) }

    // Breathing glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "breathingGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // Animation Loop
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (true) {
                // Phase 1: Inhale (0 -> 120)
                currentPhaseText = "Breathe In"
                angleAnim.animateTo(
                    targetValue = 120f,
                    animationSpec = tween(durationMillis = 4000, easing = LinearEasing)
                )

                // Phase 2: Hold (120 -> 240)
                currentPhaseText = "Hold"
                angleAnim.animateTo(
                    targetValue = 240f,
                    animationSpec = tween(durationMillis = 4000, easing = LinearEasing)
                )

                // Phase 3: Exhale (240 -> 360)
                currentPhaseText = "Breathe Out"
                angleAnim.animateTo(
                    targetValue = 360f,
                    animationSpec = tween(durationMillis = 6000, easing = LinearEasing)
                )

                // Reset
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
            // Outer glow ring
            drawCircle(
                color = OffWhite.copy(alpha = glowAlpha * 0.3f),
                style = Stroke(width = 6.dp.toPx())
            )
            
            // Static Ring
            drawCircle(
                color = OffWhite.copy(alpha = 0.35f),
                style = Stroke(width = 2.dp.toPx())
            )
            
            // Fixed Dots at 0, 120, 240 degrees
            val phases = listOf(0f, 120f, 240f)
            val r = size.minDimension / 2
            
            phases.forEach { deg ->
                val rad = Math.toRadians(deg.toDouble() - 90.0)
                val x = center.x + r * cos(rad).toFloat()
                val y = center.y + r * sin(rad).toFloat()
                
                // Dot glow
                drawCircle(
                    color = OffWhite.copy(alpha = 0.25f),
                    radius = 8.dp.toPx(),
                    center = Offset(x, y)
                )
                // Dot core
                drawCircle(
                    color = OffWhite.copy(alpha = 0.7f),
                    radius = 5.dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }

        // Moving Dot with glow
        Canvas(modifier = Modifier.size(circleRadius * 2)) {
            val r = size.minDimension / 2
            val currentDeg = angleAnim.value - 90f
            val rad = Math.toRadians(currentDeg.toDouble())
            
            val x = center.x + r * cos(rad).toFloat()
            val y = center.y + r * sin(rad).toFloat()

            // Outer glow
            drawCircle(
                color = OffWhite.copy(alpha = glowAlpha),
                radius = 18.dp.toPx(),
                center = Offset(x, y)
            )
            // Inner glow
            drawCircle(
                color = OffWhite.copy(alpha = 0.5f),
                radius = 12.dp.toPx(),
                center = Offset(x, y)
            )
            // Core
            drawCircle(
                color = OffWhite,
                radius = 7.dp.toPx(),
                center = Offset(x, y)
            )
        }

        // Center Text with crossfade
        AnimatedContent(
            targetState = currentPhaseText,
            transitionSpec = {
                fadeIn(tween(300)).togetherWith(fadeOut(tween(200)))
            },
            label = "phaseText"
        ) { phase ->
            Text(
                text = phase,
                style = MaterialTheme.typography.headlineLarge,
                color = OffWhite,
                fontWeight = FontWeight.Light
            )
        }
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
fun SoundOffIcon(): ImageVector = Icons.Default.Close