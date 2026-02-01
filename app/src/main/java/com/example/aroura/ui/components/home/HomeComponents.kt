package com.example.aroura.ui.components.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.ui.theme.*

/**
 * Premium Home Components
 * 
 * A unified design system for the Home screen featuring:
 * - Calm, emotionally safe aesthetic
 * - Subtle breathing animations
 * - Premium card designs with layered depth
 * - Thoughtful micro-interactions
 */

// ═══════════════════════════════════════════════════════════════════════════════
// SECTION 1: GREETING & EMOTIONAL CHECK-IN
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun GreetingSection(
    userName: String,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "Good Morning"
        hour < 17 -> "Good Afternoon"
        else -> "Good Evening"
    }
    
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = greeting,
                style = MaterialTheme.typography.bodyLarge,
                color = TextDarkSecondary,
                fontWeight = FontWeight.Light
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineLarge,
                color = OffWhite,
                fontWeight = FontWeight.SemiBold
            )
        }
        
        // Premium profile button
        PremiumProfileButton(onClick = onProfileClick)
    }
}

@Composable
fun PremiumProfileButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "profileScale"
    )
    
    Box(
        modifier = modifier
            .size(48.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MutedTeal.copy(alpha = 0.15f),
                        SoftBlue.copy(alpha = 0.1f)
                    )
                )
            )
            .border(
                1.dp,
                Color.White.copy(alpha = 0.08f),
                CircleShape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Person,
            contentDescription = "Profile",
            tint = OffWhite,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
fun EmotionalCheckInSection(
    selectedMood: Int,
    onMoodSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    
    // Mood data - using calming, supportive labels
    val moods = listOf(
        MoodData("😔", "Struggling", GentleError.copy(alpha = 0.3f)),
        MoodData("😐", "Meh", TextDarkSecondary.copy(alpha = 0.3f)),
        MoodData("🙂", "Okay", CalmingGreen.copy(alpha = 0.3f)),
        MoodData("😊", "Good", MutedTeal.copy(alpha = 0.3f)),
        MoodData("🤩", "Amazing", CalmingPeach.copy(alpha = 0.3f))
    )
    
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "How are you feeling?",
            style = MaterialTheme.typography.titleMedium,
            color = OffWhite,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            moods.forEachIndexed { index, mood ->
                val isSelected = selectedMood == index
                
                MoodButton(
                    mood = mood,
                    isSelected = isSelected,
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onMoodSelected(index)
                    }
                )
            }
        }
    }
}

