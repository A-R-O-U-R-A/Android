@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.example.aroura.ui.screens.calm

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Calm Anxiety Flow - Premium Guided Experience
 * 
 * A gentle, supportive, emotionally intelligent anxiety relief flow.
 * Designed to feel like a calm conversation, not an interrogation.
 * 
 * Flow Structure:
 * 1. Entry/Context Screen
 * 2. Guided Reflection Pages (one question per screen)
 * 3. Mid-Flow Grounding Pages (breathing reminders)
 * 4. Gentle Transition
 * 5. Calm Summary Screen
 * 6. Gentle Next Steps
 */

// ═══════════════════════════════════════════════════════════════════════════════
// DATA MODELS
// ═══════════════════════════════════════════════════════════════════════════════

sealed class CalmFlowStep {
    data object Entry : CalmFlowStep()
    data class Reflection(val questionIndex: Int) : CalmFlowStep()
    data class Grounding(val type: GroundingType) : CalmFlowStep()
    data object Transition : CalmFlowStep()
    data object Summary : CalmFlowStep()
    data object NextSteps : CalmFlowStep()
}

enum class GroundingType {
    BREATHE,
    BODY_SCAN,
    PAUSE
}

data class ReflectionQuestion(
    val id: Int,
    val prompt: String,
    val subtitle: String? = null,
    val suggestedChips: List<String> = emptyList(),
    val emojiHints: List<String> = emptyList()
)

