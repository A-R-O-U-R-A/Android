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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aroura.ui.components.ArouraProfileIcon
import com.example.aroura.ui.components.ArouraSectionTitle
import com.example.aroura.ui.theme.*
import com.example.aroura.ui.viewmodels.CalmMediaItemData
import com.example.aroura.ui.viewmodels.CalmViewModel
import com.example.aroura.ui.viewmodels.CalmViewModelFactory

// Legacy Data Model (for backward compatibility with navigation)
data class CalmMediaItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val startColor: Color,
    val endColor: Color,
    val streamingUrl: String = "",
    val streamingUrlBackup: String? = null,
    val duration: Int = 0,
    val loopAllowed: Boolean = false,
    val sourceName: String = ""
)

// Extension to convert new model to legacy model for navigation
fun CalmMediaItemData.toLegacyItem(): CalmMediaItem = CalmMediaItem(
    id = id,
    title = title,
    subtitle = subtitle,
    category = category,
    startColor = startColor,
    endColor = endColor,
    streamingUrl = streamingUrl,
    streamingUrlBackup = streamingUrlBackup,
    duration = duration,
    loopAllowed = loopAllowed,
    sourceName = sourceName
)

/**
 * Calm Screen - Premium Redesign with Real Audio
 * 
 * Features:
 * - Real audio streaming from Freesound, Jamendo, Internet Archive, LibriVox
 * - Clean section hierarchy
 * - Premium media cards with hover effects
 * - Smooth entrance animations
 * - Loading states and error handling
 */
