package com.example.aroura.ui.components.reflect

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.data.ReflectTest
import com.example.aroura.data.ReflectSection
import com.example.aroura.ui.theme.*

/**
 * Reflect Components - Premium UI Library
 * 
 * Reusable composables for the complete Reflect experience:
 * - Test Cards
 * - Section Headers  
 * - Get Started Screens
 * - Question Screens
 * - Navigation Elements
 */

// ═══════════════════════════════════════════════════════════════════════════════
// SECTION HEADER
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun ReflectSectionHeader(
    section: ReflectSection,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = ArouraSpacing.md.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = section.emoji,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.width(ArouraSpacing.sm.dp))
        Column {
            Text(
                text = section.title,
                style = MaterialTheme.typography.titleMedium,
                color = OffWhite,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = section.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextDarkSecondary
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// TEST CARD - The core card component for each test
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun ReflectTestCard(
    test: ReflectTest,
    isCompleted: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "cardScale"
    )
    
    val accentColor = Color(test.accentColorHex)
    
    // Subtle breathing animation for the icon
    val infiniteTransition = rememberInfiniteTransition(label = "iconBreath")
    val iconScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconPulse"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(ArouraSpacing.cardRadius.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.08f),
                        DeepSurface.copy(alpha = 0.6f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.25f),
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(ArouraSpacing.cardRadius.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .padding(ArouraSpacing.lg.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .scale(iconScale)
                    .background(accentColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getTestIcon(test.iconName),
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(ArouraSpacing.md.dp))

            // Text Content
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = test.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = OffWhite,
                        fontWeight = FontWeight.Medium
                    )
                    if (isCompleted) {
                        Spacer(modifier = Modifier.width(ArouraSpacing.sm.dp))
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(MutedTeal.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Check,
                                null,
                                tint = MutedTeal,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = test.shortDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextDarkSecondary,
                    lineHeight = 18.sp
                )
                
                Spacer(modifier = Modifier.height(ArouraSpacing.xs.dp))
                
                Text(
                    text = "${test.estimatedMinutes.first}–${test.estimatedMinutes.last} min",
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor.copy(alpha = 0.8f)
                )
            }

            // Arrow
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = accentColor.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// GET STARTED SCREEN COMPONENTS
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun TestGetStartedScreen(
    test: ReflectTest,
    onBack: () -> Unit,
    onStart: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    
    val accentColor = Color(test.accentColorHex)
    
    // Floating animation
    val infiniteTransition = rememberInfiniteTransition(label = "floatAnim")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatY"
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(horizontal = ArouraSpacing.screenHorizontal.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        ReflectTopBar(title = "", onBack = onBack)
        
        Spacer(modifier = Modifier.weight(0.15f))
        
        // Main Card
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
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                accentColor.copy(alpha = 0.12f),
                                DeepSurface.copy(alpha = 0.8f)
                            )
                        ),
                        shape = RoundedCornerShape(ArouraSpacing.xl.dp)
                    )
                    .border(
                        1.dp,
                        Brush.verticalGradient(
                            colors = listOf(
                                accentColor.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        ),
                        RoundedCornerShape(ArouraSpacing.xl.dp)
                    )
                    .padding(ArouraSpacing.xl.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Icon
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(accentColor.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getTestIcon(test.iconName),
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
                    
                    // Title
                    Text(
                        text = test.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = OffWhite,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(ArouraSpacing.md.dp))
                    
                    // Description
                    Text(
                        text = test.longDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextDarkSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                    
                    Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
                    
                    // Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TestInfoChip(
                            icon = Icons.AutoMirrored.Filled.List,
                            text = "${test.questionCount} questions",
                            color = accentColor
                        )
                        TestInfoChip(
                            icon = Icons.Default.DateRange,
                            text = "${test.estimatedMinutes.first}–${test.estimatedMinutes.last} min",
                            color = accentColor
                        )
                    }
                    
                    // Safety Note if present
                    test.safetyNote?.let { note ->
                        Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    CalmingLavender.copy(alpha = 0.1f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(ArouraSpacing.md.dp)
                        ) {
                            Row {
                                Icon(
                                    Icons.Default.Info,
                                    null,
                                    tint = CalmingLavender,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(ArouraSpacing.sm.dp))
                                Text(
                                    text = note,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CalmingLavender.copy(alpha = 0.9f),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
        
        // Reassurance message
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(500, delayMillis = 300))
        ) {
            Text(
                text = "There are no right or wrong answers.\nJust honest reflection.",
                style = MaterialTheme.typography.bodySmall,
                color = TextDarkSecondary.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
        
        Spacer(modifier = Modifier.weight(0.25f))
        
        // Start Button
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(500, delayMillis = 400)) + slideInVertically(
                initialOffsetY = { 40 },
                animationSpec = tween(500, delayMillis = 400, easing = EaseOutCubic)
            )
        ) {
            PremiumStartButton(
                text = "Start Test",
                accentColor = accentColor,
                onClick = onStart
            )
        }
        
        Spacer(modifier = Modifier.height(ArouraSpacing.xxl.dp))
    }
}

@Composable
private fun TestInfoChip(
    icon: ImageVector,
    text: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                color.copy(alpha = 0.1f),
                RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Icon(
            icon,
            null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text,
            style = MaterialTheme.typography.labelMedium,
            color = color
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// QUESTION SCREEN COMPONENTS
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun TestQuestionScreen(
    questionNumber: Int,
    totalQuestions: Int,
    questionText: String,
    accentColor: Color,
    onBack: () -> Unit,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(questionNumber) { 
        visible = false
        kotlinx.coroutines.delay(50)
        visible = true 
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(horizontal = ArouraSpacing.screenHorizontal.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress Header
        QuestionProgressHeader(
            current = questionNumber,
            total = totalQuestions,
            accentColor = accentColor,
            onBack = onBack
        )
        
        Spacer(modifier = Modifier.height(ArouraSpacing.xxl.dp))
        
        // Question Text
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(400)) + slideInVertically(
                initialOffsetY = { -20 },
                animationSpec = tween(400, easing = EaseOutCubic)
            )
        ) {
            Text(
                text = questionText,
                style = MaterialTheme.typography.headlineSmall,
                color = OffWhite,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Light,
                lineHeight = 34.sp,
                modifier = Modifier.padding(horizontal = ArouraSpacing.md.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(ArouraSpacing.xxl.dp))
        
        // Answer Options
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(400, delayMillis = 150)) + slideInVertically(
                initialOffsetY = { 30 },
                animationSpec = tween(400, delayMillis = 150, easing = EaseOutCubic)
            ),
            modifier = Modifier.weight(1f)
        ) {
            content()
        }
        
        Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
    }
}

@Composable
private fun QuestionProgressHeader(
    current: Int,
    total: Int,
    accentColor: Color,
    onBack: () -> Unit
) {
    val progress = current.toFloat() / total.toFloat()
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(500, easing = EaseOutCubic),
        label = "progress"
    )
    
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = ArouraSpacing.md.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = OffWhite)
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                text = "$current of $total",
                style = MaterialTheme.typography.labelLarge,
                color = TextDarkSecondary
            )
        }
        
        // Progress Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(DeepSurface)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                accentColor.copy(alpha = 0.6f),
                                accentColor
                            )
                        ),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// ANSWER INPUT COMPONENTS
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun YesNoSometimesInput(
    selectedOption: String?,
    accentColor: Color,
    onSelect: (String) -> Unit
) {
    val options = listOf("Yes", "Sometimes", "No")
    
    Column(
        verticalArrangement = Arrangement.spacedBy(ArouraSpacing.md.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        options.forEach { option ->
            AnswerOptionButton(
                text = option,
                isSelected = selectedOption == option,
                accentColor = accentColor,
                onClick = { onSelect(option) }
            )
        }
    }
}

@Composable
fun LikertScaleInput(
    selectedValue: Int?,
    lowLabel: String = "Not at all",
    highLabel: String = "Very much",
    accentColor: Color,
    onSelect: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ArouraSpacing.md.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(lowLabel, style = MaterialTheme.typography.labelSmall, color = TextDarkSecondary)
            Text(highLabel, style = MaterialTheme.typography.labelSmall, color = TextDarkSecondary)
        }
        
        Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
        
        // Scale Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            (1..5).forEach { value ->
                LikertButton(
                    value = value,
                    isSelected = selectedValue == value,
                    accentColor = accentColor,
                    onClick = { onSelect(value) }
                )
            }
        }
    }
}