// ═══════════════════════════════════════════════════════════════════════════════
// MAIN FLOW SCREEN
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun CalmAnxietyFlowScreen(
    onClose: () -> Unit,
    onNavigateToBreathing: () -> Unit = {},
    onNavigateToChat: () -> Unit = {}
) {
    // Flow state
    var currentStep by remember { mutableStateOf<CalmFlowStep>(CalmFlowStep.Entry) }
    var answers by remember { mutableStateOf(mutableMapOf<Int, String>()) }
    
    // Reflection questions - gentle, non-judgmental
    val questions = remember {
        listOf(
            ReflectionQuestion(
                id = 0,
                prompt = "What's weighing on your mind right now?",
                subtitle = "There's no right or wrong answer.",
                suggestedChips = listOf("work", "relationships", "health", "future", "everything"),
                emojiHints = listOf("😔", "😰", "😓")
            ),
            ReflectionQuestion(
                id = 1,
                prompt = "How does your body feel in this moment?",
                subtitle = "Notice any sensations without trying to change them.",
                suggestedChips = listOf("tight chest", "racing heart", "tense shoulders", "heavy", "restless"),
                emojiHints = listOf("💓", "😮‍💨", "🫁")
            ),
            ReflectionQuestion(
                id = 2,
                prompt = "What are you afraid might happen?",
                subtitle = "Sometimes naming our fears takes away their power.",
                suggestedChips = listOf("rejection", "failure", "being alone", "losing control", "uncertainty"),
                emojiHints = listOf("😨", "🌀", "❓")
            ),
            ReflectionQuestion(
                id = 3,
                prompt = "What do you wish someone would say to you right now?",
                subtitle = "Imagine a kind friend sitting beside you.",
                suggestedChips = listOf("it's okay", "you're not alone", "this will pass", "I understand"),
                emojiHints = listOf("🤗", "💙", "🕊️")
            ),
            ReflectionQuestion(
                id = 4,
                prompt = "Is there one small thing that might help you feel a little lighter?",
                subtitle = "Even the tiniest step counts.",
                suggestedChips = listOf("rest", "talk to someone", "go outside", "drink water", "just breathe"),
                emojiHints = listOf("🌿", "☀️", "💧")
            )
        )
    }
    
    // Flow sequence with grounding pages
    val flowSequence = remember {
        listOf(
            CalmFlowStep.Entry,
            CalmFlowStep.Reflection(0),
            CalmFlowStep.Reflection(1),
            CalmFlowStep.Grounding(GroundingType.BREATHE),
            CalmFlowStep.Reflection(2),
            CalmFlowStep.Reflection(3),
            CalmFlowStep.Grounding(GroundingType.PAUSE),
            CalmFlowStep.Reflection(4),
            CalmFlowStep.Transition,
            CalmFlowStep.Summary,
            CalmFlowStep.NextSteps
        )
    }
    
    val currentIndex = flowSequence.indexOf(currentStep).coerceAtLeast(0)
    val totalSteps = flowSequence.size
    
    // Navigation functions
    val goNext: () -> Unit = {
        val nextIndex = (currentIndex + 1).coerceAtMost(flowSequence.lastIndex)
        currentStep = flowSequence[nextIndex]
    }
    
    val goBack: () -> Unit = {
        if (currentIndex > 0) {
            currentStep = flowSequence[currentIndex - 1]
        } else {
            onClose()
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Calm background
        CalmAnxietyBackground()
        
        // Content with gentle transitions
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                fadeIn(tween(500, easing = EaseOutCubic)) togetherWith 
                fadeOut(tween(400, easing = EaseInCubic))
            },
            label = "flowContent"
        ) { step ->
            when (step) {
                is CalmFlowStep.Entry -> {
                    EntryScreen(
                        onStart = goNext,
                        onClose = onClose
                    )
                }
                
                is CalmFlowStep.Reflection -> {
                    val question = questions.getOrNull(step.questionIndex)
                    if (question != null) {
                        ReflectionScreen(
                            question = question,
                            currentAnswer = answers[question.id] ?: "",
                            onAnswerChanged = { answers[question.id] = it },
                            pageNumber = currentIndex,
                            totalPages = totalSteps - 3, // Exclude entry, transition, summary, next
                            onBack = goBack,
                            onNext = goNext,
                            onSkip = goNext
                        )
                    }
                }
                
                is CalmFlowStep.Grounding -> {
                    GroundingScreen(
                        type = step.type,
                        onContinue = goNext
                    )
                }
                
                is CalmFlowStep.Transition -> {
                    TransitionScreen(
                        onComplete = goNext
                    )
                }
                
                is CalmFlowStep.Summary -> {
                    SummaryScreen(
                        answers = answers,
                        questions = questions,
                        onContinue = goNext
                    )
                }
                
                is CalmFlowStep.NextSteps -> {
                    NextStepsScreen(
                        onBreathing = onNavigateToBreathing,
                        onChat = onNavigateToChat,
                        onHome = onClose
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// 1. ENTRY / CONTEXT SCREEN
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun EntryScreen(
    onStart: () -> Unit,
    onClose: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { 
        delay(100)
        visible = true 
    }
    
    val currentTime = remember {
        val cal = java.util.Calendar.getInstance()
        val hour = cal.get(java.util.Calendar.HOUR_OF_DAY)
        val minute = cal.get(java.util.Calendar.MINUTE)
        String.format("%02d:%02d", hour, minute)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        // Close button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            CalmIconButton(
                icon = Icons.Default.Close,
                onClick = onClose
            )
        }
        
        Spacer(modifier = Modifier.weight(0.3f))
        
        // Main content
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(800, delayMillis = 200)) + slideInVertically(
                initialOffsetY = { 30 },
                animationSpec = tween(800, delayMillis = 200, easing = EaseOutCubic)
            )
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Subtle timestamp
                Text(
                    text = currentTime,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextDarkTertiary,
                    fontWeight = FontWeight.Light
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Title
                Text(
                    text = "Ease Anxiety",
                    style = MaterialTheme.typography.displaySmall,
                    color = OffWhite,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Reassurance
                Text(
                    text = "We'll take this one step at a time.\nThere's no rush.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextDarkSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.Light
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(0.4f))
        
        // Start button
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(600, delayMillis = 600))
        ) {
            CalmPrimaryButton(
                text = "Begin",
                onClick = onStart
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// 2. REFLECTION SCREEN (One question per page)
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun ReflectionScreen(
    question: ReflectionQuestion,
    currentAnswer: String,
    onAnswerChanged: (String) -> Unit,
    pageNumber: Int,
    totalPages: Int,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    
    LaunchedEffect(question.id) {
        visible = false
        delay(50)
        visible = true
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .systemBarsPadding()
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        // Top bar with page indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CalmIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                onClick = onBack
            )
            
            // Minimal page indicator
            Text(
                text = "${pageNumber} of $totalPages",
                style = MaterialTheme.typography.labelMedium,
                color = TextDarkTertiary,
                fontWeight = FontWeight.Light
            )
            
            // Skip option
            TextButton(onClick = onSkip) {
                Text(
                    text = "Skip",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextDarkSecondary,
                    fontWeight = FontWeight.Light
                )
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Question prompt
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(600)) + slideInVertically(
                initialOffsetY = { 20 },
                animationSpec = tween(600, easing = EaseOutCubic)
            )
        ) {
            Column {
                Text(
                    text = question.prompt,
                    style = MaterialTheme.typography.headlineSmall,
                    color = OffWhite,
                    fontWeight = FontWeight.Light,
                    lineHeight = 34.sp
                )
                
                question.subtitle?.let { subtitle ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextDarkSecondary,
                        fontWeight = FontWeight.Light,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Text input area
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(600, delayMillis = 200))
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                CalmTextInput(
                    value = currentAnswer,
                    onValueChange = onAnswerChanged,
                    placeholder = "Share what comes to mind...",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .focusRequester(focusRequester)
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Suggested chips
                if (question.suggestedChips.isNotEmpty()) {
                    Text(
                        text = "or choose a feeling",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextDarkTertiary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        question.suggestedChips.forEach { chip ->
                            SuggestionChip(
                                text = chip,
                                isSelected = currentAnswer.contains(chip, ignoreCase = true),
                                onClick = {
                                    val newAnswer = if (currentAnswer.isBlank()) chip
                                    else "$currentAnswer, $chip"
                                    onAnswerChanged(newAnswer)
                                }
                            )
                        }
                    }
                }
            }
        }
        
        // Navigation
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(400, delayMillis = 400))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalArrangement = Arrangement.End
            ) {
                CalmNavigationButton(
                    text = "Continue",
                    onClick = {
                        keyboardController?.hide()
                        onNext()
                    },
                    enabled = true
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// 3. GROUNDING SCREENS
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun GroundingScreen(
    type: GroundingType,
    onContinue: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        visible = true
    }
    
    val (title, subtitle, emoji) = when (type) {
        GroundingType.BREATHE -> Triple(
            "Let's pause for a moment.",
            "Take one slow, deep breath.\nIn through your nose... out through your mouth.",
            "🌬️"
        )
        GroundingType.BODY_SCAN -> Triple(
            "Notice your body.",
            "Where are you holding tension?\nSoften your shoulders. Unclench your jaw.",
            "🧘"
        )
        GroundingType.PAUSE -> Triple(
            "You're doing beautifully.",
            "Take a moment to appreciate yourself\nfor showing up right now.",
            "💙"
        )
    }
    
    // Breathing animation
    val infiniteTransition = rememberInfiniteTransition(label = "grounding")
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathScale"
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(800)) + scaleIn(
                initialScale = 0.9f,
                animationSpec = tween(800, easing = EaseOutCubic)
            )
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Emoji with breathing effect
                Text(
                    text = emoji,
                    fontSize = 64.sp,
                    modifier = Modifier.scale(breathScale)
                )
                
                Spacer(modifier = Modifier.height(40.dp))
                
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = OffWhite,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextDarkSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Light
                )
            }
        }
        
        Spacer(modifier = Modifier.height(80.dp))
        
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(600, delayMillis = 1000))
        ) {
            CalmPrimaryButton(
                text = "Continue",
                onClick = onContinue
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// 4. TRANSITION SCREEN
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun TransitionScreen(
    onComplete: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(100)
        visible = true
        delay(3000) // Hold for 3 seconds
        onComplete()
    }
    
    // Gentle pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "transition")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(1000))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "💙",
                    fontSize = 48.sp,
                    modifier = Modifier.alpha(alpha)
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = "Thank you for sharing.",
                    style = MaterialTheme.typography.headlineSmall,
                    color = OffWhite.copy(alpha = alpha),
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// 5. SUMMARY SCREEN
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun SummaryScreen(
    answers: Map<Int, String>,
    questions: List<ReflectionQuestion>,
    onContinue: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        visible = true
    }
    
    // Generate gentle acknowledgement based on answers
    val acknowledgement = remember(answers) {
        generateAcknowledgement(answers)
    }
    
    // Extract key themes
    val keyThemes = remember(answers) {
        extractKeyThemes(answers)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(800)) + slideInVertically(
                initialOffsetY = { 30 },
                animationSpec = tween(800, easing = EaseOutCubic)
            )
        ) {
            Column {
                // Header
                Text(
                    text = "A gentle reflection",
                    style = MaterialTheme.typography.labelMedium,
                    color = MutedTeal,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Main acknowledgement
                Text(
                    text = acknowledgement,
                    style = MaterialTheme.typography.headlineSmall,
                    color = OffWhite,
                    fontWeight = FontWeight.Light,
                    lineHeight = 34.sp
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Key themes card
                if (keyThemes.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = DeepSurface.copy(alpha = 0.6f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = "What came up for you:",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextDarkSecondary
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                keyThemes.forEach { theme ->
                                    ThemeChip(text = theme)
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Supportive message
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CalmingLavender.copy(alpha = 0.08f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "🕊️",
                            fontSize = 24.sp
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Text(
                            text = "Whatever you're feeling is valid. You don't have to fix anything right now. Just being here, just noticing, is enough.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OffWhite.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Light,
                            lineHeight = 24.sp
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(600, delayMillis = 600))
        ) {
            CalmPrimaryButton(
                text = "Continue",
                onClick = onContinue,
                modifier = Modifier.padding(vertical = 32.dp)
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// 6. NEXT STEPS SCREEN
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun NextStepsScreen(
    onBreathing: () -> Unit,
    onChat: () -> Unit,
    onHome: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        visible = true
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(800)) + slideInVertically(
                initialOffsetY = { 30 },
                animationSpec = tween(800, easing = EaseOutCubic)
            )
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "What would feel good right now?",
                    style = MaterialTheme.typography.headlineSmall,
                    color = OffWhite,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "No pressure. Choose what feels right.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDarkSecondary,
                    fontWeight = FontWeight.Light
                )
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(600, delayMillis = 300))
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NextStepCard(
                    emoji = "🌬️",
                    title = "Try a breathing exercise",
                    subtitle = "A gentle way to calm your nervous system",
                    accentColor = MutedTeal,
                    onClick = onBreathing
                )
                
                NextStepCard(
                    emoji = "💬",
                    title = "Talk to Aroura",
                    subtitle = "I'm here to listen, whenever you're ready",
                    accentColor = SoftBlue,
                    onClick = onChat
                )
                
                NextStepCard(
                    emoji = "🏠",
                    title = "Return to Home",
                    subtitle = "Take some quiet time for yourself",
                    accentColor = CalmingLavender,
                    onClick = onHome
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun NextStepCard(
    emoji: String,
    title: String,
    subtitle: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "nextStepScale"
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.1f),
                            accentColor.copy(alpha = 0.05f)
                        )
                    )
                )
                .border(
                    1.dp,
                    accentColor.copy(alpha = 0.15f),
                    RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = emoji,
                    fontSize = 32.sp
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = OffWhite,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextDarkSecondary,
                        fontWeight = FontWeight.Light
                    )
                }
                
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// REUSABLE COMPONENTS
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun CalmAnxietyBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "calmBg")
    
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(40000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bgGradient"
    )
    
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bgGlow"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightCharcoal)
    ) {
        // Subtle gradient layer
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F1318),
                            Color(0xFF0A0D12),
                            Color(0xFF0D1015)
                        ),
                        startY = gradientOffset * 200
                    )
                )
        )
        
        // Soft glow orbs
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 80.dp, y = (-40).dp)
                .size(300.dp)
                .blur(80.dp)
                .alpha(glowAlpha)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            CalmingLavender.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )
        
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-60).dp, y = 60.dp)
                .size(250.dp)
                .blur(60.dp)
                .alpha(glowAlpha * 0.8f)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MutedTeal.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )
    }
}

