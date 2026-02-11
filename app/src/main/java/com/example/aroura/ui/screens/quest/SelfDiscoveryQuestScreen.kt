package com.example.aroura.ui.screens.quest

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Self-Discovery Quest Screen
 * 
 * A journey-style screen that shows 3 quests:
 * 1. Emotional Awareness Quest (3 tests)
 * 2. Mindset & Growth Quest (3 tests)
 * 3. Core Personality & Energy Quest (3 tests)
 * 
 * Completing all 3 unlocks "Self-Aware" badge
 */

// ═══════════════════════════════════════════════════════════════════════════════
// DATA MODELS
// ═══════════════════════════════════════════════════════════════════════════════

data class QuestData(
    val id: String,
    val number: Int,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val color: Color,
    val tests: List<QuestTest>,
    val resultType: String,
    val isUnlocked: Boolean = true,
    val isCompleted: Boolean = false
)

data class QuestTest(
    val id: String,
    val title: String,
    val description: String,
    val questionCount: Int,
    val isCompleted: Boolean = false
)

// Quest definitions based on sd.md
val selfDiscoveryQuests = listOf(
    QuestData(
        id = "emotional_awareness",
        number = 1,
        title = "Emotional Awareness",
        subtitle = "Understand Your Inner World",
        emoji = "🌿",
        color = CalmingGreen,
        tests = listOf(
            QuestTest("emotional_clarity", "Emotional Clarity", "Identify your dominant emotion and triggers", 8),
            QuestTest("pattern_recognition", "Pattern Recognition", "Discover when you feel most stressed", 8),
            QuestTest("emotional_strength", "Emotional Strength Mapping", "Find your coping strengths", 8)
        ),
        resultType = "Emotional Type"
    ),
    QuestData(
        id = "mindset_growth",
        number = 2,
        title = "Mindset & Growth",
        subtitle = "Discover Your Thinking Style",
        emoji = "🧠",
        color = CalmingLavender,
        tests = listOf(
            QuestTest("self_talk", "Self-Talk Analysis", "Understand your inner critic", 8),
            QuestTest("fear_risk", "Fear & Risk Mapping", "Explore your comfort zones", 8),
            QuestTest("growth_indicator", "Growth Indicator", "Measure your adaptability", 8)
        ),
        resultType = "Mindset Type"
    ),
    QuestData(
        id = "core_personality",
        number = 3,
        title = "Core Personality & Energy",
        subtitle = "Who Are You at Your Core?",
        emoji = "💛",
        color = CalmingPeach,
        tests = listOf(
            QuestTest("social_energy", "Social Energy", "Discover your social style", 8),
            QuestTest("emotional_energy", "Emotional Energy Type", "How you process decisions", 8),
            QuestTest("core_motivation", "Core Motivation", "Find what truly drives you", 8)
        ),
        resultType = "Personality Energy Type"
    )
)

// ═══════════════════════════════════════════════════════════════════════════════
// MAIN SCREEN
// ═══════════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelfDiscoveryQuestScreen(
    onClose: () -> Unit,
    onStartTest: (questId: String, testId: String) -> Unit,
    completedQuests: Set<String> = emptySet(),
    completedTests: Set<String> = emptySet()
) {
    var visible by remember { mutableStateOf(false) }
    var expandedQuestId by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        delay(100)
        visible = true
    }
    
    // Calculate overall progress
    val totalTests = selfDiscoveryQuests.sumOf { it.tests.size }
    val completedTestCount = completedTests.size
    val allQuestsCompleted = completedQuests.size == 3
    
    // Background gradient
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MidnightCharcoal,
            Color(0xFF1A2530),
            Color(0xFF1D2A35)
        )
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Header
            item {
                QuestHeader(
                    onBack = onClose,
                    visible = visible
                )
            }
            
            // Progress section
            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(500, delayMillis = 200)) + slideInVertically(
                        initialOffsetY = { 30 },
                        animationSpec = tween(500, delayMillis = 200, easing = EaseOutCubic)
                    )
                ) {
                    OverallProgressCard(
                        completedQuests = completedQuests.size,
                        totalQuests = 3,
                        completedTests = completedTestCount,
                        totalTests = totalTests,
                        badgeUnlocked = allQuestsCompleted
                    )
                }
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
            
            // Quest cards
            itemsIndexed(selfDiscoveryQuests) { index, quest ->
                val delay = 300 + (index * 100)
                val isExpanded = expandedQuestId == quest.id
                val questCompletedTests = quest.tests.count { completedTests.contains(it.id) }
                val isQuestCompleted = questCompletedTests == quest.tests.size
                val previousQuestCompleted = when (index) {
                    0 -> true
                    else -> completedQuests.contains(selfDiscoveryQuests[index - 1].id)
                }
                
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(400, delayMillis = delay)) + slideInVertically(
                        initialOffsetY = { 40 },
                        animationSpec = tween(400, delayMillis = delay, easing = EaseOutCubic)
                    )
                ) {
                    QuestCard(
                        quest = quest.copy(
                            isCompleted = isQuestCompleted,
                            isUnlocked = previousQuestCompleted
                        ),
                        completedTests = quest.tests.map { completedTests.contains(it.id) },
                        isExpanded = isExpanded,
                        onToggleExpand = {
                            expandedQuestId = if (isExpanded) null else quest.id
                        },
                        onStartTest = { testId ->
                            onStartTest(quest.id, testId)
                        }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Badge unlock celebration (if all completed)
            if (allQuestsCompleted) {
                item {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(600, delayMillis = 700)) + scaleIn(
                            initialScale = 0.8f,
                            animationSpec = tween(600, delayMillis = 700)
                        )
                    ) {
                        BadgeUnlockedCard()
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// COMPONENTS
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun QuestHeader(
    onBack: () -> Unit,
    visible: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp)
    ) {
        // Back button
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(400))
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(DeepSurface.copy(alpha = 0.5f))
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = OffWhite
                )
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Title
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(500, delayMillis = 100)) + slideInVertically(
                initialOffsetY = { 20 },
                animationSpec = tween(500, delayMillis = 100, easing = EaseOutCubic)
            )
        ) {
            Column {
                Text(
                    text = "Self-Discovery Quest",
                    style = MaterialTheme.typography.headlineMedium,
                    color = OffWhite,
                    fontWeight = FontWeight.Light
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Complete 3 quests to unlock your \"Self-Aware\" badge",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDarkSecondary,
                    fontWeight = FontWeight.Light
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun OverallProgressCard(
    completedQuests: Int,
    totalQuests: Int,
    completedTests: Int,
    totalTests: Int,
    badgeUnlocked: Boolean
) {
    val progress = completedTests.toFloat() / totalTests
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(
            containerColor = DeepSurface.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Your Progress",
                        style = MaterialTheme.typography.titleMedium,
                        color = OffWhite,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "$completedQuests of $totalQuests quests completed",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextDarkSecondary
                    )
                }
                
                if (badgeUnlocked) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(CalmingGreen.copy(alpha = 0.2f))
                            .padding(8.dp)
                    ) {
                        Text("🏅", fontSize = 24.sp)
                    }
                } else {
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.titleLarge,
                        color = MutedTeal,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White.copy(alpha = 0.1f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(MutedTeal, CalmingGreen)
                            )
                        )
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "$completedTests of $totalTests tests completed",
                style = MaterialTheme.typography.labelSmall,
                color = TextDarkTertiary
            )
        }
    }
}