@Composable
private fun MoodButton(
    mood: MoodData,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "moodScale"
    )
    
    val glowAlpha by animateFloatAsState(
        targetValue = if (isSelected) 0.4f else 0f,
        animationSpec = tween(300),
        label = "glowAlpha"
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Glow effect
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .blur(16.dp)
                        .alpha(glowAlpha)
                        .background(mood.glowColor, CircleShape)
                )
            }
            
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) mood.glowColor
                        else DeepSurface.copy(alpha = 0.6f)
                    )
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) MutedTeal else Color.White.copy(alpha = 0.06f),
                        shape = CircleShape
                    )
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                Text(text = mood.emoji, fontSize = 26.sp)
            }
        }
        
        AnimatedVisibility(
            visible = isSelected,
            enter = fadeIn(tween(200)) + expandVertically(),
            exit = fadeOut(tween(150)) + shrinkVertically()
        ) {
            Text(
                text = mood.label,
                style = MaterialTheme.typography.labelSmall,
                color = MutedTeal,
                modifier = Modifier.padding(top = 8.dp),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private data class MoodData(
    val emoji: String,
    val label: String,
    val glowColor: Color
)

// ═══════════════════════════════════════════════════════════════════════════════
// SECTION 2: PRIMARY CTA - TALK TO AROURA
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun TalkToArouraCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "arouraCardScale"
    )
    
    // Breathing animation for the glow
    val infiniteTransition = rememberInfiniteTransition(label = "arouraBreathing")
    val breathingAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathingGlow"
    )
    
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathingScale"
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        // Outer glow effect
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .scale(breathingScale)
                .blur(24.dp)
                .alpha(breathingAlpha)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            SoftBlue.copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    ),
                    RoundedCornerShape(28.dp)
                )
        )
        
        // Main card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF1A2530),
                                Color(0xFF141B24)
                            )
                        )
                    )
                    .border(
                        1.dp,
                        Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.1f),
                                Color.White.copy(alpha = 0.02f)
                            )
                        ),
                        RoundedCornerShape(24.dp)
                    )
            ) {
                // Subtle inner glow
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = 30.dp)
                        .size(140.dp)
                        .alpha(breathingAlpha * 2)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    SoftBlue.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        )
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icon with breathing animation
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .scale(breathingScale * 0.98f)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        SoftBlue.copy(alpha = 0.25f),
                                        MutedTeal.copy(alpha = 0.15f)
                                    )
                                ),
                                CircleShape
                            )
                            .border(
                                1.dp,
                                SoftBlue.copy(alpha = 0.2f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = SoftBlue,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(20.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Talk to Aroura",
                            style = MaterialTheme.typography.titleLarge,
                            color = OffWhite,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "I'm here to listen, anytime.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextDarkSecondary,
                            fontWeight = FontWeight.Light
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                SoftBlue.copy(alpha = 0.1f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = SoftBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SECTION 3: FIND A SPECIALIST
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun FindSpecialistCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "specialistScale"
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            CalmingGreen.copy(alpha = 0.08f),
                            CalmingGreen.copy(alpha = 0.04f)
                        )
                    )
                )
                .border(
                    1.dp,
                    CalmingGreen.copy(alpha = 0.15f),
                    RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(CalmingGreen.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = null,
                        tint = CalmingGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Find a Specialist",
                        style = MaterialTheme.typography.titleMedium,
                        color = OffWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Connect with professional support",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextDarkSecondary
                    )
                }
                
                Text(
                    text = "Get Started",
                    style = MaterialTheme.typography.labelMedium,
                    color = CalmingGreen,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SECTION 4: DAILY AFFIRMATION
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun DailyAffirmationCard(
    affirmation: String,
    onShare: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Subtle gradient animation
    val infiniteTransition = rememberInfiniteTransition(label = "affirmationTransition")
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradientShift"
    )
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            CalmingLavender.copy(alpha = 0.1f + gradientOffset * 0.05f),
                            CalmingPeach.copy(alpha = 0.08f),
                            CalmingLavender.copy(alpha = 0.06f)
                        ),
                        start = Offset(gradientOffset * 100, 0f),
                        end = Offset(400f + gradientOffset * 100, 400f)
                    )
                )
                .border(
                    1.dp,
                    Brush.linearGradient(
                        colors = listOf(
                            CalmingLavender.copy(alpha = 0.2f),
                            CalmingPeach.copy(alpha = 0.1f)
                        )
                    ),
                    RoundedCornerShape(24.dp)
                )
                .padding(24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = CalmingLavender,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Daily Affirmation",
                            style = MaterialTheme.typography.labelMedium,
                            color = CalmingLavender,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    IconButton(
                        onClick = onShare,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share",
                            tint = TextDarkSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "\"$affirmation\"",
                    style = MaterialTheme.typography.titleLarge,
                    color = OffWhite,
                    fontWeight = FontWeight.Light,
                    fontStyle = FontStyle.Italic,
                    lineHeight = 30.sp
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SECTION 5: SPONSORED / OPPORTUNITIES
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun SponsoredSection(
    title: String,
    description: String,
    ctaText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Sponsored",
            style = MaterialTheme.typography.labelSmall,
            color = TextDarkTertiary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = DeepSurface.copy(alpha = 0.6f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        color = OffWhite,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextDarkSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                TextButton(
                    onClick = onClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MutedTeal
                    )
                ) {
                    Text(ctaText, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SECTION 6: YOUR ROUTINE
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun YourRoutineSection(
    selectedDay: Int,
    onDaySelected: (Int) -> Unit,
    tasks: List<RoutineTask>,
    onTaskClick: (RoutineTask) -> Unit,
    modifier: Modifier = Modifier
) {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    
    Column(modifier = modifier.fillMaxWidth()) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your Routine",
                style = MaterialTheme.typography.titleMedium,
                color = OffWhite,
                fontWeight = FontWeight.SemiBold
            )
            
            Text(
                text = "View All",
                style = MaterialTheme.typography.labelMedium,
                color = MutedTeal,
                modifier = Modifier.clickable { }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Day selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            days.forEachIndexed { index, day ->
                DayChip(
                    day = day,
                    isSelected = selectedDay == index,
                    onClick = { onDaySelected(index) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Tasks
        tasks.forEach { task ->
            RoutineTaskItem(
                task = task,
                onClick = { onTaskClick(task) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun DayChip(
    day: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MutedTeal.copy(alpha = 0.2f) else Color.Transparent,
        animationSpec = tween(200),
        label = "dayBgColor"
    )
    
    val textColor by animateColorAsState(
        targetValue = if (isSelected) MutedTeal else TextDarkSecondary,
        animationSpec = tween(200),
        label = "dayTextColor"
    )
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day,
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun RoutineTaskItem(
    task: RoutineTask,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "taskScale"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) 
                CalmingGreen.copy(alpha = 0.08f) 
            else 
                DeepSurface.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Completion indicator
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        if (task.isCompleted) CalmingGreen.copy(alpha = 0.2f)
                        else Color.Transparent
                    )
                    .border(
                        2.dp,
                        if (task.isCompleted) CalmingGreen else TextDarkSecondary.copy(alpha = 0.3f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = CalmingGreen,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = task.accentColor,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (task.isCompleted) TextDarkSecondary else OffWhite,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = TextDarkTertiary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

data class RoutineTask(
    val id: String,
    val category: String,
    val title: String,
    val isCompleted: Boolean,
    val accentColor: Color
)

// ═══════════════════════════════════════════════════════════════════════════════
// SECTION 7: SELF-DISCOVERY QUEST
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun SelfDiscoveryQuestCard(
    progress: Int,
    total: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "questScale"
    )
    
    // Animated progress
    val animatedProgress by animateFloatAsState(
        targetValue = progress.toFloat() / total.toFloat(),
        animationSpec = tween(800, easing = EaseOutCubic),
        label = "questProgress"
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            CalmingPeach.copy(alpha = 0.12f),
                            CalmingPeach.copy(alpha = 0.06f)
                        )
                    )
                )
                .border(
                    1.dp,
                    CalmingPeach.copy(alpha = 0.2f),
                    RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Progress ring
                Box(
                    modifier = Modifier.size(56.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Background arc
                        drawArc(
                            color = CalmingPeach.copy(alpha = 0.2f),
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                        )
                        // Progress arc
                        drawArc(
                            color = CalmingPeach,
                            startAngle = -90f,
                            sweepAngle = 360f * animatedProgress,
                            useCenter = false,
                            style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = CalmingPeach,
                        modifier = Modifier.size(22.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Self-Discovery Quest",
                        style = MaterialTheme.typography.titleSmall,
                        color = OffWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Take 3 tests to unlock a reward",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextDarkSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$progress/$total completed",
                        style = MaterialTheme.typography.labelSmall,
                        color = CalmingPeach,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                TextButton(onClick = onClick) {
                    Text(
                        "Start",
                        color = CalmingPeach,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SECTION 8: TEST RESULTS OVERVIEW
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun TestResultsSection(
    completedCount: Int,
    totalTests: Int,
    tests: List<TestPreview>,
    onTestClick: (TestPreview) -> Unit,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Test Results",
                    style = MaterialTheme.typography.titleMedium,
                    color = OffWhite,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$completedCount/$totalTests completed",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextDarkSecondary
                )
            }
            
            TextButton(onClick = onViewAll) {
                Text(
                    "View All",
                    color = MutedTeal,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            tests.forEach { test ->
                TestPreviewCard(
                    test = test,
                    onClick = { onTestClick(test) }
                )
            }
        }
    }
}

@Composable
private fun TestPreviewCard(
    test: TestPreview,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "testPreviewScale"
    )
    
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(160.dp)
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
                    Brush.verticalGradient(
                        colors = listOf(
                            test.accentColor.copy(alpha = 0.15f),
                            test.accentColor.copy(alpha = 0.05f)
                        )
                    )
                )
                .border(
                    1.dp,
                    test.accentColor.copy(alpha = 0.2f),
                    RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(test.accentColor.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = test.emoji, fontSize = 20.sp)
                }
                
                Column {
                    Text(
                        text = test.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = OffWhite,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    if (test.isCompleted) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = CalmingGreen,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Done",
                                style = MaterialTheme.typography.labelSmall,
                                color = CalmingGreen
                            )
                        }
                    }
                }
            }
        }
    }
}

data class TestPreview(
    val id: String,
    val title: String,
    val emoji: String,
    val accentColor: Color,
    val isCompleted: Boolean
)

// ═══════════════════════════════════════════════════════════════════════════════
// SECTION 9: UPLIFTING QUIZZES
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun UpliftingQuizzesSection(
    quizzes: List<QuizPreview>,
    onQuizClick: (QuizPreview) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Uplifting Quizzes",
            style = MaterialTheme.typography.titleMedium,
            color = OffWhite,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            quizzes.forEach { quiz ->
                QuizCard(
                    quiz = quiz,
                    onClick = { onQuizClick(quiz) }
                )
            }
        }
    }
}

@Composable
private fun QuizCard(
    quiz: QuizPreview,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "quizScale"
    )
    
    Card(
        modifier = Modifier
            .width(180.dp)
            .height(120.dp)
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
                        colors = listOf(
                            quiz.accentColor.copy(alpha = 0.12f),
                            quiz.accentColor.copy(alpha = 0.06f)
                        )
                    )
                )
                .border(
                    1.dp,
                    quiz.accentColor.copy(alpha = 0.15f),
                    RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = quiz.emoji, fontSize = 28.sp)
                
                Text(
                    text = quiz.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = OffWhite,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

data class QuizPreview(
    val id: String,
    val title: String,
    val emoji: String,
    val accentColor: Color
)

// ═══════════════════════════════════════════════════════════════════════════════
// SECTION TITLE COMPONENT
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun HomeSectionTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = OffWhite,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
    )
}