@Composable
private fun CalmIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "iconBtnScale"
    )
    
    Box(
        modifier = Modifier
            .size(44.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(DeepSurface.copy(alpha = 0.5f))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = TextDarkSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun CalmPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "primaryBtnScale"
    )
    
    // Subtle glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "btnGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "btnGlowAlpha"
    )
    
    Box(modifier = modifier) {
        // Glow behind button
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(width = 200.dp, height = 60.dp)
                .blur(20.dp)
                .alpha(glowAlpha)
                .background(MutedTeal.copy(alpha = 0.5f), RoundedCornerShape(30.dp))
        )
        
        Button(
            onClick = onClick,
            modifier = Modifier
                .scale(scale)
                .height(56.dp)
                .fillMaxWidth(0.7f),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MutedTeal.copy(alpha = 0.9f)
            ),
            interactionSource = interactionSource
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = MidnightCharcoal,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun CalmNavigationButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "navBtnScale"
    )
    
    Box(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        MutedTeal.copy(alpha = 0.2f),
                        MutedTeal.copy(alpha = 0.1f)
                    )
                )
            )
            .border(
                1.dp,
                MutedTeal.copy(alpha = 0.3f),
                RoundedCornerShape(24.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = 24.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall,
                color = MutedTeal,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MutedTeal,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun CalmTextInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(
            color = OffWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.Light,
            lineHeight = 26.sp
        ),
        cursorBrush = SolidColor(MutedTeal),
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DeepSurface.copy(alpha = 0.4f))
            .border(
                1.dp,
                Color.White.copy(alpha = 0.05f),
                RoundedCornerShape(16.dp)
            )
            .padding(20.dp),
        decorationBox = { innerTextField ->
            Box {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = TextStyle(
                            color = TextDarkTertiary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Light
                        )
                    )
                }
                innerTextField()
            }
        }
    )
}

