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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.math.cos
import kotlin.math.sin

/**
 * A.R.O.U.R.A Premium Aurora Background
 * 
 * Design Philosophy:
 * - Slow, intentional motion (45-60 second cycles)
 * - Layered depth with aurora bands
 * - Subtle breathing effect
 * - Calming, not distracting
 * - GPU-friendly implementation
 * 
 * OPTIMIZED: Reduced from 10 separate animations to 3 master animations
 */
@Composable
fun ArouraBackground(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "aurora_premium")
    
    // ═══════════════════════════════════════════════════════════════════════════
    // MASTER ANIMATION 1: Primary drift (controls X/Y movement)
    // ═══════════════════════════════════════════════════════════════════════════
    
    val masterDrift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(45000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "masterDrift"
    )
    
    // ═══════════════════════════════════════════════════════════════════════════
    // MASTER ANIMATION 2: Secondary rotation/alpha
    // ═══════════════════════════════════════════════════════════════════════════
    
    val masterAlpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "masterAlpha"
    )
    
    // ═══════════════════════════════════════════════════════════════════════════
    // MASTER ANIMATION 3: Glow scale/breathe
    // ═══════════════════════════════════════════════════════════════════════════
    
    val masterGlow by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(60000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "masterGlow"
    )
    
    // Derived values from master animations (no additional animations needed)
    val drift1X = -0.15f + masterDrift * 0.3f
    val drift1Y = 0.05f - masterDrift * 0.1f
    val drift2Rotation = -5f + masterAlpha * 10f
    val drift2Alpha = 0.15f + masterAlpha * 0.2f
    val glowScale = 0.8f + masterGlow * 0.4f
    val glowAlpha = 0.1f + masterGlow * 0.15f
    val particle1Y = masterAlpha * 20f
    val particle2Y = 10f - masterDrift * 20f

    // Color Palette
    val deepNight = Color(0xFF080A10)
    val auroraGreen = Color(0xFF00E676)
    val auroraTeal = Color(0xFF1DE9B6)
    val auroraPurple = Color(0xFF651FFF)
    val auroraBlue = Color(0xFF2979FF)
    val auroraIndigo = Color(0xFF3949AB)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(deepNight)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = 0.95f }
        ) {
            val w = size.width
            val h = size.height
            val centerX = w / 2
            val centerY = h / 2

            // ═══════════════════════════════════════════════════════════════════
            // BASE LAYER: Deep indigo/purple radial glow (bottom)
            // ═══════════════════════════════════════════════════════════════════
            
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        auroraIndigo.copy(alpha = glowAlpha * 0.8f),
                        auroraPurple.copy(alpha = glowAlpha * 0.4f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.3f, h * 0.85f),
                    radius = w * 1.2f * glowScale
                )
            )

            // ═══════════════════════════════════════════════════════════════════
            // PRIMARY AURORA BAND: Teal/Green curtain (top-left to center)
            // ═══════════════════════════════════════════════════════════════════
            
            rotate(
                degrees = -20f + drift2Rotation,
                pivot = Offset(centerX, h * 0.3f)
            ) {
                // Outer glow
                drawOval(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            auroraTeal.copy(alpha = 0.08f),
                            auroraGreen.copy(alpha = drift2Alpha * 0.5f),
                            auroraTeal.copy(alpha = 0.1f),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = h * 0.8f
                    ),
                    topLeft = Offset(
                        w * (-0.3f + drift1X),
                        h * (-0.1f + drift1Y)
                    ),
                    size = Size(w * 1.8f, h * 0.9f)
                )
                
                // Inner bright band
                drawOval(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            auroraGreen.copy(alpha = 0.12f),
                            auroraTeal.copy(alpha = 0.2f),
                            Color.Transparent
                        ),
                        startY = h * 0.1f,
                        endY = h * 0.5f
                    ),
                    topLeft = Offset(
                        w * (0.1f + drift1X * 0.5f),
                        h * (0.05f + drift1Y * 0.5f)
                    ),
                    size = Size(w * 0.8f, h * 0.35f)
                )
            }

            // ═══════════════════════════════════════════════════════════════════
            // SECONDARY AURORA BAND: Blue/Purple beam (top-right)
            // ═══════════════════════════════════════════════════════════════════
            
            rotate(
                degrees = 15f - drift2Rotation * 0.5f,
                pivot = Offset(w * 0.8f, h * 0.25f)
            ) {
                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            auroraBlue.copy(alpha = drift2Alpha * 0.6f),
                            auroraPurple.copy(alpha = drift2Alpha * 0.3f),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.75f, h * 0.2f),
                        radius = w * 0.5f
                    ),
                    topLeft = Offset(w * 0.4f, h * -0.1f),
                    size = Size(w * 0.8f, h * 0.6f)
                )
            }

            // ═══════════════════════════════════════════════════════════════════
            // ACCENT GLOW: Center breathing glow
            // ═══════════════════════════════════════════════════════════════════
            
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        auroraTeal.copy(alpha = glowAlpha * 0.4f),
                        auroraGreen.copy(alpha = glowAlpha * 0.2f),
                        Color.Transparent
                    ),
                    center = Offset(centerX + w * drift1X * 0.3f, h * 0.35f),
                    radius = w * 0.35f * glowScale
                )
            )

            // ═══════════════════════════════════════════════════════════════════
            // SHIMMER PARTICLES: Subtle floating light points
            // ═══════════════════════════════════════════════════════════════════
            
            // Particle 1 - Top left
            drawCircle(
                color = Color.White.copy(alpha = 0.15f),
                radius = 3f,
                center = Offset(w * 0.15f, h * 0.12f + particle1Y)
            )
            
            // Particle 2 - Top right
            drawCircle(
                color = Color.White.copy(alpha = 0.12f),
                radius = 2.5f,
                center = Offset(w * 0.85f, h * 0.18f + particle2Y)
            )
            
            // Particle 3 - Center
            drawCircle(
                color = Color.White.copy(alpha = 0.08f),
                radius = 4f,
                center = Offset(w * 0.5f, h * 0.25f + particle1Y * 0.5f)
            )
            
            // Particle 4 - Left
            drawCircle(
                color = auroraTeal.copy(alpha = 0.1f),
                radius = 5f,
                center = Offset(w * 0.25f, h * 0.4f + particle2Y * 0.7f)
            )

            // ═══════════════════════════════════════════════════════════════════
            // TOP EDGE HIGHLIGHT: Subtle aurora edge
            // ═══════════════════════════════════════════════════════════════════
            
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        auroraGreen.copy(alpha = 0.06f),
                        auroraTeal.copy(alpha = 0.03f),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = h * 0.15f
                ),
                topLeft = Offset.Zero,
                size = Size(w, h * 0.15f)
            )
        }
    }
}

/**
 * Simplified background for secondary screens
 * Less motion, still premium feel
 */
@Composable
fun ArouraBackgroundSimple(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "aurora_simple")
    
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "simpleGlowAlpha"
    )

    val deepNight = Color(0xFF080A10)
    val auroraTeal = Color(0xFF1DE9B6)
    val auroraPurple = Color(0xFF651FFF)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(deepNight)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Single subtle glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        auroraTeal.copy(alpha = glowAlpha),
                        auroraPurple.copy(alpha = glowAlpha * 0.5f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.3f, h * 0.2f),
                    radius = w * 0.8f
                )
            )
            
            // Bottom accent
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        auroraPurple.copy(alpha = glowAlpha * 0.6f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.7f, h * 0.9f),
                    radius = w * 0.5f
                )
            )
        }
    }
}

/**
 * Legacy wrapper for compatibility
 * Maps to new premium background
 */
@Composable
fun AdvancedAuroraBackground() {
    ArouraBackground()
}