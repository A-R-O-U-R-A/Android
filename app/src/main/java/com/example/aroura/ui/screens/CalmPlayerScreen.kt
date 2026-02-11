package com.example.aroura.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.media.AudioPlayerManager
import com.example.aroura.media.formatDuration

/**
 * Calm Player Screen - Premium Spotify-Style Design
 * 
 * Features:
 * - Beautiful maroon gradient background
 * - Large album art with shadow
 * - Clean title and artist display
 * - Premium progress bar with timestamps
 * - Elegant playback controls (shuffle, prev, play, next, timer)
 * - Bottom action bar (loop, share, queue)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalmPlayerScreen(item: CalmMediaItem, onBack: () -> Unit) {
    val context = LocalContext.current
    
    // Use remember with key to avoid recreating player on recomposition
    val audioPlayer = remember(context) { 
        try {
            AudioPlayerManager.getInstance(context)
        } catch (e: Exception) {
            null
        }
    }
    
    // Handle case where player couldn't be initialized
    if (audioPlayer == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Unable to initialize audio player", color = Color.White)
        }
        return
    }
    
    // Collect player state
    val playerState by audioPlayer.playerState.collectAsState()
    val currentPosition by audioPlayer.currentPosition.collectAsState()
    val duration by audioPlayer.duration.collectAsState()
    val isLoading by audioPlayer.isLoading.collectAsState()
    
    var visible by remember { mutableStateOf(false) }
    var showSleepTimerDialog by remember { mutableStateOf(false) }
    var isShuffleEnabled by remember { mutableStateOf(false) }
    
    // Premium colors - Deep maroon gradient like the reference
    val backgroundGradientTop = Color(0xFF6B1028) // Darker maroon
    val backgroundColor = Color(0xFF8B1538) // Rich maroon
    val backgroundGradientBottom = Color(0xFF3D0A18) // Deep dark maroon
    val accentGreen = Color(0xFF1DB954) // Spotify green for shuffle
    val textWhite = Color.White
    val textGray = Color(0xFFB3B3B3)
    
    // Start playing when screen opens - with safety checks
    LaunchedEffect(item.id) {
        visible = true
        if (item.streamingUrl.isNotEmpty()) {
            try {
                audioPlayer.play(
                    url = item.streamingUrl,
                    backupUrl = item.streamingUrlBackup,
                    title = item.title,
                    subtitle = item.subtitle,
                    loop = item.loopAllowed
                )
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to play audio", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Audio not available for this track", Toast.LENGTH_SHORT).show()
        }
    }
    
    // Cleanup when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            // Don't stop the audio - let it play in background
            // User can pause manually if needed
        }
    }
    
    // Calculate progress
    val progress = if (duration > 0) {
        (currentPosition.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
    } else 0f
    
    // Entry animation
    val albumScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "albumScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundGradientTop,
                        backgroundColor,
                        backgroundGradientBottom
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ═══════════════════════════════════════════════════════════════
            // TOP BAR
            // ═══════════════════════════════════════════════════════════════
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Collapse Button
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.KeyboardArrowDown, 
                        contentDescription = "Minimize",
                        tint = textWhite,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                // Center Title
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(400))
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "RECOMMENDED FOR YOU",
                            style = MaterialTheme.typography.labelSmall,
                            color = textGray,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                // More Options
                IconButton(onClick = { /* Options menu */ }) {
                    Icon(
                        Icons.Default.MoreVert, 
                        contentDescription = "More",
                        tint = textWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ═══════════════════════════════════════════════════════════════
            // ALBUM ART
            // ═══════════════════════════════════════════════════════════════
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600)) + scaleIn(
                    initialScale = 0.8f,
                    animationSpec = tween(600, easing = EaseOutCubic)
                )
            ) {
                Box(
                    modifier = Modifier
                        .scale(albumScale)
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .padding(horizontal = 16.dp)
                        .shadow(
                            elevation = 32.dp,
                            shape = RoundedCornerShape(8.dp),
                            spotColor = Color.Black.copy(alpha = 0.5f)
                        )
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(item.startColor, item.endColor)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Loading Indicator
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = textWhite,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(64.dp)
                        )
                    } else {
                        // Category-based artwork icon
                        Text(
                            text = when {
                                item.category.contains("devotional", ignoreCase = true) -> "ॐ"
                                item.category.contains("nature", ignoreCase = true) -> "🌿"
                                item.category.contains("audiobook", ignoreCase = true) -> "📖"
                                item.category.contains("meditation", ignoreCase = true) -> "🧘"
                                else -> "♪"
                            },
                            fontSize = 120.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ═══════════════════════════════════════════════════════════════
            // TITLE & ARTIST ROW
            // ═══════════════════════════════════════════════════════════════
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500, delayMillis = 200)) + slideInVertically(
                    initialOffsetY = { 20 },
                    animationSpec = tween(500, delayMillis = 200)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Title and Artist
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.headlineSmall,
                            color = textWhite,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Source badge (like the verified badge in Spotify)
                            if (item.sourceName.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .background(accentGreen, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${item.subtitle}${if (item.sourceName.isNotEmpty()) " • ${item.sourceName}" else ""}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = textGray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    // Close and Add buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Close/Remove button
                        IconButton(
                            onClick = { /* Remove from favorites */ },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove",
                                tint = textGray,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        
                        // Add to playlist button
                        IconButton(
                            onClick = { /* Add to playlist */ },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add to playlist",
                                tint = textGray,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
            
            // Error message
            if (playerState.hasError) {
                Text(
                    text = playerState.errorMessage ?: "Playback error",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFFF6B6B),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // ═══════════════════════════════════════════════════════════════
            // PROGRESS BAR
            // ═══════════════════════════════════════════════════════════════
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500, delayMillis = 300))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    // Custom progress bar
                    Slider(
                        value = progress,
                        onValueChange = { newProgress ->
                            val newPosition = (newProgress * duration).toLong()
                            audioPlayer.seekTo(newPosition)
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = textWhite,
                            activeTrackColor = textWhite,
                            inactiveTrackColor = textGray.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                    )
                    
                    // Time indicators
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = currentPosition.formatDuration(),
                            style = MaterialTheme.typography.labelSmall,
                            color = textGray
                        )
                        Text(
                            text = if (duration > 0) duration.formatDuration() else "--:--",
                            style = MaterialTheme.typography.labelSmall,
                            color = textGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ═══════════════════════════════════════════════════════════════
            // MAIN PLAYBACK CONTROLS
            // ═══════════════════════════════════════════════════════════════
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500, delayMillis = 400)) + slideInVertically(
                    initialOffsetY = { 30 },
                    animationSpec = tween(500, delayMillis = 400)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Shuffle
                    SpotifyControlButton(
                        icon = PlayerIcons.Shuffle,
                        contentDescription = "Shuffle",
                        size = 24.dp,
                        tint = if (isShuffleEnabled) accentGreen else textGray,
                        onClick = { isShuffleEnabled = !isShuffleEnabled }
                    )
                    
                    // Previous / Skip Back
                    SpotifyControlButton(
                        icon = PlayerIcons.SkipBack,
                        contentDescription = "Previous",
                        size = 36.dp,
                        tint = textWhite,
                        onClick = { audioPlayer.seekBackward(10000) }
                    )
                    
                    // Play/Pause - Large center button
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(textWhite, CircleShape)
                            .clickable {
                                if (item.streamingUrl.isEmpty()) {
                                    Toast
                                        .makeText(context, "Audio not available", Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    audioPlayer.togglePlayPause()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.Black,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(32.dp)
                            )
                        } else {
                            Icon(
                                if (playerState.isPlaying) PlayerIcons.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (playerState.isPlaying) "Pause" else "Play",
                                tint = Color.Black,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                    
                    // Next / Skip Forward
                    SpotifyControlButton(
                        icon = PlayerIcons.SkipForward,
                        contentDescription = "Next",
                        size = 36.dp,
                        tint = textWhite,
                        onClick = { audioPlayer.seekForward(10000) }
                    )
                    
                    // Sleep Timer
                    SpotifyControlButton(
                        icon = PlayerIcons.Timer,
                        contentDescription = "Sleep Timer",
                        size = 24.dp,
                        tint = if (playerState.sleepTimerEnabled) accentGreen else textGray,
                        onClick = { showSleepTimerDialog = true },
                        badge = if (playerState.sleepTimerEnabled) "${playerState.sleepTimerMinutesRemaining}" else null
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ═══════════════════════════════════════════════════════════════
            // BOTTOM ACTION BAR
            // ═══════════════════════════════════════════════════════════════
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500, delayMillis = 500))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Loop toggle
                    SpotifyControlButton(
                        icon = PlayerIcons.Repeat,
                        contentDescription = "Loop",
                        size = 22.dp,
                        tint = if (playerState.isLooping) accentGreen else textGray,
                        onClick = { 
                            if (item.loopAllowed) {
                                audioPlayer.toggleLoop()
                            } else {
                                Toast.makeText(context, "Loop not available for this track", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Share
                    SpotifyControlButton(
                        icon = Icons.Default.Share,
                        contentDescription = "Share",
                        size = 22.dp,
                        tint = textGray,
                        onClick = { /* Share functionality */ }
                    )
                    
                    Spacer(modifier = Modifier.width(32.dp))
                    
                    // Queue / Playlist
                    SpotifyControlButton(
                        icon = PlayerIcons.Queue,
                        contentDescription = "Queue",
                        size = 22.dp,
                        tint = textGray,
                        onClick = { /* Show queue */ }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
        
        // Sleep Timer Dialog
        if (showSleepTimerDialog) {
            SpotifySleepTimerDialog(
                currentMinutes = playerState.sleepTimerMinutesRemaining,
                isEnabled = playerState.sleepTimerEnabled,
                onDismiss = { showSleepTimerDialog = false },
                onSetTimer = { minutes ->
                    audioPlayer.setSleepTimer(minutes)
                    showSleepTimerDialog = false
                },
                onCancel = {
                    audioPlayer.cancelSleepTimer()
                    showSleepTimerDialog = false
                }
            )
        }
    }
}

@Composable
private fun SpotifyControlButton(
    icon: ImageVector,
    contentDescription: String,
    size: Dp,
    tint: Color,
    onClick: () -> Unit,
    badge: String? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "buttonScale"
    )
    
    Box(
        modifier = Modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(size)
        )
        
        // Badge for timer
        if (badge != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 6.dp, y = (-6).dp)
                    .background(Color(0xFF1DB954), RoundedCornerShape(4.dp))
                    .padding(horizontal = 3.dp, vertical = 1.dp)
            ) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontSize = 8.sp
                )
            }
        }
    }
}

@Composable
private fun SpotifySleepTimerDialog(
    currentMinutes: Int,
    isEnabled: Boolean,
    onDismiss: () -> Unit,
    onSetTimer: (Int) -> Unit,
    onCancel: () -> Unit
) {
    val timerOptions = listOf(5, 10, 15, 30, 45, 60, 90)
    val backgroundColor = Color(0xFF282828)
    val accentGreen = Color(0xFF1DB954)
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = backgroundColor,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                "Sleep Timer",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column {
                if (isEnabled) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(accentGreen.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            PlayerIcons.Timer,
                            contentDescription = null,
                            tint = accentGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Timer active: $currentMinutes min remaining",
                            color = accentGreen,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
                
                Text(
                    "Stop playback after:",
                    color = Color(0xFFB3B3B3),
                    style = MaterialTheme.typography.bodySmall
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Timer chips in a flow layout
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    timerOptions.chunked(4).forEach { row ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { minutes ->
                                val isSelected = isEnabled && currentMinutes == minutes
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (isSelected) accentGreen else Color(0xFF404040),
                                            RoundedCornerShape(20.dp)
                                        )
                                        .clickable { onSetTimer(minutes) }
                                        .padding(horizontal = 16.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        "${minutes}m",
                                        color = if (isSelected) Color.Black else Color.White,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (isEnabled) {
                TextButton(
                    onClick = onCancel,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFFF6B6B))
                ) {
                    Text("Cancel Timer", fontWeight = FontWeight.Medium)
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFB3B3B3))
            ) {
                Text("Close")
            }
        }
    )
}

// ═══════════════════════════════════════════════════════════════════════════════
// CUSTOM ICONS
// ═══════════════════════════════════════════════════════════════════════════════

private object PlayerIcons {
    val Pause: ImageVector by lazy {
        ImageVector.Builder(
            name = "Pause",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).addPath(
            pathData = androidx.compose.ui.graphics.vector.PathParser().parsePathString(
                "M6,4L10,4L10,20L6,20L6,4ZM14,4L18,4L18,20L14,20L14,4Z"
            ).toNodes(),
            fill = androidx.compose.ui.graphics.SolidColor(Color.White)
        ).build()
    }
    
    val SkipBack: ImageVector by lazy {
        ImageVector.Builder(
            name = "SkipBack",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).addPath(
            pathData = androidx.compose.ui.graphics.vector.PathParser().parsePathString(
                "M6,6L6,18L8,18L8,6L6,6ZM9.5,12L18,18L18,6L9.5,12Z"
            ).toNodes(),
            fill = androidx.compose.ui.graphics.SolidColor(Color.White)
        ).build()
    }
    
    val SkipForward: ImageVector by lazy {
        ImageVector.Builder(
            name = "SkipForward",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).addPath(
            pathData = androidx.compose.ui.graphics.vector.PathParser().parsePathString(
                "M6,18L14.5,12L6,6L6,18ZM16,6L16,18L18,18L18,6L16,6Z"
            ).toNodes(),
            fill = androidx.compose.ui.graphics.SolidColor(Color.White)
        ).build()
    }
    
    val Shuffle: ImageVector by lazy {
        ImageVector.Builder(
            name = "Shuffle",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).addPath(
            pathData = androidx.compose.ui.graphics.vector.PathParser().parsePathString(
                "M10.59,9.17L5.41,4L4,5.41L9.17,10.59L10.59,9.17ZM14.5,4L16.54,6.04L4,18.59L5.41,20L17.96,7.46L20,9.5L20,4L14.5,4ZM14.83,13.41L13.41,14.83L16.54,17.96L14.5,20L20,20L20,14.5L17.96,16.54L14.83,13.41Z"
            ).toNodes(),
            fill = androidx.compose.ui.graphics.SolidColor(Color.White)
        ).build()
    }
    
    val Repeat: ImageVector by lazy {
        ImageVector.Builder(
            name = "Repeat",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).addPath(
            pathData = androidx.compose.ui.graphics.vector.PathParser().parsePathString(
                "M7,7L17,7L17,10L21,6L17,2L17,5L5,5L5,11L7,11L7,7ZM17,17L7,17L7,14L3,18L7,22L7,19L19,19L19,13L17,13L17,17Z"
            ).toNodes(),
            fill = androidx.compose.ui.graphics.SolidColor(Color.White)
        ).build()
    }
    
    val Timer: ImageVector by lazy {
        ImageVector.Builder(
            name = "Timer",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).addPath(
            pathData = androidx.compose.ui.graphics.vector.PathParser().parsePathString(
                "M15,1L9,1L9,3L15,3L15,1ZM11,14L13,14L13,8L11,8L11,14ZM19.03,7.39L20.45,5.97C20.02,5.46 19.55,4.98 19.04,4.56L17.62,5.98C16.07,4.74 14.12,4 12,4C7.03,4 3,8.03 3,13C3,17.97 7.02,22 12,22C16.98,22 21,17.97 21,13C21,10.88 20.26,8.93 19.03,7.39ZM12,20C8.13,20 5,16.87 5,13C5,9.13 8.13,6 12,6C15.87,6 19,9.13 19,13C19,16.87 15.87,20 12,20Z"
            ).toNodes(),
            fill = androidx.compose.ui.graphics.SolidColor(Color.White)
        ).build()
    }
    
    val Queue: ImageVector by lazy {
        ImageVector.Builder(
            name = "Queue",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).addPath(
            pathData = androidx.compose.ui.graphics.vector.PathParser().parsePathString(
                "M3,13L11,13L11,11L3,11L3,13ZM3,17L11,17L11,15L3,15L3,17ZM3,7L3,9L11,9L11,7L3,7ZM15,13L15,7L13,7L13,13L15,13ZM21,10.5L15,14L21,17.5L21,10.5Z"
            ).toNodes(),
            fill = androidx.compose.ui.graphics.SolidColor(Color.White)
        ).build()
    }
}