@Composable
private fun SuggestionChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "chipScale"
    )
    
    Box(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) MutedTeal.copy(alpha = 0.2f)
                else DeepSurface.copy(alpha = 0.5f)
            )
            .border(
                1.dp,
                if (isSelected) MutedTeal.copy(alpha = 0.4f)
                else Color.White.copy(alpha = 0.08f),
                RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) MutedTeal else TextDarkSecondary,
            fontWeight = FontWeight.Light
        )
    }
}

@Composable
private fun ThemeChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CalmingLavender.copy(alpha = 0.15f))
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = CalmingLavender,
            fontWeight = FontWeight.Medium
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// HELPER FUNCTIONS
// ═══════════════════════════════════════════════════════════════════════════════

private fun generateAcknowledgement(answers: Map<Int, String>): String {
    val hasContent = answers.values.any { it.isNotBlank() }
    
    if (!hasContent) {
        return "Sometimes there are no words—and that's okay. Your presence here matters."
    }
    
    val feelings = mutableListOf<String>()
    
    // Analyze answers for themes
    val allText = answers.values.joinToString(" ").lowercase()
    
    if (allText.contains("alone") || allText.contains("lonely")) {
        feelings.add("a sense of loneliness")
    }
    if (allText.contains("overwhelm") || allText.contains("everything") || allText.contains("too much")) {
        feelings.add("feeling overwhelmed")
    }
    if (allText.contains("work") || allText.contains("job") || allText.contains("career")) {
        feelings.add("work-related stress")
    }
    if (allText.contains("fear") || allText.contains("afraid") || allText.contains("scared")) {
        feelings.add("fear about the unknown")
    }
    if (allText.contains("tired") || allText.contains("exhaust")) {
        feelings.add("exhaustion")
    }
    if (allText.contains("relationship") || allText.contains("partner") || allText.contains("love")) {
        feelings.add("relationship concerns")
    }
    
    return if (feelings.isNotEmpty()) {
        "It sounds like you've been carrying ${feelings.take(2).joinToString(" and ")}. That's a lot to hold."
    } else {
        "Thank you for being honest about what you're experiencing. Your feelings are valid."
    }
}

private fun extractKeyThemes(answers: Map<Int, String>): List<String> {
    val themes = mutableListOf<String>()
    val allText = answers.values.joinToString(" ").lowercase()
    
    val keywordMap = mapOf(
        "work" to "work stress",
        "relationship" to "relationships",
        "alone" to "loneliness",
        "afraid" to "fear",
        "overwhelm" to "overwhelm",
        "tired" to "exhaustion",
        "future" to "uncertainty",
        "health" to "health worries",
        "money" to "financial stress",
        "family" to "family"
    )
    
    keywordMap.forEach { (keyword, theme) ->
        if (allText.contains(keyword)) {
            themes.add(theme)
        }
    }
    
    return themes.take(4)
}
