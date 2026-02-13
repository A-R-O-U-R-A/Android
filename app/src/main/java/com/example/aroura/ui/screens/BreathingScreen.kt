package com.example.aroura.ui.screens

import android.media.MediaPlayer
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.data.api.ApiClient
import com.example.aroura.data.api.LikeSongRequest
import com.example.aroura.data.local.TokenManager
import kotlinx.coroutines.launch
import com.example.aroura.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

/**
 * Breathing Screen - Premium Redesign
 * 
 * Features:
 * - Circular breathing with smooth dot movement
 * - Fixed pause/resume animation (maintains speed)
 * - Background meditation music
 * - Mute/unmute button
 * - Heart button to save favorite songs
 * - Randomize music button
 * - Entrance animations for controls
 * - Premium control buttons with scale feedback
 */
@Composable
fun BreathingScreen(
    onClose: () -> Unit,
    onComplete: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // API access for liked songs
    val tokenManager = remember { TokenManager.getInstance(context) }
    val reflectApi = remember { ApiClient.createReflectApiService(tokenManager) }
    
    // Premium calming gradient
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A2F35), // Deep dark teal
            Color(0xFF2A4A4F), // Deep teal
            Color(0xFF3D6B6B)  // Muted teal
        )
    )

    var isPlaying by remember { mutableStateOf(true) }
    var isFavorite by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(false) }
    
    // Ambient sound tracks (using reliable free streaming URLs from Pixabay)
    val ambientTracks = remember {
        listOf(
            "peaceful_breathing" to ("Peaceful Breathing" to "https://cdn.pixabay.com/audio/2022/03/10/audio_c8c8a73467.mp3"),
            "ocean_waves" to ("Ocean Waves" to "https://cdn.pixabay.com/audio/2022/06/07/audio_b9bd4170e4.mp3"),
            "forest_ambience" to ("Forest Ambience" to "https://cdn.pixabay.com/audio/2021/09/06/audio_8e1f2fbfc1.mp3"),
            "gentle_rain" to ("Gentle Rain" to "https://cdn.pixabay.com/audio/2022/03/24/audio_4e1e71f19f.mp3"),
            "meditation_bells" to ("Meditation Bells" to "https://cdn.pixabay.com/audio/2022/10/30/audio_b7bd30b0c8.mp3")
        )
    }
    
    // Use -1 as initial to indicate "not loaded yet"
    var currentTrackIndex by remember { mutableIntStateOf(-1) }
    var currentSongName by remember { mutableStateOf("Loading...") }
    var likedSongId by remember { mutableStateOf<String?>(null) }
    var hasLoadedLikedSong by remember { mutableStateOf(false) }
    
    // Load liked song on startup (if any)
    LaunchedEffect(Unit) {
        try {
            val response = reflectApi.getLikedSongs(source = "breathing")
            if (response.isSuccessful && response.body()?.success == true) {
                val likedSongs = response.body()?.songs ?: emptyList()
                if (likedSongs.isNotEmpty()) {
                    // Find the liked song in our tracks
                    val likedSong = likedSongs.first()
                    likedSongId = likedSong.songId
                    val trackIndex = ambientTracks.indexOfFirst { it.first == likedSong.songId }
                    if (trackIndex >= 0) {
                        currentTrackIndex = trackIndex
                        currentSongName = ambientTracks[trackIndex].second.first
                        isFavorite = true
                        hasLoadedLikedSong = true
                    } else {
                        // Liked song not found in our tracks, pick random
                        val randomIndex = (0 until ambientTracks.size).random()
                        currentTrackIndex = randomIndex
                        currentSongName = ambientTracks[randomIndex].second.first
                        hasLoadedLikedSong = true
                    }
                } else {
                    // No liked songs, start with random track
                    val randomIndex = (0 until ambientTracks.size).random()
                    currentTrackIndex = randomIndex
                    currentSongName = ambientTracks[randomIndex].second.first
                    hasLoadedLikedSong = true
                }
            } else {
                // API failed, start with random track
                val randomIndex = (0 until ambientTracks.size).random()
                currentTrackIndex = randomIndex
                currentSongName = ambientTracks[randomIndex].second.first
                hasLoadedLikedSong = true
            }
        } catch (e: Exception) {
            android.util.Log.e("BreathingScreen", "Failed to load liked songs", e)
            // On error, start with random track
            val randomIndex = (0 until ambientTracks.size).random()
            currentTrackIndex = randomIndex
            currentSongName = ambientTracks[randomIndex].second.first
            hasLoadedLikedSong = true
        }
    }
    
    // MediaPlayer state
    val mediaPlayer = remember { MediaPlayer() }
    var mediaPlayerReady by remember { mutableStateOf(false) }
    
    // Initialize and manage MediaPlayer - only when track index is valid
    LaunchedEffect(currentTrackIndex) {
        if (currentTrackIndex < 0) return@LaunchedEffect // Wait for valid track
        
        try {
            // Check if current track is the liked one
            isFavorite = ambientTracks[currentTrackIndex].first == likedSongId
            currentSongName = ambientTracks[currentTrackIndex].second.first
            
            mediaPlayerReady = false
            mediaPlayer.reset()
            mediaPlayer.setDataSource(ambientTracks[currentTrackIndex].second.second)
            mediaPlayer.isLooping = true
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                mediaPlayerReady = true
                // Auto-start playing when ready if not muted
                if (isPlaying && !isMuted) {
                    it.start()
                }
            }
            mediaPlayer.setOnErrorListener { _, what, extra ->
                android.util.Log.e("BreathingScreen", "MediaPlayer error: what=$what, extra=$extra")
                // Try next track on error
                false
            }
        } catch (e: Exception) {
            // Fallback - audio not available, continue with visual breathing only
            android.util.Log.e("BreathingScreen", "Failed to load audio", e)
        }
    }
    
    // Handle play/pause state
    LaunchedEffect(isPlaying, isMuted, mediaPlayerReady) {
        if (mediaPlayerReady) {
            try {
                if (isPlaying && !isMuted) {
                    if (!mediaPlayer.isPlaying) {
                        mediaPlayer.start()
                    }
                } else {
                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.pause()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("BreathingScreen", "MediaPlayer error", e)
            }
        }
    }
    
    // Track the animation state properly
    var animationProgress by remember { mutableFloatStateOf(0f) }
    var pausedAtProgress by remember { mutableFloatStateOf(0f) }
    
    // Track if routine has been marked complete
    var hasMarkedComplete by remember { mutableStateOf(false) }
    
    // Mark complete IMMEDIATELY when screen opens (per user request)
    LaunchedEffect(Unit) {
        if (!hasMarkedComplete) {
            hasMarkedComplete = true
            onComplete()
        }
    }
    
    LaunchedEffect(Unit) { visible = true }
    
    // Cleanup on dispose
    DisposableEffect(Unit) {
        onDispose {
            try {
                mediaPlayer.stop()
                mediaPlayer.release()
            } catch (e: Exception) {
                // Ignore cleanup errors
            }
        }
    }

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
                            MutedTeal.copy(alpha = 0.12f),
                            Color.Transparent
                        ),
                        radius = 800f
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
        
        // Current song name (top center)
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(500, delayMillis = 200)),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 56.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🎵 $currentSongName",
                    style = MaterialTheme.typography.labelMedium,
                    color = OffWhite.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Light
                )
            }
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
            BreathingAnimationFixed(
                isPlaying = isPlaying,
                onProgressUpdate = { animationProgress = it }
            )
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 40.dp)
            ) {
                // Main controls row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Favorite/Heart Button
                    PremiumControlButton(
                        icon = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        isSelected = isFavorite,
                        selectedColor = GentleError,
                        onClick = { 
                            val currentTrack = ambientTracks[currentTrackIndex]
                            if (isFavorite) {
                                // Unlike the song
                                scope.launch {
                                    try {
                                        val response = reflectApi.unlikeSong(currentTrack.first)
                                        if (response.isSuccessful) {
                                            isFavorite = false
                                            likedSongId = null
                                        }
                                    } catch (e: Exception) {
                                        android.util.Log.e("BreathingScreen", "Failed to unlike song", e)
                                    }
                                }
                            } else {
                                // Like the song
                                scope.launch {
                                    try {
                                        val request = LikeSongRequest(
                                            songId = currentTrack.first,
                                            title = currentTrack.second.first,
                                            artist = "A.R.O.U.R.A",
                                            audioUrl = currentTrack.second.second,
                                            source = "breathing",
                                            duration = 0
                                        )
                                        val response = reflectApi.likeSong(request)
                                        if (response.isSuccessful) {
                                            isFavorite = true
                                            likedSongId = currentTrack.first
                                        }
                                    } catch (e: Exception) {
                                        android.util.Log.e("BreathingScreen", "Failed to like song", e)
                                    }
                                }
                            }
                        }
                    )

                    // Play/Pause (Center, Larger)
                    PremiumControlButton(
                        icon = if (isPlaying) CustomIcons.Pause else Icons.Default.PlayArrow,
                        isSelected = false,
                        size = 80.dp,
                        iconSize = 40.dp,
                        onClick = { 
                            if (isPlaying) {
                                pausedAtProgress = animationProgress
                            }
                            isPlaying = !isPlaying 
                        }
                    )

                    // Mute/Unmute Button
                    PremiumControlButton(
                        icon = if (isMuted) VolumeOffIcon else VolumeOnIcon,
                        isSelected = !isMuted,
                        selectedColor = MutedTeal,
                        onClick = { isMuted = !isMuted }
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Secondary controls row (Randomize)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Randomize music button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.Black.copy(alpha = 0.2f))
                            .clickable {
                                // Shuffle to a different track
                                var newIndex = (0 until ambientTracks.size).random()
                                while (newIndex == currentTrackIndex && ambientTracks.size > 1) {
                                    newIndex = (0 until ambientTracks.size).random()
                                }
                                currentTrackIndex = newIndex
                                currentSongName = ambientTracks[newIndex].second.first
                            }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Randomize",
                            tint = OffWhite.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Shuffle Music",
                            style = MaterialTheme.typography.labelMedium,
                            color = OffWhite.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PremiumControlButton(
    icon: ImageVector,
    isSelected: Boolean,
    selectedColor: Color = MutedTeal,
    size: Dp = 56.dp,
    iconSize: Dp = 24.dp,
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
    
    val iconTint by animateColorAsState(
        targetValue = if (isSelected) selectedColor else OffWhite,
        animationSpec = tween(200),
        label = "iconTint"
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
            tint = iconTint,
            modifier = Modifier.size(iconSize)
        )
    }
}

