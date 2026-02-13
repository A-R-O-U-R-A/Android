package com.example.aroura.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.data.api.CalmAnxietyEntryData
import com.example.aroura.data.api.HomeMoodData
import com.example.aroura.data.api.MoodJournalEntryData
import com.example.aroura.ui.components.ArouraBackground
import com.example.aroura.ui.components.ArouraProfileIcon
import com.example.aroura.ui.theme.*

enum class ReflectOption {
    MoodCheckIn,
    Journal,
    GuidedReflection,
    MoodHistory,
    MoodJournalHistory,  // Track Your Mood detailed entries
    Assessments,
    AnxietyJournal
}

/**
 * Reflect Menu Screen - Premium Redesign
 * 
 * Features:
 * - Staggered entrance animations
 * - Premium card hover effects
 * - Breathing icon animations
 * - Unified spacing
 */
@Composable
fun ReflectMenuScreen(
    onNavigate: (ReflectOption) -> Unit, 
    onProfileClick: () -> Unit,
    profilePictureUrl: String? = null
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val options = listOf(
        ReflectOptionData(
            "How are you feeling?",
            "Quick mood check-in",
            Icons.Default.Face,
            CalmingPeach,
            ReflectOption.MoodCheckIn
        ),
        ReflectOptionData(
            "Self-Discovery Tests",
            "37+ psychological assessments",
            Icons.Default.Star,
            CalmingLavender,
            ReflectOption.Assessments
        ),
        ReflectOptionData(
            "Journal Your Thoughts",
            "Write or speak your feelings",
            Icons.Default.Edit,
            SoftBlue,
            ReflectOption.Journal
        ),
        ReflectOptionData(
            "Guided Self-Reflection",
            "Explore prompts for deeper insight",
            Icons.Default.Info,
            CalmingLavender,
            ReflectOption.GuidedReflection
        ),
        ReflectOptionData(
            "Track Your Mood",
            "View your detailed mood journal",
            Icons.Default.DateRange,
            MutedTeal,
            ReflectOption.MoodJournalHistory
        ),
        ReflectOptionData(
            "Mood Check-in History",
            "View quick mood check-ins",
            Icons.Default.DateRange,
            SoftBlue,
            ReflectOption.MoodHistory
        ),
        ReflectOptionData(
            "Anxiety Journal",
            "View your calm anxiety reflections",
            Icons.Default.Favorite,
            CalmingGreen,
            ReflectOption.AnxietyJournal
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = ArouraSpacing.screenHorizontal.dp),
        contentPadding = PaddingValues(bottom = 120.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                        text = "Reflect",
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
                    text = "Check in with your heart.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDarkSecondary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.height(ArouraSpacing.xxl.dp))
        }

        itemsIndexed(options) { index, option ->
            val delay = 150 + (index * 80)
            
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(400, delayMillis = delay)) + slideInVertically(
                    initialOffsetY = { 30 },
                    animationSpec = tween(400, delayMillis = delay, easing = EaseOutCubic)
                )
            ) {
                PremiumReflectOptionCard(
                    title = option.title,
                    subtitle = option.subtitle,
                    icon = option.icon,
                    iconColor = option.color,
                    onClick = { onNavigate(option.option) }
                )
            }
            
            Spacer(modifier = Modifier.height(ArouraSpacing.md.dp))
        }
    }
}

private data class ReflectOptionData(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val option: ReflectOption
)