@Composable
private fun QuestCard(
    quest: QuestData,
    completedTests: List<Boolean>,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onStartTest: (testId: String) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "cardScale"
    )
    
    val completedCount = completedTests.count { it }
    val isLocked = !quest.isUnlocked
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = !isLocked
            ) { onToggleExpand() },
        colors = CardDefaults.cardColors(
            containerColor = if (isLocked) 
                DeepSurface.copy(alpha = 0.3f) 
            else 
                DeepSurface.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Quest number badge
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(quest.color.copy(alpha = if (isLocked) 0.1f else 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (quest.isCompleted) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = CalmingGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = quest.emoji,
                                fontSize = 24.sp,
                                modifier = Modifier.alpha(if (isLocked) 0.5f else 1f)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Quest ${quest.number}",
                                style = MaterialTheme.typography.labelSmall,
                                color = quest.color.copy(alpha = if (isLocked) 0.5f else 1f),
                                fontWeight = FontWeight.Medium
                            )
                            if (isLocked) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = "Locked",
                                    tint = TextDarkTertiary,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                        Text(
                            text = quest.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = OffWhite.copy(alpha = if (isLocked) 0.5f else 1f),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = quest.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextDarkSecondary.copy(alpha = if (isLocked) 0.5f else 1f)
                        )
                    }
                }
                
                if (!isLocked) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = TextDarkSecondary
                    )
                }
            }
            
            // Progress indicator
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quest.tests.forEachIndexed { index, _ ->
                    val isTestCompleted = completedTests.getOrNull(index) == true
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (isTestCompleted) CalmingGreen 
                                else Color.White.copy(alpha = if (isLocked) 0.05f else 0.1f)
                            )
                    )
                }
            }
            
            // Expanded tests list
            AnimatedVisibility(
                visible = isExpanded && !isLocked,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 20.dp)) {
                    quest.tests.forEachIndexed { index, test ->
                        val isCompleted = completedTests.getOrNull(index) == true
                        val previousCompleted = index == 0 || completedTests.getOrNull(index - 1) == true
                        
                        TestItem(
                            test = test,
                            isCompleted = isCompleted,
                            isUnlocked = previousCompleted,
                            onStart = { onStartTest(test.id) }
                        )
                        
                        if (index < quest.tests.size - 1) {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TestItem(
    test: QuestTest,
    isCompleted: Boolean,
    isUnlocked: Boolean,
    onStart: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "testScale"
    )
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.03f))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = isUnlocked && !isCompleted
            ) { onStart() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Status icon
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isCompleted -> CalmingGreen.copy(alpha = 0.2f)
                        isUnlocked -> MutedTeal.copy(alpha = 0.2f)
                        else -> Color.White.copy(alpha = 0.05f)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            when {
                isCompleted -> Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = CalmingGreen,
                    modifier = Modifier.size(16.dp)
                )
                !isUnlocked -> Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    tint = TextDarkTertiary,
                    modifier = Modifier.size(14.dp)
                )
                else -> Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = MutedTeal,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = test.title,
                style = MaterialTheme.typography.bodyMedium,
                color = OffWhite.copy(alpha = if (isUnlocked) 1f else 0.5f),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = test.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextDarkSecondary.copy(alpha = if (isUnlocked) 1f else 0.5f)
            )
        }
        
        Text(
            text = "${test.questionCount} Q",
            style = MaterialTheme.typography.labelSmall,
            color = TextDarkTertiary
        )
    }
}

@Composable
private fun BadgeUnlockedCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(
            containerColor = CalmingGreen.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🏅", fontSize = 48.sp)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Self-Aware Badge Unlocked!",
                style = MaterialTheme.typography.titleLarge,
                color = OffWhite,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "You've completed your self-discovery journey. Your personalized summary is now available in Reflect.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextDarkSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}