@Composable
private fun LikertButton(
    value: Int,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.9f
            isSelected -> 1.1f
            else -> 1f
        },
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "likertScale"
    )
    
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) accentColor else DeepSurface.copy(alpha = 0.6f),
        animationSpec = tween(200),
        label = "likertBg"
    )
    
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) accentColor else Color.White.copy(alpha = 0.1f),
        animationSpec = tween(200),
        label = "likertBorder"
    )

    Box(
        modifier = Modifier
            .size(56.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(1.5.dp, borderColor, CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$value",
            style = MaterialTheme.typography.titleMedium,
            color = if (isSelected) MidnightCharcoal else OffWhite,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun AnswerOptionButton(
    text: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "optionScale"
    )
    
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) accentColor.copy(alpha = 0.2f) else DeepSurface.copy(alpha = 0.5f),
        animationSpec = tween(200),
        label = "optionBg"
    )
    
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) accentColor.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.08f),
        animationSpec = tween(200),
        label = "optionBorder"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = if (isSelected) OffWhite else TextDarkSecondary,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SHARED UI COMPONENTS
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun ReflectTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = ArouraSpacing.md.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = OffWhite)
        }
        if (title.isNotEmpty()) {
            Spacer(modifier = Modifier.width(ArouraSpacing.sm.dp))
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                color = OffWhite,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun PremiumStartButton(
    text: String,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "startScale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .scale(scale)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(accentColor, accentColor.copy(alpha = 0.85f))
                ),
                shape = RoundedCornerShape(29.dp)
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

@Composable
fun TransitionScreen(
    message: String = "Analyzing your responses…",
    accentColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "transition")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(scale)
                    .background(accentColor.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(accentColor.copy(alpha = 0.3f), CircleShape)
                )
            }
            
            Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = OffWhite.copy(alpha = 0.8f),
                fontWeight = FontWeight.Light
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// ICON MAPPING HELPER
// ═══════════════════════════════════════════════════════════════════════════════