@Composable
private fun PremiumReflectOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "cardScale"
    )
    
    // Breathing icon animation
    val infiniteTransition = rememberInfiniteTransition(label = "iconBreath")
    val iconScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconPulse"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .scale(scale)
            .clip(RoundedCornerShape(ArouraSpacing.cardRadius.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        iconColor.copy(alpha = 0.08f),
                        DeepSurface.copy(alpha = 0.5f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        iconColor.copy(alpha = 0.2f),
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(ArouraSpacing.cardRadius.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .padding(horizontal = ArouraSpacing.lg.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .scale(iconScale)
                    .background(iconColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(ArouraSpacing.lg.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = OffWhite,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextDarkSecondary
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = iconColor.copy(alpha = 0.5f)
            )
        }
    }
}

/**
 * Mood Check-In Screen - Premium Redesign with Database Integration
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodCheckInScreen(
    onBack: () -> Unit,
    onViewHistory: () -> Unit = {},
    viewModel: com.example.aroura.ui.viewmodels.ReflectViewModel? = null
) {
    var visible by remember { mutableStateOf(false) }
    var selectedMood by remember { mutableStateOf<String?>(null) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var moodNote by remember { mutableStateOf("") }
    var showSavedConfirmation by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    // Observe ViewModel state if available
    val moodSaved = viewModel?.moodSaved?.collectAsState()?.value ?: false
    
    LaunchedEffect(moodSaved) {
        if (moodSaved) {
            showSavedConfirmation = true
            showSaveDialog = false
            kotlinx.coroutines.delay(2000)
            showSavedConfirmation = false
            viewModel?.resetMoodSelection()
        }
    }

    val moods = listOf(
        Triple("Happy", Icons.Default.ThumbUp, CalmingPeach),
        Triple("Calm", Icons.Default.Star, MutedTeal),
        Triple("Sad", Icons.Default.Clear, SoftBlue),
        Triple("Anxious", Icons.Default.Warning, CalmingLavender),
        Triple("Tired", Icons.Default.Home, TextDarkSecondary),
        Triple("Angry", Icons.Default.Close, Color(0xFFE57373))
    )
    
    // Save Dialog
    if (showSaveDialog && selectedMood != null) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            containerColor = DeepSurface,
            shape = RoundedCornerShape(24.dp),
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = when (selectedMood) {
                            "Happy" -> "😊"
                            "Calm" -> "😌"
                            "Sad" -> "😢"
                            "Anxious" -> "😰"
                            "Tired" -> "😴"
                            "Angry" -> "😠"
                            else -> "🙂"
                        },
                        fontSize = 48.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Feeling $selectedMood",
                        style = MaterialTheme.typography.titleLarge,
                        color = OffWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "Add a note (optional)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextDarkSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(
                                Color.White.copy(alpha = 0.05f),
                                RoundedCornerShape(12.dp)
                            )
                            .border(
                                1.dp,
                                Color.White.copy(alpha = 0.1f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp)
                    ) {
                        BasicTextField(
                            value = moodNote,
                            onValueChange = { moodNote = it },
                            textStyle = TextStyle(
                                color = OffWhite,
                                fontSize = 15.sp
                            ),
                            cursorBrush = SolidColor(MutedTeal),
                            modifier = Modifier.fillMaxSize()
                        )
                        if (moodNote.isEmpty()) {
                            Text(
                                "What's on your mind?",
                                color = TextDarkSecondary.copy(alpha = 0.5f),
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel?.selectMood(selectedMood!!)
                        viewModel?.saveMood(moodNote)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MutedTeal),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save", color = MidnightCharcoal, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel", color = TextDarkSecondary)
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ArouraBackground()
        
        // Saved confirmation overlay
        AnimatedVisibility(
            visible = showSavedConfirmation,
            enter = fadeIn() + scaleIn(initialScale = 0.8f),
            exit = fadeOut() + scaleOut(targetScale = 0.8f),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(MutedTeal.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MutedTeal,
                    modifier = Modifier.size(60.dp)
                )
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = ArouraSpacing.screenHorizontal.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PremiumBackTopBar("Mood Check-In", onBack)
            
            Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
            
            // View History button
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(400))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .clickable { onViewHistory() }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = MutedTeal,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "View Mood History",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OffWhite
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = TextDarkSecondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
            
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500)) + slideInVertically(
                    initialOffsetY = { -20 },
                    animationSpec = tween(500, easing = EaseOutCubic)
                )
            ) {
                Text(
                    text = "How are you feeling?",
                    style = MaterialTheme.typography.headlineMedium,
                    color = OffWhite,
                    fontWeight = FontWeight.Light
                )
            }
            
            Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(ArouraSpacing.md.dp)) {
                moods.chunked(2).forEachIndexed { rowIndex, rowMoods ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(ArouraSpacing.md.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        rowMoods.forEachIndexed { colIndex, (name, icon, color) ->
                            val delay = 200 + (rowIndex * 100) + (colIndex * 50)
                            
                            AnimatedVisibility(
                                visible = visible,
                                enter = fadeIn(tween(400, delayMillis = delay)) + scaleIn(
                                    initialScale = 0.8f,
                                    animationSpec = tween(400, delayMillis = delay, easing = EaseOutCubic)
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                PremiumMoodCard(
                                    name = name,
                                    icon = icon,
                                    color = color,
                                    isSelected = selectedMood == name,
                                    onClick = { selectedMood = name }
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            AnimatedVisibility(
                visible = selectedMood != null,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 50 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { 50 })
            ) {
                PremiumSaveButton(
                    text = "Save Mood",
                    onClick = { showSaveDialog = true }
                )
            }
            
            Spacer(modifier = Modifier.height(ArouraSpacing.xxl.dp))
        }
    }
}

@Composable
private fun PremiumMoodCard(
    name: String,
    icon: ImageVector,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.95f
            isSelected -> 1.02f
            else -> 1f
        },
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "moodScale"
    )
    
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) color.copy(alpha = 0.2f) else DeepSurface.copy(alpha = 0.5f),
        animationSpec = tween(200),
        label = "moodBg"
    )
    
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) color.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.08f),
        animationSpec = tween(200),
        label = "moodBorder"
    )

    Surface(
        modifier = Modifier
            .height(110.dp)
            .scale(scale),
        color = backgroundColor,
        shape = RoundedCornerShape(ArouraSpacing.cardRadius.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                null,
                tint = if (isSelected) color else color.copy(alpha = 0.7f),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(ArouraSpacing.sm.dp))
            Text(
                name,
                color = if (isSelected) OffWhite else TextDarkSecondary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

/**
 * Journal Screen - Premium Redesign
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(onBack: () -> Unit, onNavigateToVoice: () -> Unit) {
    var text by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(modifier = Modifier.fillMaxSize()) {
        ArouraBackground()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = ArouraSpacing.screenHorizontal.dp)
        ) {
            PremiumBackTopBar("Journal", onBack)
            
            Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
            
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(400))
            ) {
                Text(
                    text = "What's on your mind today?",
                    style = MaterialTheme.typography.titleLarge,
                    color = OffWhite.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Light
                )
            }
            
            Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
            
            // Voice Toggle
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(400, delayMillis = 100)) + slideInVertically(
                    initialOffsetY = { 20 },
                    animationSpec = tween(400, delayMillis = 100)
                )
            ) {
                PremiumVoiceToggle(onClick = onNavigateToVoice)
            }
            
            Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
            
            // Text Area
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(400, delayMillis = 200)),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            DeepSurface.copy(alpha = 0.3f),
                            RoundedCornerShape(ArouraSpacing.cardRadius.dp)
                        )
                        .border(
                            1.dp,
                            Color.White.copy(alpha = 0.05f),
                            RoundedCornerShape(ArouraSpacing.cardRadius.dp)
                        )
                        .padding(ArouraSpacing.lg.dp)
                ) {
                    BasicTextField(
                        value = text,
                        onValueChange = { text = it },
                        textStyle = TextStyle(
                            color = OffWhite,
                            fontSize = 17.sp,
                            lineHeight = 28.sp
                        ),
                        cursorBrush = SolidColor(MutedTeal),
                        modifier = Modifier.fillMaxSize()
                    )
                    if (text.isEmpty()) {
                        Text(
                            "Start writing here...",
                            color = TextDarkSecondary.copy(alpha = 0.4f),
                            fontSize = 17.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
            
            AnimatedVisibility(
                visible = visible && text.isNotEmpty(),
                enter = fadeIn() + slideInVertically(initialOffsetY = { 30 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { 30 })
            ) {
                PremiumSaveButton(
                    text = "Save Entry",
                    onClick = { /* Save */ }
                )
            }
            
            Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
        }
    }
}

