package com.example.aroura.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.ui.components.AdvancedAuroraBackground
import com.example.aroura.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(onGetStarted: () -> Unit) {
    // Staggered Entry Animation States
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(300)
        isVisible = true
    }

    // Breathing Animation for Title
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f, // Very subtle
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "titleScale"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        AdvancedAuroraBackground()

        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(1000)) + slideInVertically(
                animationSpec = tween(1000, easing = EaseOutExpo),
                initialOffsetY = { 50 }
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
                    .systemBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.weight(0.8f))

                // Mentall Wellness Pill
                Surface(
                    color = MutedTeal.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    Text(
                        text = "MENTAL WELLNESS",
                        style = MaterialTheme.typography.labelMedium,
                        color = MutedTeal,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Breathing Title
                Text(
                    text = "A.R.O.U.R.A",
                    style = MaterialTheme.typography.displayMedium,
                    color = OffWhite,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Light,
                    letterSpacing = 4.sp,
                    modifier = Modifier.scale(scale)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Built to quiet the noise\ninside your head.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextDarkSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 32.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                // Animated Button
                PulsingButton(
                    text = "Get Started",
                    onClick = onGetStarted
                )

                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Private • Secure • Safe",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextDarkSecondary.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun PulsingButton(text: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Press Scale Animation
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "buttonScale"
    )
    
    // Glow Pulse Animation (Idle)
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .scale(scale)
    ) {
        // Glow Shadow (Using graphicsLayer alpha instead of blur for compatibility)
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    this.alpha = glowAlpha
                    this.scaleX = 1.05f
                    this.scaleY = 1.1f
                },
            color = MutedTeal,
            shape = RoundedCornerShape(32.dp)
        ) {}

        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MutedTeal,
                contentColor = MidnightCharcoal
            ),
            shape = RoundedCornerShape(32.dp),
            interactionSource = interactionSource,
            elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