fun getTestIcon(iconName: String): ImageVector {
    return when (iconName) {
        "person" -> Icons.Default.Person
        "psychology" -> Icons.Default.Face
        "theater_comedy" -> Icons.Default.Star
        "balance" -> Icons.Default.Refresh
        "auto_awesome" -> Icons.Default.Star
        "groups" -> Icons.Default.AccountCircle
        "favorite" -> Icons.Default.Favorite
        "whatshot" -> Icons.Default.Warning
        "trending_up" -> Icons.Default.KeyboardArrowUp
        "waves" -> Icons.Default.Star
        "battery_alert" -> Icons.Default.Warning
        "cloud" -> Icons.Default.Star
        "healing" -> Icons.Default.Favorite
        "emoji_emotions" -> Icons.Default.Face
        "person_outline" -> Icons.Default.Person
        "child_care" -> Icons.Default.Face
        "favorite_border" -> Icons.Default.FavoriteBorder
        "link" -> Icons.Default.Share
        "history" -> Icons.Default.DateRange
        "report_problem" -> Icons.Default.Warning
        "visibility_off" -> Icons.Default.Lock
        "group" -> Icons.Default.AccountCircle
        "masks" -> Icons.Default.Face
        "volunteer_activism" -> Icons.Default.Favorite
        "accessibility" -> Icons.Default.Person
        "center_focus_strong" -> Icons.Default.Search
        "psychology_alt" -> Icons.Default.Face
        "person_pin" -> Icons.Default.LocationOn
        "category" -> Icons.Default.Menu
        "heart_broken" -> Icons.Default.Favorite
        "self_improvement" -> Icons.Default.Star
        "work" -> Icons.Default.Build
        "schedule" -> Icons.Default.DateRange
        "list" -> Icons.AutoMirrored.Filled.List
        "info" -> Icons.Default.Info
        else -> Icons.Default.Star
    }
}
