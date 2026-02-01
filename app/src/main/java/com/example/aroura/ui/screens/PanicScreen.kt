package com.example.aroura.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.ui.theme.*

/**
 * Panic Screen - Premium Redesign
 * 
 * Features:
 * - Calming dark gradient with subtle pulse
 * - Breathing text animation for "You are safe"
 * - Premium action buttons with scale feedback
 * - Gentle entrance animations
 */
@Composable
fun PanicScreen(onClose: () -> Unit, onNavigateToBreathing: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) { visible = true }
    
    // Breathing animation for the main text
    val infiniteTransition = rememberInfiniteTransition(label = "breathingText")
    val textScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "textScale"
    )
    
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A0A0A),
                        Color(0xFF2D1212),
                        Color(0xFF1A0A0A)
                    )
                )
            )
    ) {
        // Subtle radial glow in center
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFB71C1C).copy(alpha = glowAlpha * 0.4f),
                            Color.Transparent
                        ),
                        radius = 900f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = ArouraSpacing.screenHorizontal.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar with Close
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(300))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = ArouraSpacing.md.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                Color.White.copy(alpha = 0.1f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Close,
                            "Close",
                            tint = OffWhite.copy(alpha = 0.8f)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(0.35f))
            
            // Main Message
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600, delayMillis = 200)) + scaleIn(
                    initialScale = 0.9f,
                    animationSpec = tween(600, delayMillis = 200, easing = EaseOutCubic)
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Breathing circle icon
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .scale(textScale)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        CalmingLavender.copy(alpha = 0.35f),
                                        Color.Transparent
                                    )
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(CalmingLavender.copy(alpha = 0.45f), CircleShape)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
                    
                    Text(
                        text = "You are safe.",
                        style = MaterialTheme.typography.displaySmall,
                        color = OffWhite,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier.scale(textScale)
                    )
                    
                    Spacer(modifier = Modifier.height(ArouraSpacing.md.dp))
                    
                    Text(
                        text = "This feeling will pass.\nFocus on your breathing.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = OffWhite.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Light,
                        lineHeight = 26.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(0.45f))
            
            // Action Buttons
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500, delayMillis = 400)) + slideInVertically(
                    initialOffsetY = { 60 },
                    animationSpec = tween(500, delayMillis = 400, easing = EaseOutCubic)
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(ArouraSpacing.md.dp)
                ) {
                    PremiumPanicButton(
                        text = "Start Breathing Exercise",
                        icon = Icons.Default.Favorite,
                        isPrimary = true,
                        onClick = onNavigateToBreathing
                    )
                    
                    PremiumPanicButton(
                        text = "Call Helpline (1860-266-2345)",
                        icon = Icons.Default.Call,
                        isPrimary = false,
                        onClick = { /* Call action */ }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(ArouraSpacing.xxl.dp))
        }
    }
}

@Composable
private fun PremiumPanicButton(
    text: String,
    icon: ImageVector,
    isPrimary: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "btnScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isPrimary) 72.dp else 60.dp)
            .scale(scale)
            .then(
                if (isPrimary) {
                    Modifier.background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                CalmingLavender,
                                SoftBlue
                            )
                        ),
                        RoundedCornerShape(36.dp)
                    )
                } else {
                    Modifier
                        .background(Color.Transparent, RoundedCornerShape(30.dp))
                        .border(
                            1.5.dp,
                            Color.White.copy(alpha = 0.3f),
                            RoundedCornerShape(30.dp)
                        )
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                null,
                tint = if (isPrimary) MidnightCharcoal else OffWhite,
                modifier = Modifier.size(if (isPrimary) 26.dp else 22.dp)
            )
            Spacer(modifier = Modifier.width(ArouraSpacing.md.dp))
            Text(
                text,
                color = if (isPrimary) MidnightCharcoal else OffWhite,
                fontWeight = FontWeight.SemiBold,
                fontSize = if (isPrimary) 18.sp else 15.sp
            )
        }
    }
}
