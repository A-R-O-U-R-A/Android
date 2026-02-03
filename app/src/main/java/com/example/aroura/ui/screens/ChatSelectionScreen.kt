package com.example.aroura.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.ui.components.ArouraProfileIcon
import com.example.aroura.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * Chat Selection Screen - Premium Redesign
 * 
 * Features:
 * - Calm, spacious layout
 * - Subtle floating animations on cards
 * - Premium glass-morphism cards
 * - Gentle visual hierarchy
 */
@Composable
fun ChatSelectionScreen(
    onChatSelected: (String) -> Unit, 
    onProfileClick: () -> Unit,
    profilePictureUrl: String? = null
) {
    // Staggered entrance animations
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = ArouraSpacing.screenHorizontal.dp)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))

        // Header with Profile
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ArouraProfileIcon(
                onClick = onProfileClick,
                profilePictureUrl = profilePictureUrl
            )
            
            // Subtle mode indicator
            Surface(
                color = DeepSurface.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Chat",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextDarkSecondary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(ArouraSpacing.xxl.dp))

        // Title with animation
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(600)) + slideInVertically(
                initialOffsetY = { 20 },
                animationSpec = tween(600, easing = EaseOutCubic)
            )
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Who would you like",
                    style = MaterialTheme.typography.headlineSmall,
                    color = OffWhite,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "to talk to?",
                    style = MaterialTheme.typography.headlineSmall,
                    color = OffWhite,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))

        // Cards Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(ArouraSpacing.md.dp)
        ) {
            // Counselor Card
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600, delayMillis = 150)) + slideInVertically(
                    initialOffsetY = { 40 },
                    animationSpec = tween(600, delayMillis = 150, easing = EaseOutCubic)
                ),
                modifier = Modifier.weight(1f)
            ) {
                PremiumChatOptionCard(
                    title = "Counselor",
                    subtitle = "Professional Support",
                    description = "A gentle guide for\nemotional wellbeing",
                    accentColor = SoftBlue,
                    onClick = { onChatSelected("Counselor") }
                ) {
                    MoonVisualPremium()
                }
            }

            // Companion Card
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600, delayMillis = 250)) + slideInVertically(
                    initialOffsetY = { 40 },
                    animationSpec = tween(600, delayMillis = 250, easing = EaseOutCubic)
                ),
                modifier = Modifier.weight(1f)
            ) {
                PremiumChatOptionCard(
                    title = "Companion",
                    subtitle = "Friendly Chat",
                    description = "A caring friend\nwho listens",
                    accentColor = CalmingPeach,
                    onClick = { onChatSelected("Companion") }
                ) {
                    StarVisualPremium()
                }
            }
        }
        
        Spacer(modifier = Modifier.height(120.dp))
    }
}

@Composable
private fun PremiumChatOptionCard(
    title: String,
    subtitle: String,
    description: String,
    accentColor: Color,
    onClick: () -> Unit,
    visualContent: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "cardScale"
    )
    
    // Subtle floating animation
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxHeight(0.75f)
            .scale(scale)
            .offset(y = floatOffset.dp)
            .clip(RoundedCornerShape(ArouraSpacing.cardRadius.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.08f),
                        DeepSurface.copy(alpha = 0.6f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.3f),
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(ArouraSpacing.cardRadius.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(ArouraSpacing.lg.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = subtitle.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = OffWhite,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Visual
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                visualContent()
            }

            // Description
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TextDarkSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            // Button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                color = accentColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Start Chat",
                        style = MaterialTheme.typography.labelLarge,
                        color = accentColor,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MoonVisualPremium() {
    val infiniteTransition = rememberInfiniteTransition(label = "moonGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )
    
    Canvas(modifier = Modifier.size(80.dp)) {
        // Outer Glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    SoftBlue.copy(alpha = glowAlpha),
                    Color.Transparent
                )
            ),
            radius = size.width / 1.3f
        )
        // Moon Body
        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFE3F2FD),
                    SoftBlue
                ),
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height)
            ),
            radius = size.width / 3f
        )
        // Sparkles
        drawCircle(color = Color.White.copy(alpha = 0.8f), radius = 3f, center = Offset(12f, 18f))
        drawCircle(color = Color.White.copy(alpha = 0.6f), radius = 2f, center = Offset(70f, 65f))
        drawCircle(color = Color.White.copy(alpha = 0.4f), radius = 2f, center = Offset(55f, 20f))
    }
}

@Composable
private fun StarVisualPremium() {
    val infiniteTransition = rememberInfiniteTransition(label = "starGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "starGlowAlpha"
    )
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "starRotation"
    )

    Canvas(modifier = Modifier.size(80.dp)) {
        // Glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    CalmingPeach.copy(alpha = glowAlpha),
                    Color.Transparent
                )
            ),
            radius = size.width / 1.3f
        )

        val cx = size.width / 2
        val cy = size.height / 2
        val outerRadius = size.width / 2.8f
        val innerRadius = outerRadius / 2.2f
        
        val starPath = Path().apply {
            var angle = -Math.PI / 2 + Math.toRadians(rotation.toDouble())
            val step = Math.PI / 5
            
            moveTo(
                (cx + outerRadius * cos(angle)).toFloat(),
                (cy + outerRadius * sin(angle)).toFloat()
            )
            
            for (i in 1..5) {
                angle += step
                lineTo(
                    (cx + innerRadius * cos(angle)).toFloat(),
                    (cy + innerRadius * sin(angle)).toFloat()
                )
                angle += step
                lineTo(
                    (cx + outerRadius * cos(angle)).toFloat(),
                    (cy + outerRadius * sin(angle)).toFloat()
                )
            }
            close()
        }

        drawPath(
            path = starPath,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFFFF8E1),
                    CalmingPeach
                ),
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height)
            )
        )
    }
}

// Keep legacy functions for compatibility but mark private
@Composable
private fun MoonVisual() = MoonVisualPremium()

@Composable
private fun StarVisual() = StarVisualPremium()