/**
 * Fixed breathing animation that properly handles pause/resume
 * The key fix: Use a running sum of time instead of Animatable
 * This prevents speed changes when pausing/resuming
 */
@Composable
fun BreathingAnimationFixed(
    isPlaying: Boolean,
    onProgressUpdate: (Float) -> Unit = {}
) {
    var currentPhaseText by remember { mutableStateOf("Breathe In") }
    
    // Use elapsed time tracking instead of Animatable for smoother pause/resume
    var elapsedTime by remember { mutableLongStateOf(0L) }
    var lastFrameTime by remember { mutableLongStateOf(0L) }
    
    // Total cycle duration: 4s inhale + 4s hold + 6s exhale = 14s = 14000ms
    val cycleDuration = 14000L
    
    // Calculate current angle based on elapsed time
    val currentAngle = remember(elapsedTime) {
        val cycleProgress = (elapsedTime % cycleDuration).toFloat() / cycleDuration
        cycleProgress * 360f
    }
    
    // Determine current phase
    LaunchedEffect(currentAngle) {
        currentPhaseText = when {
            currentAngle < 102.86f -> "Breathe In" // 4/14 of 360 = ~102.86°
            currentAngle < 205.71f -> "Hold"       // 8/14 of 360 = ~205.71°
            else -> "Breathe Out"
        }
        onProgressUpdate(currentAngle)
    }
    
    // Animation loop using frame-based timing
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            lastFrameTime = System.currentTimeMillis()
            while (true) {
                delay(16) // ~60 FPS
                val currentTime = System.currentTimeMillis()
                val deltaTime = currentTime - lastFrameTime
                lastFrameTime = currentTime
                elapsedTime += deltaTime
            }
        }
    }

    // Breathing glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "breathingGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )
    
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathingScale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        val circleRadius = 120.dp
        
        // Outer glow ring
        Canvas(
            modifier = Modifier
                .size(circleRadius * 2.5f)
                .scale(breathingScale)
        ) {
            drawCircle(
                color = MutedTeal.copy(alpha = glowAlpha * 0.15f),
                style = Stroke(width = 40.dp.toPx())
            )
        }
        
        // Static Circle & Fixed Dots
        Canvas(modifier = Modifier.size(circleRadius * 2)) {
            // Static Ring
            drawCircle(
                color = OffWhite.copy(alpha = 0.3f),
                style = Stroke(width = 2.dp.toPx())
            )
            
            // Fixed Dots at 0, 120, 240 degrees (breathing phases)
            val phases = listOf(
                Pair(0f, "Breathe In"),
                Pair(102.86f, "Hold"),
                Pair(205.71f, "Breathe Out")
            )
            val r = size.minDimension / 2
            
            phases.forEach { (deg, _) ->
                val rad = Math.toRadians(deg.toDouble() - 90.0)
                val x = center.x + r * cos(rad).toFloat()
                val y = center.y + r * sin(rad).toFloat()
                
                // Dot glow
                drawCircle(
                    color = OffWhite.copy(alpha = 0.2f),
                    radius = 10.dp.toPx(),
                    center = Offset(x, y)
                )
                // Dot core
                drawCircle(
                    color = OffWhite.copy(alpha = 0.6f),
                    radius = 6.dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }

        // Moving Dot with glow trail
        Canvas(modifier = Modifier.size(circleRadius * 2)) {
            val r = size.minDimension / 2
            val currentDeg = currentAngle - 90f
            val rad = Math.toRadians(currentDeg.toDouble())
            
            val x = center.x + r * cos(rad).toFloat()
            val y = center.y + r * sin(rad).toFloat()

            // Trail effect (subtle arc behind the dot)
            val trailLength = 30f
            val trailStartAngle = currentDeg - trailLength
            drawArc(
                color = MutedTeal.copy(alpha = 0.3f),
                startAngle = trailStartAngle,
                sweepAngle = trailLength,
                useCenter = false,
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )

            // Outer glow
            drawCircle(
                color = MutedTeal.copy(alpha = glowAlpha),
                radius = 20.dp.toPx(),
                center = Offset(x, y)
            )
            // Inner glow
            drawCircle(
                color = OffWhite.copy(alpha = 0.6f),
                radius = 12.dp.toPx(),
                center = Offset(x, y)
            )
            // Core
            drawCircle(
                color = OffWhite,
                radius = 8.dp.toPx(),
                center = Offset(x, y)
            )
        }

        // Center Text with crossfade
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                    fontWeight = FontWeight.Light,
                    fontSize = 28.sp
                )
            }
            
            // Phase indicator dots
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val phases = listOf("Breathe In", "Hold", "Breathe Out")
                phases.forEach { phase ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (phase == currentPhaseText) MutedTeal
                                else OffWhite.copy(alpha = 0.3f)
                            )
                    )
                }
            }
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

