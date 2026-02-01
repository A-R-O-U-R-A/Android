package com.example.aroura.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.aroura.ui.components.ArouraBackground
import com.example.aroura.ui.theme.*

/**
 * Audio List Screen - Premium Redesign
 * 
 * Features:
 * - Staggered entrance animations for grid items
 * - Premium header with fade transitions
 * - Unified spacing with ArouraSpacing
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioListScreen(
    title: String,
    items: List<CalmMediaItem>,
    onItemClick: (CalmMediaItem) -> Unit,
    onBack: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(modifier = Modifier.fillMaxSize()) {
        ArouraBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ArouraSpacing.sm.dp, vertical = ArouraSpacing.md.dp),
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
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = OffWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            // Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(
                    horizontal = ArouraSpacing.screenHorizontal.dp,
                    vertical = ArouraSpacing.md.dp
                ),
                verticalArrangement = Arrangement.spacedBy(ArouraSpacing.md.dp),
                horizontalArrangement = Arrangement.spacedBy(ArouraSpacing.md.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(items) { index, item ->
                    val delay = 100 + (index * 50)
                    
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(400, delayMillis = delay)) + scaleIn(
                            initialScale = 0.9f,
                            animationSpec = tween(400, delayMillis = delay, easing = EaseOutCubic)
                        )
                    ) {
                        PremiumSquareMediaCard(item = item, onClick = onItemClick)
                    }
                }
            }
        }
    }
}

// Use the shared PremiumSquareMediaCard from CalmScreen
@Composable
private fun PremiumSquareMediaCard(item: CalmMediaItem, onClick: (CalmMediaItem) -> Unit) {
    SquareMediaCard(item = item, onClick = onClick)
}
