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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.ui.components.ArouraProfileIcon
import com.example.aroura.ui.components.ArouraSectionTitle
import com.example.aroura.ui.theme.*

// Data Model
data class CalmMediaItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val startColor: Color,
    val endColor: Color
)

// Mock Data with refined colors
val devotionalItems = listOf(
    CalmMediaItem("1", "Krishna Bhajan", "Divine Flute", "Devotional", Color(0xFF5C6BC0), Color(0xFF3949AB)),
    CalmMediaItem("2", "Mantras & Chants", "Om Chanting", "Devotional", Color(0xFFFFB74D), Color(0xFFF57C00)),
    CalmMediaItem("3", "Islamic Nasheeds", "Peaceful Sufi", "Devotional", Color(0xFF4DB6AC), Color(0xFF00897B))
)

val audioBookItems = listOf(
    CalmMediaItem("4", "Mahabharata", "Epic Saga", "AudioBook", Color(0xFFE57373), Color(0xFFD32F2F)),
    CalmMediaItem("5", "Bhagavad Gita", "Sacred Song", "AudioBook", Color(0xFFFFD54F), Color(0xFFFFA000)),
    CalmMediaItem("6", "Holy Quran", "Recitation", "AudioBook", Color(0xFF81C784), Color(0xFF388E3C))
)

val relaxationItems = listOf(
    CalmMediaItem("7", "Nature Sounds", "Forest Rain", "Relaxation", Color(0xFF81C784), Color(0xFF2E7D32)),
    CalmMediaItem("8", "Calm Music", "Deep Sleep", "Relaxation", Color(0xFF9575CD), Color(0xFF512DA8))
)

/**
 * Calm Screen - Premium Redesign
 * 
 * Features:
 * - Clean section hierarchy
 * - Premium media cards with hover effects
 * - Smooth entrance animations
 * - Consistent spacing and typography
 */
@Composable
fun CalmScreen(
    onItemClick: (CalmMediaItem) -> Unit, 
    onViewAllClick: (String, List<CalmMediaItem>) -> Unit,
    onProfileClick: () -> Unit
) {
    var showFilter by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(modifier = Modifier.fillMaxSize()) {
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
                    ArouraProfileIcon(onClick = onProfileClick)
                    
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

            // Devotional Section
            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(500, delayMillis = 150)) + slideInVertically(
                        initialOffsetY = { 20 },
                        animationSpec = tween(500, delayMillis = 150)
                    )
                ) {
                    Column {
                        PremiumSectionHeader(
                            title = "Devotional Songs",
                            onClick = { onViewAllClick("Devotional Songs", devotionalItems) }
                        )
                        Spacer(modifier = Modifier.height(ArouraSpacing.md.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(ArouraSpacing.md.dp)
                        ) {
                            items(devotionalItems) { item ->
                                PremiumSquareMediaCard(item, onItemClick)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
            }

            // Audio Books Section
            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(500, delayMillis = 250)) + slideInVertically(
                        initialOffsetY = { 20 },
                        animationSpec = tween(500, delayMillis = 250)
                    )
                ) {
                    Column {
                        PremiumSectionHeader(
                            title = "Audio Books",
                            onClick = { onViewAllClick("Audio Books", audioBookItems) }
                        )
                        Spacer(modifier = Modifier.height(ArouraSpacing.md.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(ArouraSpacing.md.dp)
                        ) {
                            items(audioBookItems) { item ->
                                PremiumSquareMediaCard(item, onItemClick)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
            }

            // Relaxation Section
            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(500, delayMillis = 350)) + slideInVertically(
                        initialOffsetY = { 20 },
                        animationSpec = tween(500, delayMillis = 350)
                    )
                ) {
                    Column {
                        PremiumSectionHeader(
                            title = "Relaxation Sounds",
                            onClick = { onViewAllClick("Relaxation Sounds", relaxationItems) }
                        )
                        Spacer(modifier = Modifier.height(ArouraSpacing.md.dp))
                    }
                }
            }
            
            items(relaxationItems) { item ->
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(500, delayMillis = 400))
                ) {
                    PremiumWideMediaCard(item, onItemClick)
                }
                Spacer(modifier = Modifier.height(ArouraSpacing.md.dp))
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
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "cardScale"
    )
    
    Box(
        modifier = Modifier
            .size(150.dp)
            .scale(scale)
            .clip(RoundedCornerShape(ArouraSpacing.cardRadius.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        item.startColor.copy(alpha = 0.7f),
                        item.endColor.copy(alpha = 0.9f)
                    )
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick(item) }
    ) {
        // Decorative Shine
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 20.dp, y = (-20).dp)
                .size(60.dp)
                .background(Color.White.copy(alpha = 0.15f), CircleShape)
        )
        
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
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "wideCardScale"
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .scale(scale)
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
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick(item) }
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
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = OffWhite.copy(alpha = 0.7f)
                )
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