package com.example.aroura.ui.screens.reflect

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.data.*
import com.example.aroura.ui.components.ArouraBackground
import com.example.aroura.ui.components.reflect.*
import com.example.aroura.ui.theme.DeepSurface
import com.example.aroura.ui.theme.OffWhite
import kotlinx.coroutines.delay

/**
 * Test Flow Screen - Complete Test Experience
 * 
 * Handles the 4-step flow for every test:
 * 1. Get Started Screen
 * 2. Question Screens (15-25 questions)
 * 3. Transition/Analysis Screen
 * 4. Results/Summary Screen
 */

sealed class TestFlowState {
    data object GetStarted : TestFlowState()
    data class Question(val index: Int) : TestFlowState()
    data object Analyzing : TestFlowState()
    data object Results : TestFlowState()
}

@Composable
fun TestFlowScreen(
    testId: ReflectTestId,
    onBack: () -> Unit,
    onComplete: (TestResult) -> Unit
) {
    val test = remember { ReflectTestRepository.getTestById(testId) }
    
    if (test == null) {
        onBack()
        return
    }
    
    // Generate questions for this test (in production, these would come from backend)
    val questions = remember(testId) { generateQuestionsForTest(test) }
    
    var flowState by remember { mutableStateOf<TestFlowState>(TestFlowState.GetStarted) }
    var answers by remember { mutableStateOf(mutableMapOf<Int, Any>()) }
    
    // Handle analyzing transition
    LaunchedEffect(flowState) {
        if (flowState is TestFlowState.Analyzing) {
            delay(2500) // Calm transition pause
            flowState = TestFlowState.Results
        }
    }
    
    val accentColor = Color(test.accentColorHex)
    
    Box(modifier = Modifier.fillMaxSize()) {
        ArouraBackground()
        
        AnimatedContent(
            targetState = flowState,
            transitionSpec = {
                fadeIn(tween(400)) + slideInHorizontally(
                    initialOffsetX = { if (targetState is TestFlowState.GetStarted) -it else it },
                    animationSpec = tween(400, easing = EaseOutCubic)
                ) togetherWith fadeOut(tween(300))
            },
            label = "flowTransition"
        ) { state ->
            when (state) {
                is TestFlowState.GetStarted -> {
                    TestGetStartedScreen(
                        test = test,
                        onBack = onBack,
                        onStart = { flowState = TestFlowState.Question(0) }
                    )
                }
                
                is TestFlowState.Question -> {
                    val questionIndex = state.index
                    val question = questions.getOrNull(questionIndex)
                    
                    if (question != null) {
                        TestQuestionContent(
                            test = test,
                            question = question,
                            questionNumber = questionIndex + 1,
                            totalQuestions = questions.size,
                            currentAnswer = answers[questionIndex],
                            accentColor = accentColor,
                            onBack = {
                                if (questionIndex > 0) {
                                    flowState = TestFlowState.Question(questionIndex - 1)
                                } else {
                                    flowState = TestFlowState.GetStarted
                                }
                            },
                            onAnswer = { answer ->
                                answers[questionIndex] = answer
                                // Auto-advance after brief delay
                                if (questionIndex < questions.size - 1) {
                                    flowState = TestFlowState.Question(questionIndex + 1)
                                } else {
                                    flowState = TestFlowState.Analyzing
                                }
                            }
                        )
                    }
                }
                
                is TestFlowState.Analyzing -> {
                    TransitionScreen(
                        message = "Analyzing your responses…",
                        accentColor = accentColor
                    )
                }
                
                is TestFlowState.Results -> {
                    val result = remember(answers) {
                        calculateResult(test, questions, answers)
                    }
                    
                    TestResultScreen(
                        test = test,
                        result = result,
                        onRetake = {
                            answers.clear()
                            flowState = TestFlowState.GetStarted
                        },
                        onBack = {
                            onComplete(result)
                            onBack()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TestQuestionContent(
    test: ReflectTest,
    question: TestQuestion,
    questionNumber: Int,
    totalQuestions: Int,
    currentAnswer: Any?,
    accentColor: Color,
    onBack: () -> Unit,
    onAnswer: (Any) -> Unit
) {
    // Auto-advance delay after selection
    var selectedAnswer by remember(questionNumber) { mutableStateOf(currentAnswer) }
    
    LaunchedEffect(selectedAnswer) {
        if (selectedAnswer != null && selectedAnswer != currentAnswer) {
            delay(400) // Brief pause before advancing
            onAnswer(selectedAnswer!!)
        }
    }
    
    TestQuestionScreen(
        questionNumber = questionNumber,
        totalQuestions = totalQuestions,
        questionText = question.text,
        accentColor = accentColor,
        onBack = onBack
    ) {
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
                androidx.compose.foundation.layout.Column(
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                        com.example.aroura.ui.theme.ArouraSpacing.sm.dp
                    )
                ) {
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
                // Text input for reflective questions
                ShortTextInput(
                    value = selectedAnswer as? String ?: "",
                    accentColor = accentColor,
                    onValueChange = { selectedAnswer = it },
                    onSubmit = { onAnswer(selectedAnswer ?: "") }
                )
            }
        }
    }
}

@Composable
private fun ShortTextInput(
    value: String,
    accentColor: Color,
    onValueChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        androidx.compose.foundation.text.BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = androidx.compose.ui.text.TextStyle(
                color = com.example.aroura.ui.theme.OffWhite,
                fontSize = 16.sp
            ),
            cursorBrush = androidx.compose.ui.graphics.SolidColor(accentColor),
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    com.example.aroura.ui.theme.DeepSurface.copy(alpha = 0.5f),
                    androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        PremiumStartButton(
            text = "Continue",
            accentColor = accentColor,
            onClick = onSubmit
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// QUESTION GENERATION (In production, this comes from backend/database)
// ═══════════════════════════════════════════════════════════════════════════════

private fun generateQuestionsForTest(test: ReflectTest): List<TestQuestion> {
    // Generate appropriate questions based on test type
    return when (test.id) {
        // Personality & Identity
        ReflectTestId.PERSONALITY_TYPE -> generatePersonalityTypeQuestions()
        ReflectTestId.TEMPERAMENT_TYPE -> generateTemperamentQuestions()
        ReflectTestId.JUNGIAN_ARCHETYPE -> generateArchetypeQuestions()
        ReflectTestId.MASCULINE_FEMININE_BALANCE -> generateEnergyBalanceQuestions()
        ReflectTestId.CHARISMA_LEVEL -> generateCharismaQuestions()
        ReflectTestId.SOCIAL_STYLE -> generateSocialStyleQuestions()
        ReflectTestId.EMPATHY_LEVEL -> generateEmpathyQuestions()
        
        // Emotional Health
        ReflectTestId.EMOTIONAL_INTELLIGENCE -> generateEQQuestions()
        ReflectTestId.ANGER_SCALE -> generateAngerQuestions()
        ReflectTestId.MOOD_SWINGS -> generateMoodSwingsQuestions()
        ReflectTestId.OVERWHELM_SCALE -> generateOverwhelmQuestions()
        ReflectTestId.EMOTIONAL_BURNOUT -> generateBurnoutQuestions()
        ReflectTestId.LOW_MOOD_CHECK -> generateLowMoodQuestions()
        ReflectTestId.DEPRESSION_LEVEL -> generateDepressionQuestions()
        ReflectTestId.HAPPINESS_LEVEL -> generateHappinessQuestions()
        ReflectTestId.LONELINESS_SCALE -> generateLonelinessQuestions()
        
        // Childhood
        ReflectTestId.CHILDHOOD_EXPERIENCES -> generateChildhoodQuestions()
        ReflectTestId.INNER_CHILD -> generateInnerChildQuestions()
        ReflectTestId.WOUNDED_INNER_CHILD -> generateWoundedChildQuestions()
        ReflectTestId.FATHER_IMPACT -> generateFatherImpactQuestions()
        ReflectTestId.MOTHER_IMPACT -> generateMotherImpactQuestions()
        
        // Relationships
        ReflectTestId.ATTACHMENT_STYLE -> generateAttachmentQuestions()
        ReflectTestId.LOVE_LANGUAGE -> generateLoveLanguageQuestions()
        ReflectTestId.PAST_RELATIONSHIP_IMPACT -> generatePastRelationshipQuestions()
        ReflectTestId.TOXIC_PARTNER_CHECK -> generateToxicPartnerQuestions()
        ReflectTestId.GASLIGHTING_CHECK -> generateGaslightingQuestions()
        ReflectTestId.TOXIC_SIBLING_DYNAMICS -> generateSiblingQuestions()
        
        // Trauma & Coping
        ReflectTestId.PAST_TRAUMA_ASSESSMENT -> generateTraumaQuestions()
        ReflectTestId.IMPOSTER_SYNDROME -> generateImposterQuestions()
        ReflectTestId.PEOPLE_PLEASING_CHECK -> generatePeoplePleasingQuestions()
        ReflectTestId.BODY_IMAGE_ISSUES -> generateBodyImageQuestions()
        ReflectTestId.FOCUS_SKILLS -> generateFocusQuestions()
        
        // Toxicity
        ReflectTestId.MANIPULATION_TENDENCY -> generateManipulationQuestions()
        ReflectTestId.NARCISSISTIC_TRAITS -> generateNarcissisticTraitsQuestions()
        ReflectTestId.NARCISSISTIC_TYPE -> generateNarcissisticTypeQuestions()
        ReflectTestId.NARCISSISTIC_IMPACT -> generateNarcissisticImpactQuestions()
        ReflectTestId.TOXIC_TRAITS_ASSESSMENT -> generateToxicTraitsQuestions()
        
        // Growth
        ReflectTestId.CAREER_GUIDANCE -> generateCareerQuestions()
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// RESULT CALCULATION
// ═══════════════════════════════════════════════════════════════════════════════

private fun calculateResult(
    test: ReflectTest,
    questions: List<TestQuestion>,
    answers: Map<Int, Any>
): TestResult {
    // Basic scoring logic - in production this would be more sophisticated
    var totalScore = 0f
    var maxScore = 0f
    
    questions.forEachIndexed { index, question ->
        val answer = answers[index]
        when (question.type) {
            QuestionType.YES_NO_SOMETIMES -> {
                maxScore += 2f
                when (answer) {
                    "Yes" -> totalScore += 2f
                    "Sometimes" -> totalScore += 1f
                    "No" -> totalScore += 0f
                }
            }
            QuestionType.LIKERT_5 -> {
                maxScore += 5f
                totalScore += (answer as? Int)?.toFloat() ?: 0f
            }
            QuestionType.LIKERT_7 -> {
                maxScore += 7f
                totalScore += (answer as? Int)?.toFloat() ?: 0f
            }
            else -> {
                maxScore += 1f
                if (answer != null) totalScore += 1f
            }
        }
    }
    
    val normalizedScore = if (maxScore > 0) (totalScore / maxScore) * 100f else 50f
    
    // Generate result based on test type
    return generateResultForTest(test, normalizedScore)
}

private fun generateResultForTest(test: ReflectTest, score: Float): TestResult {
    // This would be much more sophisticated in production
    val (label, description, insights, reflection) = when {
        score >= 75 -> Quadruple(
            "High",
            "Your responses indicate a strong presence of these traits. This can be a significant strength when balanced with self-awareness.",
            listOf(
                "You show strong tendencies in this area",
                "Consider how these traits serve you",
                "Balance is key to wellbeing"
            ),
            "Take a moment to reflect on how these patterns show up in your daily life."
        )
        score >= 50 -> Quadruple(
            "Moderate",
            "You show a balanced presence of these characteristics. This middle ground often indicates adaptability and situational awareness.",
            listOf(
                "Your responses show healthy variation",
                "You adapt to different situations",
                "Continue observing your patterns"
            ),
            "Notice when and where you lean more strongly in either direction."
        )
        score >= 25 -> Quadruple(
            "Low-Moderate",
            "Your responses suggest these traits are present but not dominant. This can indicate room for growth or simply different priorities.",
            listOf(
                "These traits aren't your primary mode",
                "Consider if growth in this area would serve you",
                "Your uniqueness is valuable"
            ),
            "Reflect on whether you'd like to develop more in this area."
        )
        else -> Quadruple(
            "Low",
            "Your responses indicate minimal presence of these characteristics. This isn't good or bad—just information about your current patterns.",
            listOf(
                "This area isn't prominent for you",
                "Consider what does define you",
                "Self-knowledge is the goal"
            ),
            "What traits do you feel are more central to who you are?"
        )
    }
    
    // Add test-specific labels
    val specificLabel = when (test.id) {
        ReflectTestId.ATTACHMENT_STYLE -> when {
            score >= 70 -> "Secure Attachment"
            score >= 50 -> "Anxious-Secure"
            score >= 30 -> "Avoidant-Leaning"
            else -> "Disorganized Patterns"
        }
        ReflectTestId.EMPATHY_LEVEL -> when {
            score >= 75 -> "Highly Empathic"
            score >= 50 -> "Balanced Empathy"
            else -> "Developing Empathy"
        }
        ReflectTestId.EMOTIONAL_INTELLIGENCE -> when {
            score >= 75 -> "High EQ"
            score >= 50 -> "Moderate EQ"
            else -> "Developing EQ"
        }
        else -> "$label ${test.title.split(" ").first()}"
    }
    
    // Generate category breakdown for multi-dimensional tests
    val categories = when (test.id) {
        ReflectTestId.EMOTIONAL_INTELLIGENCE -> mapOf(
            "Self-Awareness" to (score * 0.9f + 10).coerceIn(0f, 100f),
            "Self-Regulation" to (score * 1.1f - 5).coerceIn(0f, 100f),
            "Motivation" to (score * 0.95f + 5).coerceIn(0f, 100f),
            "Empathy" to (score * 1.05f).coerceIn(0f, 100f),
            "Social Skills" to (score * 0.9f + 8).coerceIn(0f, 100f)
        )
        ReflectTestId.LOVE_LANGUAGE -> mapOf(
            "Words of Affirmation" to (score * 0.8f + 15).coerceIn(0f, 100f),
            "Quality Time" to (score * 1.1f).coerceIn(0f, 100f),
            "Acts of Service" to (score * 0.9f + 10).coerceIn(0f, 100f),
            "Physical Touch" to (score * 0.95f + 5).coerceIn(0f, 100f),
            "Gifts" to (score * 0.85f + 12).coerceIn(0f, 100f)
        )
        ReflectTestId.PERSONALITY_TYPE -> mapOf(
            "Introversion" to (100 - score * 0.9f).coerceIn(0f, 100f),
            "Intuition" to (score * 1.05f).coerceIn(0f, 100f),
            "Thinking" to (score * 0.85f + 15).coerceIn(0f, 100f),
            "Judging" to (score * 0.95f + 5).coerceIn(0f, 100f)
        )
        else -> emptyMap()
    }
    
    return TestResult(
        testId = test.id,
        primaryScore = score,
        categories = categories,
        primaryLabel = specificLabel,
        description = description,
        insights = insights,
        reflection = reflection
    )
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

// ═══════════════════════════════════════════════════════════════════════════════
// QUESTION GENERATORS - Sample questions for each test
// ═══════════════════════════════════════════════════════════════════════════════

private fun generatePersonalityTypeQuestions() = listOf(
    TestQuestion(1, "Do you feel energized after spending time with large groups of people?", QuestionType.YES_NO_SOMETIMES),
    TestQuestion(2, "Do you prefer to have detailed plans rather than going with the flow?", QuestionType.YES_NO_SOMETIMES),
    TestQuestion(3, "When making decisions, do you rely more on logic than emotions?", QuestionType.YES_NO_SOMETIMES),
    TestQuestion(4, "Do you often find yourself daydreaming about possibilities?", QuestionType.YES_NO_SOMETIMES),
    TestQuestion(5, "How comfortable are you with spontaneous changes?", QuestionType.LIKERT_5, likertLabels = "Very uncomfortable" to "Very comfortable"),
    TestQuestion(6, "Do you prefer working on one task at a time rather than multitasking?", QuestionType.YES_NO_SOMETIMES),
    TestQuestion(7, "In conversations, do you tend to listen more than talk?", QuestionType.YES_NO_SOMETIMES),
    TestQuestion(8, "Do you find abstract concepts more interesting than practical details?", QuestionType.YES_NO_SOMETIMES),
    TestQuestion(9, "How often do you consider others' feelings when making decisions?", QuestionType.LIKERT_5, likertLabels = "Rarely" to "Always"),
    TestQuestion(10, "Do you feel stressed when things are left open-ended?", QuestionType.YES_NO_SOMETIMES),
    TestQuestion(11, "Do you prefer deep conversations over small talk?", QuestionType.YES_NO_SOMETIMES),
    TestQuestion(12, "How much do you value tradition and established methods?", QuestionType.LIKERT_5, likertLabels = "Not at all" to "Very much"),
    TestQuestion(13, "Do you often play devil's advocate in discussions?", QuestionType.YES_NO_SOMETIMES),
    TestQuestion(14, "How often do you seek out new experiences?", QuestionType.LIKERT_5, likertLabels = "Rarely" to "Very often"),
    TestQuestion(15, "Do you need time alone to recharge after social events?", QuestionType.YES_NO_SOMETIMES),
    TestQuestion(16, "When solving problems, do you focus on the big picture first?", QuestionType.YES_NO_SOMETIMES),
    TestQuestion(17, "How important is maintaining harmony in your relationships?", QuestionType.LIKERT_5, likertLabels = "Not important" to "Very important"),
    TestQuestion(18, "Do you prefer to keep your options open rather than commit early?", QuestionType.YES_NO_SOMETIMES),
    TestQuestion(19, "How often do you trust your gut instincts?", QuestionType.LIKERT_5, likertLabels = "Rarely" to "Always"),
    TestQuestion(20, "Do you find it easy to adapt to new social situations?", QuestionType.YES_NO_SOMETIMES)
)

private fun generateTemperamentQuestions() = listOf(
    TestQuestion(1, "How quickly do you typically react to emotional situations?", QuestionType.LIKERT_5, likertLabels = "Very slowly" to "Very quickly"),
    TestQuestion(2, "Do you often feel restless or need to stay busy?", QuestionType.YES_NO_SOMETIMES),
    TestQuestion(3, "How easily do you become frustrated?", QuestionType.LIKERT_5, likertLabels = "Not easily" to "Very easily"),
    TestQuestion(4, "Do you prefer calm, predictable environments?", QuestionType.YES_NO_SOMETIMES),
    TestQuestion(5, "How long does it take you to warm up to new people?", QuestionType.LIKERT_5, likertLabels = "Very quick" to "Very long"),
    TestQuestion(6, "Do you tend to see the glass as half full or half empty?", QuestionType.LIKERT_5, likertLabels = "Half empty" to "Half full"),
    TestQuestion(7, "How often do you feel deeply moved by art or music?", QuestionType.LIKERT_5, likertLabels = "Rarely" to "Very often"),
    TestQuestion(8, "Do you prefer routine over variety in daily life?", QuestionType.YES_NO_SOMETIMES),
    TestQuestion(9, "How sensitive are you to criticism?", QuestionType.LIKERT_5, likertLabels = "Not sensitive" to "Very sensitive"),
    TestQuestion(10, "Do you often feel the need to lead in group situations?", QuestionType.YES_NO_SOMETIMES),
    TestQuestion(11, "How quickly do you bounce back from disappointments?", QuestionType.LIKERT_5, likertLabels = "Very slowly" to "Very quickly"),
    TestQuestion(12, "Do you prefer to process emotions internally?", QuestionType.YES_NO_SOMETIMES),
    TestQuestion(13, "How comfortable are you with taking risks?", QuestionType.LIKERT_5, likertLabels = "Not comfortable" to "Very comfortable"),
    TestQuestion(14, "Do you often feel overwhelmed by strong emotions?", QuestionType.YES_NO_SOMETIMES),
    TestQuestion(15, "How much energy do you typically have throughout the day?", QuestionType.LIKERT_5, likertLabels = "Very low" to "Very high"),
    TestQuestion(16, "Do you prefer thinking before speaking?", QuestionType.YES_NO_SOMETIMES),
    TestQuestion(17, "How often do you feel content with life as it is?", QuestionType.LIKERT_5, likertLabels = "Rarely" to "Often"),
    TestQuestion(18, "Do you tend to avoid conflict?", QuestionType.YES_NO_SOMETIMES)
)

private fun generateArchetypeQuestions() = listOf(
    TestQuestion(1, "Do you feel drawn to help others find their way?", QuestionType.YES_NO_SOMETIMES, category = "Sage"),
    TestQuestion(2, "Do you often challenge established rules or systems?", QuestionType.YES_NO_SOMETIMES, category = "Rebel"),
    TestQuestion(3, "Do you dream of creating something that lasts?", QuestionType.YES_NO_SOMETIMES, category = "Creator"),
    TestQuestion(4, "Do you feel called to protect those who cannot protect themselves?", QuestionType.YES_NO_SOMETIMES, category = "Hero"),
    TestQuestion(5, "How much do you value comfort and pleasure?", QuestionType.LIKERT_5, category = "Lover", likertLabels = "Not much" to "Very much"),
    TestQuestion(6, "Do you often use humor to connect with others?", QuestionType.YES_NO_SOMETIMES, category = "Jester"),
    TestQuestion(7, "Do you believe you have a special destiny to fulfill?", QuestionType.YES_NO_SOMETIMES, category = "Hero"),
    TestQuestion(8, "How important is belonging to a community?", QuestionType.LIKERT_5, category = "Everyman", likertLabels = "Not important" to "Very important"),
    TestQuestion(9, "Do you seek to understand life's deeper mysteries?", QuestionType.YES_NO_SOMETIMES, category = "Sage"),
    TestQuestion(10, "Do you often feel like you don't fit in with mainstream society?", QuestionType.YES_NO_SOMETIMES, category = "Rebel"),
    TestQuestion(11, "How much do you value self-expression?", QuestionType.LIKERT_5, category = "Creator", likertLabels = "Not much" to "Very much"),
    TestQuestion(12, "Do you believe love can transform people?", QuestionType.YES_NO_SOMETIMES, category = "Lover"),
    TestQuestion(13, "Do you prefer simple pleasures over luxury?", QuestionType.YES_NO_SOMETIMES, category = "Everyman"),
    TestQuestion(14, "How often do you use humor in difficult situations?", QuestionType.LIKERT_5, category = "Jester", likertLabels = "Rarely" to "Often"),
    TestQuestion(15, "Do you feel responsible for making the world better?", QuestionType.YES_NO_SOMETIMES, category = "Hero"),
    TestQuestion(16, "Do you value knowledge above material wealth?", QuestionType.YES_NO_SOMETIMES, category = "Sage")
)

// Continuing with remaining question generators...
private fun generateEnergyBalanceQuestions() = generateGenericLikertQuestions(20, "energy balance")
private fun generateCharismaQuestions() = generateGenericLikertQuestions(15, "charisma")
private fun generateSocialStyleQuestions() = generateGenericLikertQuestions(18, "social style")
private fun generateEmpathyQuestions() = generateGenericLikertQuestions(16, "empathy")
private fun generateEQQuestions() = generateGenericLikertQuestions(20, "emotional intelligence")
private fun generateAngerQuestions() = generateGenericLikertQuestions(18, "anger patterns")
private fun generateMoodSwingsQuestions() = generateGenericLikertQuestions(16, "mood variability")
private fun generateOverwhelmQuestions() = generateGenericLikertQuestions(15, "overwhelm")
private fun generateBurnoutQuestions() = generateGenericLikertQuestions(18, "burnout")
private fun generateLowMoodQuestions() = generateGenericLikertQuestions(15, "mood")
private fun generateDepressionQuestions() = generateGenericLikertQuestions(20, "depression screening")
private fun generateHappinessQuestions() = generateGenericLikertQuestions(18, "happiness")
private fun generateLonelinessQuestions() = generateGenericLikertQuestions(16, "loneliness")
private fun generateChildhoodQuestions() = generateGenericLikertQuestions(20, "childhood")
private fun generateInnerChildQuestions() = generateGenericLikertQuestions(18, "inner child")
private fun generateWoundedChildQuestions() = generateGenericLikertQuestions(20, "childhood wounds")
private fun generateFatherImpactQuestions() = generateGenericLikertQuestions(18, "father relationship")
private fun generateMotherImpactQuestions() = generateGenericLikertQuestions(18, "mother relationship")
private fun generateAttachmentQuestions() = generateGenericLikertQuestions(20, "attachment")
private fun generateLoveLanguageQuestions() = generateGenericLikertQuestions(18, "love language")
private fun generatePastRelationshipQuestions() = generateGenericLikertQuestions(18, "past relationships")
private fun generateToxicPartnerQuestions() = generateGenericLikertQuestions(20, "relationship patterns")
private fun generateGaslightingQuestions() = generateGenericLikertQuestions(18, "gaslighting")
private fun generateSiblingQuestions() = generateGenericLikertQuestions(16, "sibling dynamics")
private fun generateTraumaQuestions() = generateGenericLikertQuestions(20, "trauma")
private fun generateImposterQuestions() = generateGenericLikertQuestions(16, "imposter syndrome")
private fun generatePeoplePleasingQuestions() = generateGenericLikertQuestions(18, "people pleasing")
private fun generateBodyImageQuestions() = generateGenericLikertQuestions(18, "body image")
private fun generateFocusQuestions() = generateGenericLikertQuestions(16, "focus")
private fun generateManipulationQuestions() = generateGenericLikertQuestions(18, "manipulation")
private fun generateNarcissisticTraitsQuestions() = generateGenericLikertQuestions(20, "narcissistic traits")
private fun generateNarcissisticTypeQuestions() = generateGenericLikertQuestions(16, "narcissistic patterns")
private fun generateNarcissisticImpactQuestions() = generateGenericLikertQuestions(20, "narcissistic impact")
private fun generateToxicTraitsQuestions() = generateGenericLikertQuestions(20, "toxic patterns")
private fun generateCareerQuestions() = generateGenericLikertQuestions(25, "career")

private fun generateGenericLikertQuestions(count: Int, topic: String): List<TestQuestion> {
    val questionTemplates = listOf(
        "How often do you experience this in your daily life?",
        "To what extent does this describe you?",
        "How strongly do you relate to this statement?",
        "How frequently does this occur for you?",
        "How comfortable are you with this?",
        "How important is this to you?",
        "How much does this affect your wellbeing?",
        "How often do you notice this pattern?",
        "To what degree does this resonate with you?",
        "How significant is this in your life?"
    )
    
    return (1..count).map { index ->
        TestQuestion(
            id = index,
            text = questionTemplates[(index - 1) % questionTemplates.size],
            type = if (index % 3 == 0) QuestionType.YES_NO_SOMETIMES else QuestionType.LIKERT_5,
            likertLabels = "Not at all" to "Very much"
        )
    }
}
