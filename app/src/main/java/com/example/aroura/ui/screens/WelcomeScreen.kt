package com.example.aroura.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.ui.components.ArouraBackground
import com.example.aroura.ui.components.ArouraPill
import com.example.aroura.ui.components.ArouraPrimaryButton
import com.example.aroura.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Welcome / Get Started Screen
 * 
 * First impression screen - calm, minimal, inviting
 * Features:
 * - Staggered entrance animation
 * - Subtle breathing effect on title
 * - Premium aurora background
 * - Smooth CTA transition
 */
@Composable
fun WelcomeScreen(onGetStarted: () -> Unit) {
    // ═══════════════════════════════════════════════════════════════════════════
    // ANIMATION STATES
    // ═══════════════════════════════════════════════════════════════════════════
    
    var showPill by remember { mutableStateOf(false) }
    var showTitle by remember { mutableStateOf(false) }
    var showTagline by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }
    var showFooter by remember { mutableStateOf(false) }
    
    // Staggered entrance sequence
    LaunchedEffect(Unit) {
        delay(400)
        showPill = true
        delay(200)
        showTitle = true
        delay(300)
        showTagline = true
        delay(400)
        showButton = true
        delay(200)
        showFooter = true
    }
    
    // Title breathing animation (very subtle)
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val titleScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.015f,
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "titleBreathing"
    )
    
    val titleAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.92f,
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "titleAlphaBreathing"
    )

    // ═══════════════════════════════════════════════════════════════════════════
    // UI LAYOUT
    // ═══════════════════════════════════════════════════════════════════════════
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Premium Aurora Background
        ArouraBackground()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = ArouraSpacing.screenHorizontal.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(0.35f))
            
            // ═══════════════════════════════════════════════════════════════════
            // MENTAL WELLNESS PILL
            // ═══════════════════════════════════════════════════════════════════
            
            AnimatedVisibility(
                visible = showPill,
                enter = fadeIn(tween(800)) + scaleIn(
                    initialScale = 0.8f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            ) {
                ArouraPill(
                    text = "Mental Wellness",
                    modifier = Modifier.padding(bottom = 28.dp)
                )
            }
            
            // ═══════════════════════════════════════════════════════════════════
            // TITLE: A.R.O.U.R.A
            // ═══════════════════════════════════════════════════════════════════
            
            AnimatedVisibility(
                visible = showTitle,
                enter = fadeIn(tween(1000, easing = FastOutSlowInEasing)) + 
                        slideInVertically(
                            initialOffsetY = { 40 },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
            ) {
                Text(
                    text = "A.R.O.U.R.A",
                    style = MaterialTheme.typography.displayMedium,
                    color = OffWhite,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Light,
                    letterSpacing = 6.sp,
                    modifier = Modifier
                        .scale(titleScale)
                        .alpha(titleAlpha)
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // ═══════════════════════════════════════════════════════════════════
            // TAGLINE
            // ═══════════════════════════════════════════════════════════════════
            
            AnimatedVisibility(
                visible = showTagline,
                enter = fadeIn(tween(900, easing = FastOutSlowInEasing)) +
                        slideInVertically(
                            initialOffsetY = { 30 },
                            animationSpec = tween(900, easing = FastOutSlowInEasing)
                        )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Built to quiet the noise",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextDarkSecondary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "inside your head.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextDarkSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(0.55f))
            
            // ═══════════════════════════════════════════════════════════════════
            // CTA BUTTON
            // ═══════════════════════════════════════════════════════════════════
            
            AnimatedVisibility(
                visible = showButton,
                enter = fadeIn(tween(800)) + slideInVertically(
                    initialOffsetY = { 60 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            ) {
                ArouraPrimaryButton(
                    text = "Get Started",
                    onClick = onGetStarted,
                    modifier = Modifier.padding(horizontal = ArouraSpacing.md.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // ═══════════════════════════════════════════════════════════════════
            // FOOTER
            // ═══════════════════════════════════════════════════════════════════
            
            AnimatedVisibility(
                visible = showFooter,
                enter = fadeIn(tween(600))
            ) {
                Text(
                    text = "Private  •  Secure  •  Safe",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextDarkTertiary,
                    letterSpacing = 1.sp
                )
            }
            
            Spacer(modifier = Modifier.height(ArouraSpacing.xxl.dp))
        }
    }
}
