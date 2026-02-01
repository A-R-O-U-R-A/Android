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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.ui.components.ArouraBackground
import com.example.aroura.ui.components.ArouraProfileIcon
import com.example.aroura.ui.theme.*

enum class ReflectOption {
    MoodCheckIn,
    Journal,
    GuidedReflection,
    EmotionTracker,
    Assessments
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
fun ReflectMenuScreen(onNavigate: (ReflectOption) -> Unit, onProfileClick: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val options = listOf(
        ReflectOptionData(
            "How are you feeling?",
            "Check in and reflect on your mood.",
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
            "Track Your Emotions",
            "Notice patterns and gain insight",
            Icons.Default.DateRange,
            MutedTeal,
            ReflectOption.EmotionTracker
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
                ArouraProfileIcon(onClick = onProfileClick)
                
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

// Legacy compatibility
@Composable
fun ReflectOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) = PremiumReflectOptionCard(title, subtitle, icon, iconColor, onClick)

/**
 * Mood Check-In Screen - Premium Redesign
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodCheckInScreen(onBack: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    var selectedMood by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) { visible = true }

    val moods = listOf(
        Triple("Happy", Icons.Default.ThumbUp, CalmingPeach),
        Triple("Calm", Icons.Default.Star, MutedTeal),
        Triple("Sad", Icons.Default.Clear, SoftBlue),
        Triple("Anxious", Icons.Default.Warning, CalmingLavender),
        Triple("Tired", Icons.Default.Home, TextDarkSecondary),
        Triple("Angry", Icons.Default.Close, Color(0xFFE57373))
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
            PremiumBackTopBar("Mood Check-In", onBack)
            
            Spacer(modifier = Modifier.height(ArouraSpacing.xxl.dp))
            
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
            
            Spacer(modifier = Modifier.height(ArouraSpacing.xxl.dp))
            
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
                    onClick = { /* Save */ }
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

// Legacy compatibility
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleBackTopBar(title: String, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(title, style = MaterialTheme.typography.titleMedium, color = OffWhite) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = OffWhite)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}