@Composable
private fun PremiumVoiceToggle(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "voiceScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .scale(scale)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        SoftBlue.copy(alpha = 0.15f),
                        MutedTeal.copy(alpha = 0.1f)
                    )
                ),
                shape = RoundedCornerShape(26.dp)
            )
            .border(
                1.dp,
                SoftBlue.copy(alpha = 0.2f),
                RoundedCornerShape(26.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Star,
                null,
                tint = SoftBlue,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(ArouraSpacing.sm.dp))
            Text(
                "Switch to Voice Journal",
                color = SoftBlue,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Guided Reflection Screen - Premium Redesign
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuidedReflectionScreen(onBack: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    
    // Floating animation for prompt card
    val infiniteTransition = rememberInfiniteTransition(label = "promptFloat")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatY"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        ArouraBackground()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = ArouraSpacing.screenHorizontal.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PremiumBackTopBar("Guided Reflection", onBack)
            
            Spacer(modifier = Modifier.weight(0.3f))
            
            // Prompt Card
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600)) + scaleIn(
                    initialScale = 0.9f,
                    animationSpec = tween(600, easing = EaseOutCubic)
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = offsetY.dp)
                        .aspectRatio(0.85f)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    CalmingLavender.copy(alpha = 0.15f),
                                    DeepSurface.copy(alpha = 0.8f)
                                )
                            ),
                            shape = RoundedCornerShape(ArouraSpacing.xl.dp)
                        )
                        .border(
                            1.dp,
                            Brush.verticalGradient(
                                colors = listOf(
                                    CalmingLavender.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            ),
                            RoundedCornerShape(ArouraSpacing.xl.dp)
                        )
                        .padding(ArouraSpacing.xl.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Decorative element
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    CalmingLavender.copy(alpha = 0.2f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Favorite,
                                null,
                                tint = CalmingLavender,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
                        
                        Text(
                            text = "Daily Prompt",
                            style = MaterialTheme.typography.labelMedium,
                            color = CalmingLavender,
                            letterSpacing = 2.sp
                        )
                        
                        Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
                        
                        Text(
                            text = "What is one small thing you can do for yourself today?",
                            style = MaterialTheme.typography.headlineSmall,
                            color = OffWhite,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Light,
                            lineHeight = 32.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(0.3f))
            
            // New Prompt Button
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500, delayMillis = 400))
            ) {
                PremiumOutlinedButton(
                    text = "New Prompt",
                    onClick = { /* Next */ }
                )
            }
            
            Spacer(modifier = Modifier.height(ArouraSpacing.xxl.dp))
        }
    }
}

