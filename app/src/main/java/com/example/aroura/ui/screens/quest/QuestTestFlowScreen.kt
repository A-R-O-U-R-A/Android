package com.example.aroura.ui.screens.quest

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.data.*
import com.example.aroura.data.api.QuestSectionAnswerData
import com.example.aroura.ui.components.ArouraBackground
import com.example.aroura.ui.components.reflect.*
import com.example.aroura.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Quest Test Flow Screen
 *
 * Handles the question flow for each quest section:
 * 1. Get Started - intro with section info
 * 2. Questions (8 per section) - various question types
 * 3. Analyzing - calm transition
 * 4. Complete - section done, shows summary
 */

sealed class QuestFlowState {
    data object GetStarted : QuestFlowState()
    data class Question(val index: Int) : QuestFlowState()
    data object Analyzing : QuestFlowState()
    data object Complete : QuestFlowState()
}

@Composable
fun QuestTestFlowScreen(
    questId: String,
    sectionId: String,
    onClose: () -> Unit,
    onComplete: (questId: String, sectionId: String, answers: List<QuestSectionAnswerData>) -> Unit
) {
    val questInfo = remember { QuestQuestionRepository.getQuestInfo(questId) }
    val sectionInfo = remember { QuestQuestionRepository.getSectionInfo(questId, sectionId) }
    val questions = remember { QuestQuestionRepository.getQuestions(questId, sectionId) }

    if (sectionInfo == null || questions.isEmpty()) {
        // Fallback if data not found
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Section not found", color = OffWhite)
        }
        return
    }

    var flowState by remember { mutableStateOf<QuestFlowState>(QuestFlowState.GetStarted) }
    val answers = remember { mutableStateMapOf<Int, Any>() }

    // Section accent color based on quest
    val accentColor = remember {
        when (questId) {
            "emotional_awareness" -> CalmingLavender
            "mindset_growth" -> SoftBlue
            "core_personality" -> CalmingPeach
            else -> MutedTeal
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ArouraBackground()

        AnimatedContent(
            targetState = flowState,
            transitionSpec = {
                fadeIn(tween(300)) + slideInHorizontally { it / 3 } togetherWith
                fadeOut(tween(200)) + slideOutHorizontally { -it / 3 }
            },
            label = "quest_flow"
        ) { state ->
            when (state) {
                is QuestFlowState.GetStarted -> QuestGetStartedScreen(
                    questTitle = questInfo?.title ?: "",
                    sectionTitle = sectionInfo.title,
                    sectionDescription = sectionInfo.description,
                    sectionEmoji = sectionInfo.emoji,
                    questionCount = questions.size,
                    accentColor = accentColor,
                    onStart = { flowState = QuestFlowState.Question(0) },
                    onClose = onClose
                )

                is QuestFlowState.Question -> QuestQuestionScreen(
                    question = questions[state.index],
                    questionNumber = state.index + 1,
                    totalQuestions = questions.size,
                    accentColor = accentColor,
                    previousAnswer = answers[state.index],
                    onAnswer = { answer ->
                        answers[state.index] = answer
                        if (state.index < questions.size - 1) {
                            flowState = QuestFlowState.Question(state.index + 1)
                        } else {
                            flowState = QuestFlowState.Analyzing
                        }
                    },
                    onBack = {
                        if (state.index > 0) {
                            flowState = QuestFlowState.Question(state.index - 1)
                        } else {
                            flowState = QuestFlowState.GetStarted
                        }
                    },
                    onClose = onClose
                )

                is QuestFlowState.Analyzing -> QuestAnalyzingScreen(
                    accentColor = accentColor,
                    onFinish = { flowState = QuestFlowState.Complete }
                )

                is QuestFlowState.Complete -> QuestCompleteScreen(
                    sectionTitle = sectionInfo.title,
                    sectionEmoji = sectionInfo.emoji,
                    accentColor = accentColor,
                    onContinue = {
                        // Build the answer data list
                        val answerList = questions.mapIndexed { index, q ->
                            QuestSectionAnswerData(
                                questionIndex = index,
                                questionText = q.text,
                                answer = answers[index]?.toString() ?: ""
                            )
                        }
                        onComplete(questId, sectionId, answerList)
                    }
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// GET STARTED SCREEN
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun QuestGetStartedScreen(
    questTitle: String,
    sectionTitle: String,
    sectionDescription: String,
    sectionEmoji: String,
    questionCount: Int,
    accentColor: Color,
    onStart: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = TextDarkSecondary)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Emoji
        Text(
            text = sectionEmoji,
            fontSize = 72.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Quest title
        Text(
            text = questTitle,
            color = accentColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Section title
        Text(
            text = sectionTitle,
            color = OffWhite,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Description
        Text(
            text = sectionDescription,
            color = TextDarkSecondary,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Question count badge
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = accentColor.copy(alpha = 0.15f)
        ) {
            Text(
                text = "$questionCount questions • ~3 min",
                color = accentColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Start button
        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Begin Section",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DeepSurface
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// QUESTION SCREEN
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun QuestQuestionScreen(
    question: QuestQuestion,
    questionNumber: Int,
    totalQuestions: Int,
    accentColor: Color,
    previousAnswer: Any?,
    onAnswer: (Any) -> Unit,
    onBack: () -> Unit,
    onClose: () -> Unit
) {
    var selectedAnswer by remember(questionNumber) { mutableStateOf<Any?>(previousAnswer) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .systemBarsPadding()
    ) {
        // Top bar with progress
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("Back", color = TextDarkSecondary, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "$questionNumber/$totalQuestions",
                color = TextDarkSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = TextDarkSecondary)
            }
        }

        // Progress bar
        LinearProgressIndicator(
            progress = { questionNumber.toFloat() / totalQuestions.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = accentColor,
            trackColor = accentColor.copy(alpha = 0.15f)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Question text
        Text(
            text = question.text,
            color = OffWhite,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 30.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Answer input based on type
        when (question.type) {
            QuestionType.YES_NO_SOMETIMES -> {
                YesNoSometimesInput(
                    selectedOption = selectedAnswer as? String,
                    accentColor = accentColor,
                    onSelect = { selectedAnswer = it }
                )
            }

            QuestionType.LIKERT_5 -> {
                LikertScaleInput(
                    selectedValue = selectedAnswer as? Int,
                    lowLabel = question.likertLabels.first.ifEmpty { "Not at all" },
                    highLabel = question.likertLabels.second.ifEmpty { "Very much" },
                    accentColor = accentColor,
                    onSelect = { selectedAnswer = it }
                )
            }

            QuestionType.LIKERT_7 -> {
                LikertScaleInput(
                    selectedValue = selectedAnswer as? Int,
                    lowLabel = question.likertLabels.first.ifEmpty { "Strongly disagree" },
                    highLabel = question.likertLabels.second.ifEmpty { "Strongly agree" },
                    accentColor = accentColor,
                    onSelect = { selectedAnswer = it }
                )
            }

            QuestionType.MULTIPLE_CHOICE -> {
                Column(verticalArrangement = Arrangement.spacedBy(ArouraSpacing.sm.dp)) {
                    question.options.forEach { option ->
                        AnswerOptionButton(
                            text = option,
                            isSelected = selectedAnswer == option,
                            accentColor = accentColor,
                            onClick = { selectedAnswer = option }
                        )
                    }
                }
            }

            QuestionType.SHORT_TEXT -> {
                QuestShortTextInput(
                    value = selectedAnswer as? String ?: "",
                    accentColor = accentColor,
                    onValueChange = { selectedAnswer = it }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Next/Submit button
        Button(
            onClick = { selectedAnswer?.let { onAnswer(it) } },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = selectedAnswer != null && (selectedAnswer as? String)?.isNotBlank() != false,
            colors = ButtonDefaults.buttonColors(
                containerColor = accentColor,
                disabledContainerColor = accentColor.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = if (questionNumber == totalQuestions) "Finish" else "Next",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (selectedAnswer != null) DeepSurface else TextDarkSecondary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SHORT TEXT INPUT (self-contained for quest flow)
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun QuestShortTextInput(
    value: String,
    accentColor: Color,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp)
                .background(DeepSurface, RoundedCornerShape(16.dp))
                .padding(16.dp),
            textStyle = TextStyle(
                color = OffWhite,
                fontSize = 16.sp,
                lineHeight = 24.sp
            ),
            cursorBrush = SolidColor(accentColor),
            decorationBox = { innerTextField ->
                Box {
                    if (value.isEmpty()) {
                        Text(
                            text = "Type your answer here...",
                            color = TextDarkTertiary,
                            fontSize = 16.sp
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// ANALYZING SCREEN
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun QuestAnalyzingScreen(
    accentColor: Color,
    onFinish: () -> Unit
) {
    // Auto-navigate after delay
    LaunchedEffect(Unit) {
        delay(2500)
        onFinish()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "analyzing")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Pulsing circle
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                accentColor.copy(alpha = pulseAlpha * 0.6f),
                                accentColor.copy(alpha = pulseAlpha * 0.1f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("✨", fontSize = 36.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Processing your answers...",
                color = OffWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Building your self-discovery profile",
                color = TextDarkSecondary,
                fontSize = 14.sp
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// COMPLETE SCREEN
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun QuestCompleteScreen(
    sectionTitle: String,
    sectionEmoji: String,
    accentColor: Color,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Success emoji
        Text(
            text = "🎉",
            fontSize = 64.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Section Complete!",
            color = OffWhite,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "$sectionEmoji $sectionTitle",
            color = accentColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Your answers have been saved.\nYou're one step closer to understanding yourself!",
            color = TextDarkSecondary,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Continue button
        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Continue",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DeepSurface
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
