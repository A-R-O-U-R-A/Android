package com.example.aroura.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// Colors for splash screen
private val SplashBackground = Color(0xFF1A1A1E)
private val SplashTeal = Color(0xFF4DB6AC)
private val SplashTextSecondary = Color(0xFFB0B0B0)
private val SplashTextPrimary = Color(0xFFF5F5F5)

/**
 * Splash Screen - Lightweight fast-loading screen
 * Shows while app initializes in background
 */
@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit
) {
    // Animate alpha for smooth appearance
    var visible by remember { mutableStateOf(false) }
    
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(400),
        label = "alpha"
    )
    
    // Pulsing animation for the dot
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    
    LaunchedEffect(Unit) {
        visible = true
        // Minimum splash time to allow initialization
        delay(800)
        onSplashComplete()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SplashBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(alpha)
        ) {
            // App Name
            Text(
                text = "A.R.O.U.R.A",
                style = MaterialTheme.typography.headlineLarge,
                color = SplashTextPrimary,
                fontWeight = FontWeight.Light,
                letterSpacing = 8.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Tagline
            Text(
                text = "Your Mental Wellness Companion",
                style = MaterialTheme.typography.bodyMedium,
                color = SplashTextSecondary
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Loading indicator - simple dot
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .alpha(pulseAlpha)
                    .background(SplashTeal, shape = CircleShape)
            )
        }
    }
}