@Composable
private fun PremiumOutlinedButton(text: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "btnScale"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .border(
                1.dp,
                CalmingLavender.copy(alpha = 0.5f),
                RoundedCornerShape(24.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .padding(horizontal = 32.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            color = CalmingLavender,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Emotion Tracker Screen - Premium Redesign
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmotionTrackerScreen(onBack: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    
    // Pulsing animation
    val infiniteTransition = rememberInfiniteTransition(label = "trackerPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        ArouraBackground()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = ArouraSpacing.screenHorizontal.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PremiumBackTopBar("Emotion Tracker", onBack)
            
            Spacer(modifier = Modifier.weight(1f))
            
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600)) + scaleIn(
                    initialScale = 0.8f,
                    animationSpec = tween(600, easing = EaseOutCubic)
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Animated Icon
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .scale(pulseScale)
                            .background(
                                MutedTeal.copy(alpha = 0.15f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(
                                    MutedTeal.copy(alpha = 0.2f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = MutedTeal
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
                    
                    Text(
                        text = "Your Emotional Journey",
                        style = MaterialTheme.typography.titleLarge,
                        color = OffWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Spacer(modifier = Modifier.height(ArouraSpacing.sm.dp))
                    
                    Text(
                        text = "Tracking coming soon.",
                        color = TextDarkSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
                    
                    Text(
                        text = "We're building something beautiful\nto help you understand your patterns.",
                        color = TextDarkSecondary.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

// Shared Components

@Composable
private fun PremiumBackTopBar(title: String, onBack: () -> Unit) {
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
            title,
            style = MaterialTheme.typography.titleMedium,
            color = OffWhite,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun PremiumSaveButton(text: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "saveScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(MutedTeal, MutedTeal.copy(alpha = 0.85f))
                ),
                shape = RoundedCornerShape(28.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            style = MaterialTheme.typography.titleMedium,
            color = MidnightCharcoal,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Mood History Screen - Shows all saved mood check-ins
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodHistoryScreen(
    onBack: () -> Unit,
    moodHistory: List<HomeMoodData> = emptyList(),
    viewModel: com.example.aroura.ui.viewmodels.ReflectViewModel? = null
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { 
        visible = true
        // Refresh mood history when screen opens
        viewModel?.fetchAllData()
    }
    
    // Get mood history from viewModel if available
    val moods = viewModel?.moodHistory?.collectAsState()?.value ?: moodHistory
    
    Box(modifier = Modifier.fillMaxSize()) {
        ArouraBackground()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            PremiumBackTopBar(
                title = "Mood History",
                onBack = onBack
            )
            
            if (moods.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = ArouraSpacing.screenHorizontal.dp),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(500)) + scaleIn(initialScale = 0.9f)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .background(CalmingPeach.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("📊", fontSize = 40.sp)
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Text(
                                text = "No mood check-ins yet",
                                style = MaterialTheme.typography.titleLarge,
                                color = OffWhite,
                                fontWeight = FontWeight.Medium
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "Start tracking how you feel to see\nyour emotional patterns over time.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextDarkSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                // Mood list
                LazyColumn(
                    contentPadding = PaddingValues(
                        horizontal = ArouraSpacing.screenHorizontal.dp,
                        vertical = ArouraSpacing.md.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Summary card
                    item {
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(tween(400)) + slideInVertically(initialOffsetY = { -20 })
                        ) {
                            MoodSummaryCard(
                                totalCheckIns = moods.size,
                                mostCommonMood = getMostCommonMoodFromList(moods)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Recent Check-ins",
                            style = MaterialTheme.typography.titleMedium,
                            color = OffWhite,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    items(moods) { mood ->
                        androidx.compose.animation.AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(tween(300, delayMillis = 100)) + slideInVertically(
                                initialOffsetY = { 20 },
                                animationSpec = tween(300, delayMillis = 100)
                            )
                        ) {
                            MoodHistoryCard(mood = mood)
                        }
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun MoodSummaryCard(
    totalCheckIns: Int,
    mostCommonMood: String?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        MutedTeal.copy(alpha = 0.15f),
                        CalmingLavender.copy(alpha = 0.1f)
                    )
                )
            )
            .border(
                1.dp,
                MutedTeal.copy(alpha = 0.2f),
                RoundedCornerShape(16.dp)
            )
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Total check-ins
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = totalCheckIns.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MutedTeal,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Check-ins",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextDarkSecondary
                )
            }
            
            // Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(50.dp)
                    .background(Color.White.copy(alpha = 0.1f))
            )
            
            // Most common mood
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = mostCommonMood?.let { getMoodEmojiFromName(it) } ?: "—",
                    fontSize = 28.sp
                )
                Text(
                    text = mostCommonMood ?: "N/A",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextDarkSecondary
                )
            }
        }
    }
}

@Composable
private fun MoodHistoryCard(mood: HomeMoodData) {
    val moodColor = getMoodColorFromName(mood.moodLabel)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DeepSurface.copy(alpha = 0.5f))
            .border(
                1.dp,
                Color.White.copy(alpha = 0.05f),
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mood emoji
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(moodColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getMoodEmojiFromName(mood.moodLabel),
                    fontSize = 24.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = mood.moodLabel,
                    style = MaterialTheme.typography.titleMedium,
                    color = OffWhite,
                    fontWeight = FontWeight.Medium
                )
                
                mood.note?.let { note ->
                    if (note.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = note,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextDarkSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            
            // Date
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = formatMoodDate(mood.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextDarkSecondary
                )
            }
        }
    }
}

// Helper functions
private fun getMoodEmojiFromName(mood: String): String {
    return when (mood.lowercase()) {
        "happy" -> "😊"
        "calm" -> "😌"
        "sad" -> "😢"
        "anxious" -> "😰"
        "tired" -> "😴"
        "angry" -> "😠"
        else -> "🙂"
    }
}

private fun getMoodColorFromName(mood: String): Color {
    return when (mood.lowercase()) {
        "happy" -> CalmingPeach
        "calm" -> MutedTeal
        "sad" -> SoftBlue
        "anxious" -> CalmingLavender
        "tired" -> TextDarkSecondary
        "angry" -> Color(0xFFE57373)
        else -> MutedTeal
    }
}

private fun getMostCommonMoodFromList(moods: List<HomeMoodData>): String? {
    if (moods.isEmpty()) return null
    return moods.groupingBy { it.moodLabel }
        .eachCount()
        .maxByOrNull { it.value }
        ?.key
}

private fun formatMoodDate(dateString: String): String {
    return try {
        val parts = dateString.split("T")
        if (parts.isNotEmpty()) {
            val dateParts = parts[0].split("-")
            if (dateParts.size == 3) {
                val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                val month = dateParts[1].toIntOrNull()?.let { months.getOrNull(it - 1) } ?: dateParts[1]
                val day = dateParts[2].toIntOrNull() ?: dateParts[2]
                "$month $day"
            } else dateString
        } else dateString
    } catch (e: Exception) {
        dateString
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// MOOD JOURNAL HISTORY SCREEN (Track Your Mood - detailed entries)
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Mood Journal History Screen - Shows all saved mood journal entries from Track Your Mood
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MoodJournalHistoryScreen(
    onBack: () -> Unit,
    viewModel: com.example.aroura.ui.viewmodels.ReflectViewModel? = null
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { 
        visible = true
        viewModel?.fetchAllData()
    }
    
    // Get mood journal history from viewModel
    val entries = viewModel?.moodJournalHistory?.collectAsState()?.value ?: emptyList()
    
    Box(modifier = Modifier.fillMaxSize()) {
        ArouraBackground()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            PremiumBackTopBar(
                title = "Mood Journal",
                onBack = onBack
            )
            
            if (entries.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = ArouraSpacing.screenHorizontal.dp),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(500)) + scaleIn(initialScale = 0.9f)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .background(MutedTeal.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("📝", fontSize = 40.sp)
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Text(
                                text = "No mood journal entries yet",
                                style = MaterialTheme.typography.titleLarge,
                                color = OffWhite,
                                fontWeight = FontWeight.Medium
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "Complete \"Track Your Mood\" in your\ndaily routine to see entries here.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextDarkSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                // Journal entries list
                LazyColumn(
                    contentPadding = PaddingValues(
                        horizontal = ArouraSpacing.screenHorizontal.dp,
                        vertical = ArouraSpacing.md.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Summary card
                    item {
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(tween(400)) + slideInVertically(initialOffsetY = { -20 })
                        ) {
                            MoodJournalSummaryCard(
                                totalEntries = entries.size,
                                averageMood = entries.map { it.moodLevel }.average().toFloat()
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Recent Entries",
                            style = MaterialTheme.typography.titleMedium,
                            color = OffWhite,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    items(entries) { entry ->
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(tween(300, delayMillis = 100)) + slideInVertically(
                                initialOffsetY = { 20 },
                                animationSpec = tween(300, delayMillis = 100)
                            )
                        ) {
                            MoodJournalEntryCard(entry = entry)
                        }
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun MoodJournalSummaryCard(
    totalEntries: Int,
    averageMood: Float
) {
    val moodEmoji = when {
        averageMood >= 0.8f -> "😄"
        averageMood >= 0.6f -> "🙂"
        averageMood >= 0.4f -> "😐"
        averageMood >= 0.2f -> "😕"
        else -> "😔"
    }
    
    val moodLabel = when {
        averageMood >= 0.8f -> "Happy"
        averageMood >= 0.6f -> "Good"
        averageMood >= 0.4f -> "Okay"
        averageMood >= 0.2f -> "Low"
        else -> "Struggling"
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        MutedTeal.copy(alpha = 0.15f),
                        CalmingLavender.copy(alpha = 0.1f)
                    )
                )
            )
            .border(
                1.dp,
                MutedTeal.copy(alpha = 0.2f),
                RoundedCornerShape(16.dp)
            )
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Total entries
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = totalEntries.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MutedTeal,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Entries",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextDarkSecondary
                )
            }
            
            // Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(50.dp)
                    .background(Color.White.copy(alpha = 0.1f))
            )
            
            // Average mood
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = moodEmoji,
                    fontSize = 28.sp
                )
                Text(
                    text = moodLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextDarkSecondary
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MoodJournalEntryCard(entry: MoodJournalEntryData) {
    val moodEmoji = when {
        entry.moodLevel >= 0.8f -> "😄"
        entry.moodLevel >= 0.6f -> "🙂"
        entry.moodLevel >= 0.4f -> "😐"
        entry.moodLevel >= 0.2f -> "😕"
        else -> "😔"
    }
    
    val moodColor = when {
        entry.moodLevel >= 0.6f -> CalmingGreen
        entry.moodLevel >= 0.4f -> MutedTeal
        else -> SoftBlue
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DeepSurface.copy(alpha = 0.5f))
            .border(
                1.dp,
                moodColor.copy(alpha = 0.2f),
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            // Header row with emoji and date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(moodColor.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(moodEmoji, fontSize = 20.sp)
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = getMoodLabelFromLevel(entry.moodLevel),
                            style = MaterialTheme.typography.titleSmall,
                            color = OffWhite,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = formatMoodDate(entry.createdAt),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextDarkSecondary
                        )
                    }
                }
            }
            
            // Note (if present)
            if (entry.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = entry.note,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDarkSecondary,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Feelings tags
            if (entry.feelings.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    entry.feelings.take(5).forEach { feeling ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (feeling.isPositive) CalmingGreen.copy(alpha = 0.15f)
                                    else SoftBlue.copy(alpha = 0.15f)
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = feeling.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (feeling.isPositive) CalmingGreen else SoftBlue
                            )
                        }
                    }
                    if (entry.feelings.size > 5) {
                        Text(
                            text = "+${entry.feelings.size - 5}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextDarkTertiary
                        )
                    }
                }
            }
            
            // Activities
            if (entry.activities.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    entry.activities.take(6).forEach { activity ->
                        Text(activity.emoji, fontSize = 16.sp)
                    }
                    if (entry.activities.size > 6) {
                        Text(
                            text = "+${entry.activities.size - 6}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextDarkTertiary
                        )
                    }
                }
            }
        }
    }
}

private fun getMoodLabelFromLevel(level: Float): String {
    return when {
        level >= 0.8f -> "Feeling Great"
        level >= 0.6f -> "Doing Good"
        level >= 0.4f -> "Feeling Okay"
        level >= 0.2f -> "Feeling Low"
        else -> "Struggling"
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// ANXIETY HISTORY SCREEN
// ═══════════════════════════════════════════════════════════════════════════════

/**
 * Anxiety History Screen - Shows all saved calm anxiety entries
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnxietyHistoryScreen(
    onBack: () -> Unit,
    anxietyHistory: List<CalmAnxietyEntryData> = emptyList(),
    viewModel: com.example.aroura.ui.viewmodels.ReflectViewModel? = null
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { 
        visible = true
        viewModel?.fetchAnxietyHistory()
    }
    
    // Get anxiety history from viewModel if available
    val entries = viewModel?.anxietyHistory?.collectAsState()?.value ?: anxietyHistory
    
    Box(modifier = Modifier.fillMaxSize()) {
        ArouraBackground()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            PremiumBackTopBar(
                title = "Anxiety Journal",
                onBack = onBack
            )
            
            if (entries.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = ArouraSpacing.screenHorizontal.dp),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(500)) + scaleIn(initialScale = 0.9f)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .background(CalmingLavender.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🧘", fontSize = 40.sp)
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Text(
                                text = "No anxiety reflections yet",
                                style = MaterialTheme.typography.titleLarge,
                                color = OffWhite,
                                fontWeight = FontWeight.Medium
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "Complete the Calm Your Anxiety\nflow to see your reflections here.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextDarkSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                // Anxiety entries list
                LazyColumn(
                    contentPadding = PaddingValues(
                        horizontal = ArouraSpacing.screenHorizontal.dp,
                        vertical = ArouraSpacing.md.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Summary card
                    item {
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(tween(400)) + slideInVertically(initialOffsetY = { -20 })
                        ) {
                            AnxietySummaryCard(
                                totalSessions = entries.size,
                                completedSessions = entries.count { it.completedFully }
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Recent Reflections",
                            style = MaterialTheme.typography.titleMedium,
                            color = OffWhite,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    items(entries) { entry ->
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(tween(300, delayMillis = 100)) + slideInVertically(
                                initialOffsetY = { 20 },
                                animationSpec = tween(300, delayMillis = 100)
                            )
                        ) {
                            AnxietyHistoryCard(entry = entry)
                        }
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AnxietySummaryCard(
    totalSessions: Int,
    completedSessions: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        CalmingLavender.copy(alpha = 0.15f),
                        SoftBlue.copy(alpha = 0.1f)
                    )
                )
            )
            .border(
                1.dp,
                CalmingLavender.copy(alpha = 0.2f),
                RoundedCornerShape(16.dp)
            )
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = totalSessions.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = CalmingLavender,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Sessions",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextDarkSecondary
                )
            }
            
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(50.dp)
                    .background(CalmingLavender.copy(alpha = 0.3f))
            )
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = completedSessions.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MutedTeal,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Completed",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextDarkSecondary
                )
            }
        }
    }
}

@Composable
private fun AnxietyHistoryCard(
    entry: CalmAnxietyEntryData
) {
    val primaryTrigger = entry.primaryTrigger.ifEmpty {
        entry.reflections.firstOrNull()?.answer?.take(50) ?: "Reflection"
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DeepSurface.copy(alpha = 0.5f))
            .border(
                1.dp,
                CalmingLavender.copy(alpha = 0.15f),
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(CalmingLavender.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🧘", fontSize = 18.sp)
                    }
                    
                    Column {
                        Text(
                            text = formatMoodDate(entry.createdAt),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextDarkSecondary
                        )
                        Text(
                            text = if (entry.completedFully) "Completed" else "Partial",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (entry.completedFully) MutedTeal else CalmingPeach
                        )
                    }
                }
                
                if (entry.durationSeconds > 0) {
                    Text(
                        text = "${entry.durationSeconds / 60}m ${entry.durationSeconds % 60}s",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextDarkSecondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "\"$primaryTrigger\"",
                style = MaterialTheme.typography.bodyMedium,
                color = OffWhite,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Light
            )
            
            if (entry.reflections.size > 1) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${entry.reflections.size} reflections recorded",
                    style = MaterialTheme.typography.labelSmall,
                    color = CalmingLavender.copy(alpha = 0.7f)
                )
            }
        }
    }
}
