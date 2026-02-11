package com.example.aroura.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aroura.ui.components.ArouraProfileIcon
import com.example.aroura.ui.theme.ArouraSpacing
import com.example.aroura.ui.theme.MidnightCharcoal
import com.example.aroura.ui.theme.MutedTeal
import com.example.aroura.ui.theme.OffWhite
import com.example.aroura.ui.theme.TextDarkSecondary
import com.example.aroura.ui.viewmodels.CalmMediaItemData
import com.example.aroura.ui.viewmodels.CalmTab
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
 * Calm Screen - Optimized & Premium Design
 * 
 * PERFORMANCE OPTIMIZATIONS:
 * - Lazy loading per tab (only loads when selected)
 * - Stable keys for LazyColumn items
 * - Minimal recompositions with remember
 * 
 * Categories: Nature | Ambient | Meditation | Sleep | Focus | ASMR | Music
 * Sources: Freesound (sounds), Jamendo (music)
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
    val selectedTab by viewModel.selectedTab.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
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
                    
                    IconButton(onClick = { isSearching = !isSearching }) {
                        Icon(
                            if (isSearching) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = if (isSearching) "Close" else "Search", 
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
                        text = "Let your mind find peace.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextDarkSecondary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
                
                Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
            }
            
            // Search Bar
            item {
                AnimatedVisibility(
                    visible = isSearching,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { 
                            searchQuery = it
                            if (it.length >= 2) {
                                viewModel.searchAudio(it)
                            } else if (it.isEmpty()) {
                                viewModel.clearSearch()
                            }
                        },
                        placeholder = { Text("Search calm sounds...", color = TextDarkSecondary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MutedTeal,
                            unfocusedBorderColor = Color(0xFF2A2A2A),
                            focusedTextColor = OffWhite,
                            unfocusedTextColor = OffWhite,
                            cursorColor = MutedTeal
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Search, "Search", tint = TextDarkSecondary)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { 
                                    searchQuery = ""
                                    viewModel.clearSearch()
                                }) {
                                    Icon(Icons.Default.Close, "Clear", tint = TextDarkSecondary)
                                }
                            }
                        }
                    )
                }
            }
            
            // Tab Row
            item {
                AnimatedVisibility(
                    visible = visible && !uiState.isSearchActive,
                    enter = fadeIn(tween(300))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CalmTab.entries.forEach { tab ->
                            CalmTabChip(
                                tab = tab,
                                isSelected = selectedTab == tab,
                                onClick = { viewModel.selectTab(tab) }
                            )
                        }
                    }
                }
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
                item(key = "error") {
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
                                    viewModel.refreshCurrentTab() 
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

            // Search Results
            if (uiState.isSearchActive && uiState.searchResults.isNotEmpty()) {
                item(key = "search_header") {
                    Text(
                        text = "Search Results",
                        style = MaterialTheme.typography.titleMedium,
                        color = OffWhite,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                itemsIndexed(
                    items = uiState.searchResults.chunked(2),
                    key = { index, _ -> "search_row_$index" }
                ) { _, rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { item ->
                            PremiumSquareMediaCard(
                                item = item.toLegacyItem(),
                                onClick = { onItemClick(item.toLegacyItem()) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            
            // Tab Content - Optimized with stable keys
            if (!uiState.isSearchActive) {
                val currentItems = viewModel.getCurrentTabItems()
                
                if (isLoading && currentItems.isEmpty()) {
                    item(key = "loading_tab") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(
                                    color = MutedTeal,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Loading ${selectedTab.displayName}...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextDarkSecondary
                                )
                            }
                        }
                    }
                } else if (currentItems.isNotEmpty()) {
                    item(key = "section_header_${selectedTab.name}") {
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(tween(300)) + slideInVertically(
                                initialOffsetY = { 20 },
                                animationSpec = tween(300)
                            )
                        ) {
                            Column {
                                PremiumSectionHeader(
                                    title = selectedTab.displayName,
                                    onClick = { 
                                        onViewAllClick(
                                            selectedTab.displayName, 
                                            currentItems.map { it.toLegacyItem() }
                                        ) 
                                    }
                                )
                                Spacer(modifier = Modifier.height(ArouraSpacing.md.dp))
                            }
                        }
                    }
                    
                    // Grid of items with stable keys
                    itemsIndexed(
                        items = currentItems.chunked(2),
                        key = { index, _ -> "${selectedTab.name}_row_$index" }
                    ) { _, rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowItems.forEach { item ->
                                PremiumSquareMediaCard(
                                    item = item.toLegacyItem(),
                                    onClick = { onItemClick(item.toLegacyItem()) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                } else if (!isLoading) {
                    item(key = "empty_state") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = selectedTab.icon,
                                    fontSize = 48.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No ${selectedTab.displayName.lowercase()} sounds yet",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextDarkSecondary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                TextButton(onClick = { viewModel.refreshCurrentTab() }) {
                                    Icon(Icons.Default.Refresh, null, tint = MutedTeal)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Refresh", color = MutedTeal)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// TAB CHIP - Premium Design with Icons
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun CalmTabChip(
    tab: CalmTab,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MutedTeal else Color.Transparent,
        animationSpec = tween(200),
        label = "tabBgColor"
    )
    
    val textColor by animateColorAsState(
        targetValue = if (isSelected) MidnightCharcoal else TextDarkSecondary,
        animationSpec = tween(200),
        label = "tabTextColor"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = 0.7f),
        label = "tabScale"
    )
    
    Surface(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        color = backgroundColor,
        shape = RoundedCornerShape(24.dp),
        border = if (!isSelected) BorderStroke(
            1.dp, 
            Color(0xFF3A3A3A)
        ) else null,
        shadowElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = tab.icon,
                fontSize = 14.sp
            )
            Text(
                text = tab.displayName,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SECTION HEADER
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun PremiumSectionHeader(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
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
                style = MaterialTheme.typography.bodySmall,
                color = MutedTeal
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// MEDIA CARDS
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun PremiumSquareMediaCard(
    item: CalmMediaItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.7f),
        label = "cardScale"
    )
    
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(item.startColor, item.endColor)
                    )
                )
        ) {
            // Play button overlay
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(32.dp)
                    .background(
                        Color.Black.copy(alpha = 0.3f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            
            // Content
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 11.sp
                )
                
                if (item.duration > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatDuration(item.duration),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 10.sp
                    )
                }
            }
            
            // Loop indicator
            if (item.loopAllowed) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .background(
                            Color.Black.copy(alpha = 0.3f),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "∞",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PremiumCompactCard(
    item: CalmMediaItem,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.7f),
        label = "compactScale"
    )
    
    Card(
        modifier = Modifier
            .width(140.dp)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(item.startColor, item.endColor)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 12.sp
                )
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 10.sp
                )
            }
            
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(24.dp)
                    .background(Color.Black.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// UTILITY FUNCTIONS
// ═══════════════════════════════════════════════════════════════════════════════

private fun formatDuration(seconds: Int): String {
    if (seconds <= 0) return ""
    val mins = seconds / 60
    val secs = seconds % 60
    return if (mins >= 60) {
        val hrs = mins / 60
        val remainingMins = mins % 60
        "${hrs}h ${remainingMins}m"
    } else {
        "${mins}:${secs.toString().padStart(2, '0')}"
    }
}