@Composable
fun CalmScreen(
    onItemClick: (CalmMediaItem) -> Unit, 
    onViewAllClick: (String, List<CalmMediaItem>) -> Unit,
    onProfileClick: () -> Unit,
    profilePictureUrl: String? = null
) {
    val context = LocalContext.current
    val viewModel: CalmViewModel = viewModel(factory = CalmViewModelFactory(context))
    
    val uiState by viewModel.uiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    var showFilter by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) { visible = true }

    Box(modifier = Modifier.fillMaxSize()) {
        // Main Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = ArouraSpacing.screenHorizontal.dp),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Header
            item {
                Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .systemBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ArouraProfileIcon(
                        onClick = onProfileClick,
                        profilePictureUrl = profilePictureUrl
                    )
                    
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(400))
                    ) {
                        Text(
                            text = "Calm",
                            style = MaterialTheme.typography.headlineMedium,
                            color = OffWhite,
                            fontWeight = FontWeight.Light
                        )
                    }
                    
                    IconButton(onClick = { showFilter = true }) {
                        Icon(
                            Icons.Default.Menu, 
                            contentDescription = "Filter", 
                            tint = TextDarkSecondary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(500, delayMillis = 100))
                ) {
                    Text(
                        text = "Let your heart quiet down.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextDarkSecondary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
                
                Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
            }
            
            // Loading Indicator
            if (isLoading && !uiState.isContentLoaded) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = MutedTeal,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Loading peaceful content...",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextDarkSecondary
                            )
                        }
                    }
                }
            }
            
            // Error State with Retry
            error?.let { errorMessage ->
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF3D2929)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = errorMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFE57373)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            TextButton(
                                onClick = { 
                                    viewModel.clearError()
                                    viewModel.loadAllContent() 
                                }
                            ) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = MutedTeal
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Retry", color = MutedTeal)
                            }
                        }
                    }
                }
            }

            // Devotional Section
            if (uiState.devotionalItems.isNotEmpty()) {
                item {
                    AnimatedVisibility(
                        visible = visible && uiState.isContentLoaded,
                        enter = fadeIn(tween(300)) + slideInVertically(
                            initialOffsetY = { 20 },
                            animationSpec = tween(300)
                        )
                    ) {
                        Column {
                            PremiumSectionHeader(
                                title = "Devotional Songs",
                                onClick = { 
                                    onViewAllClick(
                                        "Devotional Songs", 
                                        uiState.devotionalItems.map { it.toLegacyItem() }
                                    ) 
                                }
                            )
                            Spacer(modifier = Modifier.height(ArouraSpacing.md.dp))
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(ArouraSpacing.md.dp)
                            ) {
                                items(uiState.devotionalItems) { item ->
                                    PremiumSquareMediaCard(
                                        item = item.toLegacyItem(),
                                        onClick = { onItemClick(item.toLegacyItem()) }
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
                }
            }

            // Audio Books Section
            if (uiState.audiobookItems.isNotEmpty()) {
                item {
                    AnimatedVisibility(
                        visible = visible && uiState.isContentLoaded,
                        enter = fadeIn(tween(300, delayMillis = 50)) + slideInVertically(
                            initialOffsetY = { 20 },
                            animationSpec = tween(300, delayMillis = 50)
                        )
                    ) {
                        Column {
                            PremiumSectionHeader(
                                title = "Spiritual Audiobooks",
                                onClick = { 
                                    onViewAllClick(
                                        "Spiritual Audiobooks", 
                                        uiState.audiobookItems.map { it.toLegacyItem() }
                                    ) 
                                }
                            )
                            Spacer(modifier = Modifier.height(ArouraSpacing.md.dp))
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(ArouraSpacing.md.dp)
                            ) {
                                items(uiState.audiobookItems) { item ->
                                    PremiumSquareMediaCard(
                                        item = item.toLegacyItem(),
                                        onClick = { onItemClick(item.toLegacyItem()) }
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
                }
            }
            
            // Nature Sounds Section
            if (uiState.natureSounds.isNotEmpty()) {
                item {
                    AnimatedVisibility(
                        visible = visible && uiState.isContentLoaded,
                        enter = fadeIn(tween(300, delayMillis = 100)) + slideInVertically(
                            initialOffsetY = { 20 },
                            animationSpec = tween(300, delayMillis = 100)
                        )
                    ) {
                        Column {
                            PremiumSectionHeader(
                                title = "Nature Sounds",
                                onClick = { 
                                    onViewAllClick(
                                        "Nature Sounds", 
                                        uiState.natureSounds.map { it.toLegacyItem() }
                                    ) 
                                }
                            )
                            Spacer(modifier = Modifier.height(ArouraSpacing.md.dp))
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(ArouraSpacing.md.dp)
                            ) {
                                items(uiState.natureSounds) { item ->
                                    PremiumSquareMediaCard(
                                        item = item.toLegacyItem(),
                                        onClick = { onItemClick(item.toLegacyItem()) }
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
                }
            }
            
            // Calm Music Section
            if (uiState.calmMusic.isNotEmpty()) {
                item {
                    AnimatedVisibility(
                        visible = visible && uiState.isContentLoaded,
                        enter = fadeIn(tween(300, delayMillis = 150)) + slideInVertically(
                            initialOffsetY = { 20 },
                            animationSpec = tween(300, delayMillis = 150)
                        )
                    ) {
                        Column {
                            PremiumSectionHeader(
                                title = "Calm Music",
                                onClick = { 
                                    onViewAllClick(
                                        "Calm Music", 
                                        uiState.calmMusic.map { it.toLegacyItem() }
                                    ) 
                                }
                            )
                            Spacer(modifier = Modifier.height(ArouraSpacing.md.dp))
                        }
                    }
                }
                
                items(uiState.calmMusic.take(4)) { item ->
                    AnimatedVisibility(
                        visible = visible && uiState.isContentLoaded,
                        enter = fadeIn(tween(300, delayMillis = 200))
                    ) {
                        PremiumWideMediaCard(
                            item = item.toLegacyItem(),
                            onClick = { onItemClick(item.toLegacyItem()) }
                        )
                    }
                    Spacer(modifier = Modifier.height(ArouraSpacing.md.dp))
                }
            }

            // Relaxation Section (fallback)
            if (uiState.relaxationItems.isNotEmpty() && uiState.natureSounds.isEmpty()) {
                item {
                    AnimatedVisibility(
                        visible = visible && uiState.isContentLoaded,
                        enter = fadeIn(tween(500, delayMillis = 350)) + slideInVertically(
                            initialOffsetY = { 20 },
                            animationSpec = tween(500, delayMillis = 350)
                        )
                    ) {
                        Column {
                            PremiumSectionHeader(
                                title = "Relaxation Sounds",
                                onClick = { 
                                    onViewAllClick(
                                        "Relaxation Sounds", 
                                        uiState.relaxationItems.map { it.toLegacyItem() }
                                    ) 
                                }
                            )
                            Spacer(modifier = Modifier.height(ArouraSpacing.md.dp))
                        }
                    }
                }
                
                items(uiState.relaxationItems) { item ->
                    AnimatedVisibility(
                        visible = visible && uiState.isContentLoaded,
                        enter = fadeIn(tween(500, delayMillis = 400))
                    ) {
                        PremiumWideMediaCard(
                            item = item.toLegacyItem(),
                            onClick = { onItemClick(item.toLegacyItem()) }
                        )
                    }
                    Spacer(modifier = Modifier.height(ArouraSpacing.md.dp))
                }
            }
        }
        
        if (showFilter) {
            CalmFilterScreen(
                onClose = { showFilter = false },
                onApply = { _, _ -> showFilter = false }
            )
        }
    }
}

@Composable
private fun PremiumSectionHeader(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = OffWhite,
            fontWeight = FontWeight.SemiBold
        )
        TextButton(onClick = onClick) {
            Text(
                text = "View All",
                style = MaterialTheme.typography.labelMedium,
                color = MutedTeal
            )
        }
    }
}

@Composable
private fun PremiumSquareMediaCard(item: CalmMediaItem, onClick: (CalmMediaItem) -> Unit) {
    // Removed heavy press animation - use simpler opacity change
    Box(
        modifier = Modifier
            .size(150.dp)
            .clip(RoundedCornerShape(ArouraSpacing.cardRadius.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        item.startColor.copy(alpha = 0.7f),
                        item.endColor.copy(alpha = 0.9f)
                    )
                )
            )
            .clickable { onClick(item) }
    ) {
        // Decorative Shine
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 20.dp, y = (-20).dp)
                .size(60.dp)
                .background(Color.White.copy(alpha = 0.15f), CircleShape)
        )
        
        // Source Badge
        if (item.sourceName.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(ArouraSpacing.xs.dp)
                    .background(
                        Color.Black.copy(alpha = 0.4f),
                        RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = item.sourceName,
                    style = MaterialTheme.typography.labelSmall,
                    color = OffWhite.copy(alpha = 0.8f),
                    fontSize = 8.sp
                )
            }
        }
        
        // Play Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(ArouraSpacing.sm.dp)
                .size(36.dp)
                .background(Color.Black.copy(alpha = 0.25f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = OffWhite,
                modifier = Modifier.size(20.dp)
            )
        }

        // Title
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(ArouraSpacing.sm.dp)
                .padding(end = 40.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                color = OffWhite,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = OffWhite.copy(alpha = 0.8f),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun PremiumWideMediaCard(item: CalmMediaItem, onClick: (CalmMediaItem) -> Unit) {
    // Removed heavy press animation - use simpler approach
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(ArouraSpacing.cardRadius.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        item.startColor.copy(alpha = 0.5f),
                        item.endColor.copy(alpha = 0.7f)
                    )
                )
            )
            .border(
                1.dp,
                Brush.horizontalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.1f),
                        Color.Transparent
                    )
                ),
                RoundedCornerShape(ArouraSpacing.cardRadius.dp)
            )
            .clickable { onClick(item) }
            .padding(horizontal = ArouraSpacing.lg.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = OffWhite,
                    fontWeight = FontWeight.SemiBold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = OffWhite.copy(alpha = 0.7f)
                    )
                    if (item.sourceName.isNotEmpty()) {
                        Text(
                            text = " • ${item.sourceName}",
                            style = MaterialTheme.typography.labelSmall,
                            color = OffWhite.copy(alpha = 0.5f)
                        )
                    }
                }
            }
            
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.White.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = OffWhite
                )
            }
        }
    }
}

// Keep legacy functions for compatibility
@Composable
fun SectionHeader(title: String, onClick: () -> Unit) = PremiumSectionHeader(title, onClick)

@Composable
fun SquareMediaCard(item: CalmMediaItem, onClick: (CalmMediaItem) -> Unit) = PremiumSquareMediaCard(item, onClick)

@Composable
fun WideMediaCard(item: CalmMediaItem, onClick: (CalmMediaItem) -> Unit) = PremiumWideMediaCard(item, onClick)