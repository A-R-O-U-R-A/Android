package com.example.aroura.ui.screens.reflect

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.aroura.data.ReflectSection
import com.example.aroura.data.ReflectTestId
import com.example.aroura.data.ReflectTestRepository
import com.example.aroura.ui.components.ArouraBackground
import com.example.aroura.ui.components.reflect.ReflectSectionHeader
import com.example.aroura.ui.components.reflect.ReflectTestCard
import com.example.aroura.ui.theme.*

/**
 * Reflect Library Screen - Premium Test Collection
 * 
 * A beautiful, scrollable library organized into 7 sections
 * with 37+ psychological assessments. Each section has
 * staggered entrance animations and premium card design.
 */
@Composable
fun ReflectLibraryScreen(
    completedTests: Set<ReflectTestId> = emptySet(),
    onBack: () -> Unit = {},
    onTestClick: (ReflectTestId) -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    
    val sections = ReflectSection.entries
    
    Box(modifier = Modifier.fillMaxSize()) {
        ArouraBackground()
        
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
                    // Back button
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(DeepSurface.copy(alpha = 0.5f))
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = OffWhite,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(400))
                    ) {
                        Text(
                            text = "Self-Discovery",
                            style = MaterialTheme.typography.headlineMedium,
                            color = OffWhite,
                            fontWeight = FontWeight.Light
                        )
                    }
                    
                    Spacer(modifier = Modifier.size(48.dp))
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(500, delayMillis = 100))
                ) {
                    Text(
                        text = "Discover yourself through reflection.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextDarkSecondary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
                
                Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
                
                // Stats summary
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(500, delayMillis = 200))
                ) {
                    ReflectStatsCard(
                        completedCount = completedTests.size,
                        totalCount = ReflectTestRepository.allTests.size
                    )
                }
                
                Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
            }
            
            // Sections with tests
            sections.forEachIndexed { sectionIndex, section ->
                val testsInSection = ReflectTestRepository.getTestsBySection(section)
                val sectionDelay = 300 + (sectionIndex * 100)
                
                // Section Header
                item {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(400, delayMillis = sectionDelay)) + slideInHorizontally(
                            initialOffsetX = { -30 },
                            animationSpec = tween(400, delayMillis = sectionDelay, easing = EaseOutCubic)
                        )
                    ) {
                        ReflectSectionHeader(section = section)
                    }
                    
                    Spacer(modifier = Modifier.height(ArouraSpacing.sm.dp))
                }
                
                // Tests in this section
                itemsIndexed(testsInSection) { testIndex, test ->
                    val testDelay = sectionDelay + 50 + (testIndex * 40)
                    
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(350, delayMillis = testDelay)) + slideInVertically(
                            initialOffsetY = { 20 },
                            animationSpec = tween(350, delayMillis = testDelay, easing = EaseOutCubic)
                        )
                    ) {
                        ReflectTestCard(
                            test = test,
                            isCompleted = completedTests.contains(test.id),
                            onClick = { onTestClick(test.id) }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(ArouraSpacing.sm.dp))
                }
                
                // Section spacing
                item {
                    Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
                }
            }
        }
    }
}

@Composable
private fun ReflectStatsCard(
    completedCount: Int,
    totalCount: Int
) {
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f
    
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label = "statsProgress"
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                DeepSurface.copy(alpha = 0.5f),
                androidx.compose.foundation.shape.RoundedCornerShape(ArouraSpacing.cardRadius.dp)
            )
            .padding(ArouraSpacing.lg.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Your Journey",
                    style = MaterialTheme.typography.titleMedium,
                    color = OffWhite,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$completedCount of $totalCount assessments completed",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextDarkSecondary
                )
                
                Spacer(modifier = Modifier.height(ArouraSpacing.md.dp))
                
                // Progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(
                            MidnightCharcoal.copy(alpha = 0.5f),
                            androidx.compose.foundation.shape.RoundedCornerShape(3.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .fillMaxHeight()
                            .background(
                                MutedTeal,
                                androidx.compose.foundation.shape.RoundedCornerShape(3.dp)
                            )
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(ArouraSpacing.lg.dp))
            
            // Percentage circle
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        MutedTeal.copy(alpha = 0.15f),
                        androidx.compose.foundation.shape.CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${(animatedProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = MutedTeal,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