// Volume icons
val VolumeOnIcon: ImageVector
    get() = ImageVector.Builder(
        name = "VolumeOn",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.White)) {
            moveTo(3f, 9f)
            verticalLineTo(15f)
            horizontalLineTo(7f)
            lineTo(12f, 20f)
            verticalLineTo(4f)
            lineTo(7f, 9f)
            horizontalLineTo(3f)
            close()
            moveTo(16.5f, 12f)
            curveTo(16.5f, 10.23f, 15.48f, 8.71f, 14f, 7.97f)
            verticalLineTo(16.02f)
            curveTo(15.48f, 15.29f, 16.5f, 13.77f, 16.5f, 12f)
            close()
            moveTo(14f, 3.23f)
            verticalLineTo(5.29f)
            curveTo(16.89f, 6.15f, 19f, 8.83f, 19f, 12f)
            curveTo(19f, 15.17f, 16.89f, 17.85f, 14f, 18.71f)
            verticalLineTo(20.77f)
            curveTo(18f, 19.86f, 21f, 16.28f, 21f, 12f)
            curveTo(21f, 7.72f, 18f, 4.14f, 14f, 3.23f)
            close()
        }
    }.build()

val VolumeOffIcon: ImageVector
    get() = ImageVector.Builder(
        name = "VolumeOff",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.White)) {
            moveTo(16.5f, 12f)
            curveTo(16.5f, 10.23f, 15.48f, 8.71f, 14f, 7.97f)
            verticalLineTo(10.18f)
            lineTo(16.45f, 12.63f)
            curveTo(16.48f, 12.43f, 16.5f, 12.22f, 16.5f, 12f)
            close()
            moveTo(19f, 12f)
            curveTo(19f, 12.94f, 18.8f, 13.82f, 18.46f, 14.64f)
            lineTo(19.97f, 16.15f)
            curveTo(20.63f, 14.91f, 21f, 13.5f, 21f, 12f)
            curveTo(21f, 7.72f, 18f, 4.14f, 14f, 3.23f)
            verticalLineTo(5.29f)
            curveTo(16.89f, 6.15f, 19f, 8.83f, 19f, 12f)
            close()
            moveTo(4.27f, 3f)
            lineTo(3f, 4.27f)
            lineTo(7.73f, 9f)
            horizontalLineTo(3f)
            verticalLineTo(15f)
            horizontalLineTo(7f)
            lineTo(12f, 20f)
            verticalLineTo(13.27f)
            lineTo(16.25f, 17.52f)
            curveTo(15.58f, 18.04f, 14.83f, 18.45f, 14f, 18.7f)
            verticalLineTo(20.77f)
            curveTo(15.38f, 20.45f, 16.63f, 19.82f, 17.68f, 18.96f)
            lineTo(19.73f, 21f)
            lineTo(21f, 19.73f)
            lineTo(12f, 10.73f)
            lineTo(4.27f, 3f)
            close()
            moveTo(12f, 4f)
            lineTo(9.91f, 6.09f)
            lineTo(12f, 8.18f)
            verticalLineTo(4f)
            close()
        }
    }.build()