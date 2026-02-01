package com.example.aroura.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.ui.components.ArouraBackground
import com.example.aroura.ui.theme.*

/**
 * Grounding Screen - Premium Redesign
 * 
 * Features:
 * - Animated step counter with scale transitions
 * - Breathing glow effect on number
 * - Smooth step transitions
 * - Premium action button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroundingScreen(onBack: () -> Unit) {
    var currentStep by remember { mutableIntStateOf(5) }
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) { visible = true }
    
    val steps = mapOf(
        5 to "Acknowledge 5 things you see around you.",
        4 to "Acknowledge 4 things you can touch.",
        3 to "Acknowledge 3 things you hear.",
        2 to "Acknowledge 2 things you can smell.",
        1 to "Acknowledge 1 thing you can taste."
    )
    
    // Breathing glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "numberGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )
    
    // Number scale animation on change
    val numberScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "numberScale"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        ArouraBackground()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = ArouraSpacing.screenHorizontal.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = ArouraSpacing.md.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = OffWhite)
                }
                
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(400)) + slideInHorizontally(
                        initialOffsetX = { -20 },
                        animationSpec = tween(400)
                    )
                ) {
                    Text(
                        "Ground Yourself",
                        style = MaterialTheme.typography.titleLarge,
                        color = OffWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
            
            // Title
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(400, delayMillis = 100))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "5-4-3-2-1 Technique",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MutedTeal,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(ArouraSpacing.sm.dp))
                    
                    Text(
                        text = "Use your senses to bring yourself\nback to the present moment.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextDarkSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(0.3f))
            
            // Current Step Display
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500, delayMillis = 200)) + scaleIn(
                    initialScale = 0.8f,
                    animationSpec = tween(500, delayMillis = 200, easing = EaseOutCubic)
                )
            ) {
                Box(contentAlignment = Alignment.Center) {
                    // Glow Ring
                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        MutedTeal.copy(alpha = glowAlpha),
                                        Color.Transparent
                                    )
                                ),
                                shape = CircleShape
                            )
                    )
                    
                    // Number Circle
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .scale(numberScale)
                            .background(DeepSurface.copy(alpha = 0.6f), CircleShape)
                            .border(2.dp, MutedTeal.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedContent(
                            targetState = currentStep,
                            transitionSpec = {
                                (fadeIn(tween(300)) + scaleIn(
                                    initialScale = 0.8f,
                                    animationSpec = tween(300)
                                )).togetherWith(
                                    fadeOut(tween(200)) + scaleOut(targetScale = 1.2f)
                                )
                            },
                            label = "stepNumber"
                        ) { step ->
                            Text(
                                text = "$step",
                                fontSize = 100.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = OffWhite.copy(alpha = 0.95f)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(0.3f))
            
            // Instruction Card
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500, delayMillis = 300)) + slideInVertically(
                    initialOffsetY = { 50 },
                    animationSpec = tween(500, delayMillis = 300, easing = EaseOutCubic)
                )
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DeepSurface.copy(alpha = 0.7f)),
                    shape = RoundedCornerShape(ArouraSpacing.cardRadius.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            Color.White.copy(alpha = 0.08f),
                            RoundedCornerShape(ArouraSpacing.cardRadius.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(ArouraSpacing.lg.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AnimatedContent(
                            targetState = currentStep,
                            transitionSpec = {
                                fadeIn(tween(300)).togetherWith(fadeOut(tween(200)))
                            },
                            label = "stepText"
                        ) { step ->
                            Text(
                                text = steps[step] ?: "Breathe.",
                                style = MaterialTheme.typography.titleLarge,
                                color = OffWhite,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Light,
                                lineHeight = 28.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
                        
                        PremiumGroundingButton(
                            isReset = currentStep == 1,
                            onClick = {
                                if (currentStep > 1) {
                                    currentStep--
                                } else {
                                    currentStep = 5
                                }
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(ArouraSpacing.xxl.dp))
        }
    }
}

@Composable
private fun PremiumGroundingButton(
    isReset: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "btnScale"
    )
    
    val backgroundColor by animateColorAsState(
        targetValue = if (isReset) CalmingPeach else MutedTeal,
        animationSpec = tween(300),
        label = "btnBg"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale)
            .background(
                backgroundColor,
                RoundedCornerShape(28.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (isReset) Icons.Default.Refresh else Icons.Default.Check,
                null,
                tint = MidnightCharcoal,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(ArouraSpacing.sm.dp))
            Text(
                if (isReset) "Start Over" else "I've done this",
                color = MidnightCharcoal,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
