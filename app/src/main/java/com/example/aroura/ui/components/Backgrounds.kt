package com.example.aroura.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun AdvancedAuroraBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "aurora_advanced")

    // Animation 1: Primary Aurora Curtain (Green/Teal) - Drifts Horizontal
    val curtain1X by infiniteTransition.animateFloat(
        initialValue = -0.2f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "curtain1X"
    )

    // Animation 2: Secondary Aurora Beam (Purple/Blue) - Drifts Vertical + Scale
    val beam2Y by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = -0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "beam2Y"
    )
    val beam2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "beam2Alpha"
    )

    // Animation 3: Deep Glow (Indigo) - Pulses
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    // Aurora Colors
    val deepNight = Color(0xFF0B1026) // Deepest Navy
    val auroraGreen = Color(0xFF00E676)
    val auroraTeal = Color(0xFF1DE9B6)
    val auroraPurple = Color(0xFF651FFF)
    val auroraBlue = Color(0xFF2979FF)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(deepNight)
    ) {
        // We use a single Canvas for performance, drawing multiple layers
        Canvas(modifier = Modifier.fillMaxSize().graphicsLayer { alpha = 0.8f }) {
            val w = size.width
            val h = size.height

            // 1. Deep Indigo/Purple Bottom Glow (Base)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(auroraPurple.copy(alpha = 0.15f), Color.Transparent),
                    center = Offset(w * 0.2f, h * 0.8f),
                    radius = w * 0.8f * glowScale
                )
            )

            // 2. Primary Curtain (Teal/Green) - Top Left to Center
            // Simulated by a large rotated oval gradient
            rotate(degrees = -25f, pivot = Offset(w * 0.5f, h * 0.2f)) {
                drawOval(
                    brush = Brush.linearGradient(
                        colors = listOf(Color.Transparent, auroraTeal.copy(alpha = 0.2f), auroraGreen.copy(alpha = 0.1f), Color.Transparent),
                        start = Offset(w * (0.2f + curtain1X), 0f),
                        end = Offset(w * (0.8f + curtain1X), h * 0.6f)
                    ),
                    topLeft = Offset(w * -0.2f, h * -0.2f),
                    size = Size(w * 1.5f, h * 1.2f)
                )
            }

            // 3. Secondary Beam (Blue/Purple) - Top Right
            rotate(degrees = 15f, pivot = Offset(w * 0.8f, h * 0.3f)) {
                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(auroraBlue.copy(alpha = beam2Alpha * 0.5f), Color.Transparent),
                        center = Offset(w * 0.8f, h * (0.3f + beam2Y)),
                        radius = w * 0.6f
                    ),
                    topLeft = Offset(w * 0.3f, 0f),
                    size = Size(w, h)
                )
            }
            
            // 4. Subtle shimmer overlay
             drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = 0.05f), Color.Transparent),
                    center = Offset(w * 0.5f, h * 0.5f),
                    radius = w * 0.4f
                )
            )
        }
    }
}