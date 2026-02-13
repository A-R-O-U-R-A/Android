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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.data.api.SavedQuestAnswerEntry
import com.example.aroura.data.api.QuestSectionAnswerData
import com.example.aroura.ui.components.ArouraBackground
import com.example.aroura.ui.theme.*
import com.example.aroura.ui.viewmodels.ReflectViewModel

/**
 * Quest History Screen - Shows all saved quest answers grouped by quest
 * 
 * Follows the same premium design pattern as MoodHistoryScreen and AnxietyHistoryScreen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestHistoryScreen(
    onBack: () -> Unit,
    reflectViewModel: ReflectViewModel? = null
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    
    val questAnswers by reflectViewModel?.questAnswers?.collectAsState() ?: remember { mutableStateOf(emptyList()) }
    val questProgress by reflectViewModel?.questProgress?.collectAsState() ?: remember { mutableStateOf(null) }
    
    // Group answers by questId
    val groupedAnswers = remember(questAnswers) {
        questAnswers.groupBy { it.questId }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        ArouraBackground()
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = ArouraSpacing.screenHorizontal.dp),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Top Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = ArouraSpacing.md.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = OffWhite)
                    }
                    Spacer(modifier = Modifier.width(ArouraSpacing.sm.dp))
                    Text(
                        "Quest Journey",
                        style = MaterialTheme.typography.titleMedium,
                        color = OffWhite,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            // Summary Card
            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(500)) + slideInVertically(
                        initialOffsetY = { -20 },
                        animationSpec = tween(500, easing = EaseOutCubic)
                    )
                ) {
                    QuestSummaryCard(
                        totalSectionsCompleted = questAnswers.size,
                        totalQuestions = questAnswers.sumOf { it.answers.size },
                        badgeEarned = questProgress?.badgeEarned == true
                    )
                }
                Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
            }
            
            // Badge card if earned
            if (questProgress?.badgeEarned == true) {
                item {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(600, delayMillis = 100)) + scaleIn(
                            initialScale = 0.8f,
                            animationSpec = tween(600, delayMillis = 100, easing = EaseOutCubic)
                        )
                    ) {
                        BadgeEarnedCard()
                    }
                    Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
                }
            }
            
            // Empty state
            if (questAnswers.isEmpty()) {
                item {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(500, delayMillis = 200))
                    ) {
                        EmptyQuestState()
                    }
                }
            }
            
            // Grouped quest sections
            groupedAnswers.entries.forEachIndexed { questIndex, (questId, sections) ->
                val questTitle = sections.firstOrNull()?.questTitle ?: "Quest ${questIndex + 1}"
                
                item {
                    val delay = 200 + (questIndex * 100)
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(400, delayMillis = delay)) + slideInVertically(
                            initialOffsetY = { 30 },
                            animationSpec = tween(400, delayMillis = delay, easing = EaseOutCubic)
                        )
                    ) {
                        QuestGroupHeader(
                            questTitle = questTitle,
                            sectionsCompleted = sections.size,
                            totalQuestions = sections.sumOf { it.answers.size }
                        )
                    }
                    Spacer(modifier = Modifier.height(ArouraSpacing.sm.dp))
                }
                
                itemsIndexed(sections) { sectionIndex, entry ->
                    val delay = 300 + (questIndex * 100) + (sectionIndex * 80)
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(400, delayMillis = delay)) + slideInVertically(
                            initialOffsetY = { 30 },
                            animationSpec = tween(400, delayMillis = delay, easing = EaseOutCubic)
                        )
                    ) {
                        QuestAnswerCard(entry = entry)
                    }
                    Spacer(modifier = Modifier.height(ArouraSpacing.md.dp))
                }
                
                item {
                    Spacer(modifier = Modifier.height(ArouraSpacing.md.dp))
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SUMMARY CARD
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun QuestSummaryCard(
    totalSectionsCompleted: Int,
    totalQuestions: Int,
    badgeEarned: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        CalmingPeach.copy(alpha = 0.12f),
                        CalmingPeach.copy(alpha = 0.04f)
                    )
                ),
                RoundedCornerShape(20.dp)
            )
            .border(
                1.dp,
                CalmingPeach.copy(alpha = 0.2f),
                RoundedCornerShape(20.dp)
            )
            .padding(ArouraSpacing.xl.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (badgeEarned) "🏆 Quest Journey Complete!" else "Your Quest Journey",
                style = MaterialTheme.typography.titleMedium,
                color = if (badgeEarned) CalmingPeach else OffWhite,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryStatItem(
                    value = "$totalSectionsCompleted",
                    label = "Sections",
                    color = CalmingPeach
                )
                SummaryStatItem(
                    value = "$totalQuestions",
                    label = "Questions",
                    color = CalmingLavender
                )
                SummaryStatItem(
                    value = if (badgeEarned) "✓" else "—",
                    label = "Badge",
                    color = if (badgeEarned) CalmingGreen else TextDarkSecondary
                )
            }
        }
    }
}

@Composable
private fun SummaryStatItem(
    value: String,
    label: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextDarkSecondary
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// BADGE EARNED CARD
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun BadgeEarnedCard() {
    val infiniteTransition = rememberInfiniteTransition(label = "badgeGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFD700).copy(alpha = 0.15f),
                        Color(0xFFFFA500).copy(alpha = 0.08f)
                    )
                ),
                RoundedCornerShape(20.dp)
            )
            .border(
                1.dp,
                Color(0xFFFFD700).copy(alpha = glowAlpha),
                RoundedCornerShape(20.dp)
            )
            .padding(ArouraSpacing.xl.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Badge icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        Color(0xFFFFD700).copy(alpha = 0.2f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🏆",
                    fontSize = 28.sp
                )
            }
            
            Spacer(modifier = Modifier.width(ArouraSpacing.lg.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Self-Aware Badge",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "You've completed all 9 quest sections and earned this badge of self-discovery!",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextDarkSecondary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// QUEST GROUP HEADER
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun QuestGroupHeader(
    questTitle: String,
    sectionsCompleted: Int,
    totalQuestions: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = ArouraSpacing.sm.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(CalmingPeach, CircleShape)
        )
        Spacer(modifier = Modifier.width(ArouraSpacing.md.dp))
        Text(
            text = questTitle,
            style = MaterialTheme.typography.titleSmall,
            color = OffWhite,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "$sectionsCompleted sections · $totalQuestions answers",
            style = MaterialTheme.typography.labelSmall,
            color = TextDarkSecondary
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// ANSWER CARD (expandable)
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun QuestAnswerCard(entry: SavedQuestAnswerEntry) {
    var isExpanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "cardScale"
    )
    
    val completedDate = entry.completedAt?.let { formatQuestDate(it) } ?: ""
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        CalmingLavender.copy(alpha = 0.08f),
                        DeepSurface.copy(alpha = 0.5f)
                    )
                )
            )
            .border(
                1.dp,
                CalmingLavender.copy(alpha = 0.15f),
                RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { isExpanded = !isExpanded }
            .padding(ArouraSpacing.lg.dp)
    ) {
        Column {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(CalmingLavender.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = CalmingLavender,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(ArouraSpacing.md.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entry.sectionTitle.ifEmpty { "Section ${entry.sectionId}" },
                        style = MaterialTheme.typography.titleSmall,
                        color = OffWhite,
                        fontWeight = FontWeight.Medium
                    )
                    if (completedDate.isNotEmpty()) {
                        Text(
                            text = completedDate,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextDarkSecondary
                        )
                    }
                }
                
                // Expand indicator
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp 
                        else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = TextDarkSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            // Answers list (expandable)
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(tween(200)) + expandVertically(tween(300)),
                exit = fadeOut(tween(200)) + shrinkVertically(tween(300))
            ) {
                Column(
                    modifier = Modifier.padding(top = ArouraSpacing.md.dp)
                ) {
                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.06f),
                        modifier = Modifier.padding(bottom = ArouraSpacing.md.dp)
                    )
                    
                    entry.answers.forEachIndexed { index, answer ->
                        AnswerItem(
                            questionNumber = index + 1,
                            questionText = answer.questionText,
                            answerText = answer.answer
                        )
                        if (index < entry.answers.lastIndex) {
                            Spacer(modifier = Modifier.height(ArouraSpacing.md.dp))
                        }
                    }
                }
            }
            
            // Collapsed answer count
            if (!isExpanded && entry.answers.isNotEmpty()) {
                Spacer(modifier = Modifier.height(ArouraSpacing.sm.dp))
                Text(
                    text = "${entry.answers.size} answers · Tap to expand",
                    style = MaterialTheme.typography.labelSmall,
                    color = CalmingLavender.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun AnswerItem(
    questionNumber: Int,
    questionText: String,
    answerText: String
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Question number badge
        Box(
            modifier = Modifier
                .size(22.dp)
                .background(
                    CalmingPeach.copy(alpha = 0.15f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$questionNumber",
                style = MaterialTheme.typography.labelSmall,
                color = CalmingPeach,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp
            )
        }
        
        Spacer(modifier = Modifier.width(ArouraSpacing.md.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = questionText,
                style = MaterialTheme.typography.bodySmall,
                color = TextDarkSecondary,
                lineHeight = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .background(
                        MutedTeal.copy(alpha = 0.1f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = answerText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedTeal,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// EMPTY STATE
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun EmptyQuestState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = ArouraSpacing.xxl.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(CalmingPeach.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = CalmingPeach,
                    modifier = Modifier.size(36.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
            
            Text(
                text = "No quest answers yet",
                style = MaterialTheme.typography.titleMedium,
                color = OffWhite,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(ArouraSpacing.sm.dp))
            
            Text(
                text = "Complete quest sections to see\nyour answers here",
                style = MaterialTheme.typography.bodyMedium,
                color = TextDarkSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// DATE FORMATTER
// ═══════════════════════════════════════════════════════════════════════════════

private fun formatQuestDate(dateString: String): String {
    return try {
        val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
        inputFormat.isLenient = true
        val date = inputFormat.parse(dateString.substringBefore('.').substringBefore('Z'))
        val outputFormat = java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault())
        date?.let { outputFormat.format(it) } ?: dateString.take(10)
    } catch (e: Exception) {
        dateString.take(10)
    }
}
