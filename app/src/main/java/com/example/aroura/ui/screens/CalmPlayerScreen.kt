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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.ui.components.ArouraBackground
import com.example.aroura.ui.theme.*

/**
 * Calm Player Screen - Premium Redesign
 * 
 * Features:
 * - Rotating album art with breathing glow
 * - Animated play/pause button
 * - Premium progress slider
 * - Smooth entrance animations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalmPlayerScreen(item: CalmMediaItem, onBack: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0.3f) }
    var volume by remember { mutableFloatStateOf(0.7f) }
    
    LaunchedEffect(Unit) { visible = true }
    
    // Continuous rotation for playing state
    val infiniteTransition = rememberInfiniteTransition(label = "albumArt")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    // Breathing glow effect
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )
    
    // Animated scale for play/pause
    val playButtonScale by animateFloatAsState(
        targetValue = if (isPlaying) 1f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "playScale"
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = OffWhite)
                }
                
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(400))
                ) {
                    Text(
                        text = "Now Playing",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextDarkSecondary
                    )
                }
                
                IconButton(onClick = {}) { 
                    Icon(Icons.Default.Share, "Share", tint = TextDarkSecondary) 
                }
            }

            Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))

            // Album Art with Glow
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600)) + scaleIn(
                    initialScale = 0.8f,
                    animationSpec = tween(600, easing = EaseOutCubic)
                )
            ) {
                Box(contentAlignment = Alignment.Center) {
                    // Outer Glow
                    Box(
                        modifier = Modifier
                            .size(320.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        item.startColor.copy(alpha = glowAlpha),
                                        Color.Transparent
                                    )
                                ),
                                shape = CircleShape
                            )
                    )
                    
                    // Album Art
                    Box(
                        modifier = Modifier
                            .size(280.dp)
                            .shadow(24.dp, RoundedCornerShape(ArouraSpacing.xl.dp))
                            .clip(RoundedCornerShape(ArouraSpacing.xl.dp))
                            .rotate(if (isPlaying) rotation else 0f)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(item.startColor, item.endColor)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Decorative Ring
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                        )
                        
                        // Inner Glow Circle
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .background(
                                    Color.White.copy(alpha = 0.1f),
                                    CircleShape
                                )
                        )
                        
                        Text(
                            text = "ॐ",
                            fontSize = 64.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(ArouraSpacing.xxl.dp))

            // Title & Subtitle
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500, delayMillis = 200)) + slideInVertically(
                    initialOffsetY = { 20 },
                    animationSpec = tween(500, delayMillis = 200)
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = OffWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(ArouraSpacing.xs.dp))
                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextDarkSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Progress Bar
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500, delayMillis = 300))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Slider(
                        value = progress,
                        onValueChange = { progress = it },
                        colors = SliderDefaults.colors(
                            thumbColor = MutedTeal,
                            activeTrackColor = MutedTeal,
                            inactiveTrackColor = DeepSurface.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.height(24.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "1:05", 
                            style = MaterialTheme.typography.labelSmall, 
                            color = TextDarkSecondary
                        )
                        Text(
                            "-4:32", 
                            style = MaterialTheme.typography.labelSmall, 
                            color = TextDarkSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))

            // Controls
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500, delayMillis = 400)) + slideInVertically(
                    initialOffsetY = { 30 },
                    animationSpec = tween(500, delayMillis = 400)
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous
                    PremiumControlButton(
                        onClick = {},
                        size = 56.dp,
                        backgroundColor = Color.White.copy(alpha = 0.08f)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            null, 
                            tint = OffWhite, 
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    
                    // Play/Pause
                    PremiumControlButton(
                        onClick = { isPlaying = !isPlaying },
                        size = 80.dp,
                        backgroundColor = MutedTeal.copy(alpha = 0.3f),
                        modifier = Modifier.scale(playButtonScale)
                    ) {
                        Icon(
                            if (isPlaying) CalmIcons.Pause else Icons.Default.PlayArrow, 
                            null, 
                            tint = OffWhite, 
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    // Next
                    PremiumControlButton(
                        onClick = {},
                        size = 56.dp,
                        backgroundColor = Color.White.copy(alpha = 0.08f)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward, 
                            null, 
                            tint = OffWhite, 
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))

            // Volume
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500, delayMillis = 500))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = ArouraSpacing.lg.dp)
                ) {
                    Icon(
                        CalmIcons.VolumeUp, 
                        null, 
                        tint = TextDarkSecondary, 
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(ArouraSpacing.md.dp))
                    Slider(
                        value = volume,
                        onValueChange = { volume = it },
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = TextDarkSecondary,
                            activeTrackColor = TextDarkSecondary.copy(alpha = 0.5f),
                            inactiveTrackColor = DeepSurface.copy(alpha = 0.4f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(ArouraSpacing.xxl.dp))
        }
    }
}

@Composable
private fun PremiumControlButton(
    onClick: () -> Unit,
    size: androidx.compose.ui.unit.Dp,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "controlScale"
    )
    
    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .background(backgroundColor, CircleShape)
            .border(
                1.dp,
                Color.White.copy(alpha = 0.1f),
                CircleShape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

// Custom icons for CalmPlayerScreen
private object CalmIcons {
    val Pause: androidx.compose.ui.graphics.vector.ImageVector by lazy {
        androidx.compose.ui.graphics.vector.ImageVector.Builder(
            name = "Pause",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).addPath(
            pathData = androidx.compose.ui.graphics.vector.PathParser().parsePathString(
                "M6,19L10,19L10,5L6,5L6,19ZM14,5L14,19L18,19L18,5L14,5Z"
            ).toNodes(),
            fill = androidx.compose.ui.graphics.SolidColor(Color.White)
        ).build()
    }
    
    val VolumeUp: androidx.compose.ui.graphics.vector.ImageVector by lazy {
        androidx.compose.ui.graphics.vector.ImageVector.Builder(
            name = "VolumeUp",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).addPath(
            pathData = androidx.compose.ui.graphics.vector.PathParser().parsePathString(
                "M3,9L3,15L7,15L12,20L12,4L7,9L3,9ZM16.5,12C16.5,10.23 15.48,8.71 14,7.97L14,16.02C15.48,15.29 16.5,13.77 16.5,12ZM14,3.23L14,5.29C16.89,6.15 19,8.83 19,12C19,15.17 16.89,17.85 14,18.71L14,20.77C18,19.86 21,16.28 21,12C21,7.72 18,4.14 14,3.23Z"
            ).toNodes(),
            fill = androidx.compose.ui.graphics.SolidColor(Color.White)
        ).build()
    }
}
