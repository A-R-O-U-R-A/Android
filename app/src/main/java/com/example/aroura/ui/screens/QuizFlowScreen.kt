package com.example.aroura.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.data.QuizQuestion
import com.example.aroura.data.QuizResultCategory
import com.example.aroura.data.UpliftingQuiz
import com.example.aroura.data.UpliftingQuizRepository
import com.example.aroura.ui.theme.*
import android.content.Intent
import kotlinx.coroutines.delay

/**
 * Quiz Flow Screen
 * 
 * A beautiful, animated quiz experience that:
 * - Shows one question at a time with smooth transitions
 * - Tracks progress with an elegant progress bar
 * - Displays an encouraging result with share functionality
 * - Saves results to the database
 */
@Composable
fun QuizFlowScreen(
    quizId: String,
    onClose: () -> Unit,
    onSaveResult: (quizId: String, quizTitle: String, resultMessage: String, score: Int) -> Unit = { _, _, _, _ -> }
) {
    val quiz = remember { UpliftingQuizRepository.getQuizById(quizId) }
    
    if (quiz == null) {
        // Quiz not found - show error and close
        LaunchedEffect(Unit) {
            onClose()
        }
        return
    }
    
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var answers by remember { mutableStateOf(mapOf<Int, String>()) }
    var showResult by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<QuizResultCategory?>(null) }
    
    // Animation states
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A2F35),
            Color(0xFF2A4A4F),
            Color(0xFF3D5A5A)
        )
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        if (showResult && result != null) {
            QuizResultScreen(
                quiz = quiz,
                result = result!!,
                onClose = onClose,
                onShare = {
                    // Share result
                },
                onSave = {
                    onSaveResult(
                        quiz.id,
                        quiz.title,
                        result!!.title, // Use result title as the result message
                        answers.size
                    )
                }
            )
        } else {
            QuizQuestionScreen(
                quiz = quiz,
                currentQuestionIndex = currentQuestionIndex,
                totalQuestions = quiz.questions.size,
                answeredCount = answers.size,
                onAnswerSelected = { questionId, categoryId ->
                    answers = answers + (questionId to categoryId)
                    
                    if (currentQuestionIndex < quiz.questions.size - 1) {
                        currentQuestionIndex++
                    } else {
                        // Calculate and show result
                        result = UpliftingQuizRepository.calculateResult(quiz, answers)
                        showResult = true
                    }
                },
                onBack = {
                    if (currentQuestionIndex > 0) {
                        currentQuestionIndex--
                    } else {
                        onClose()
                    }
                },
                onClose = onClose
            )
        }
    }
}

@Composable
private fun QuizQuestionScreen(
    quiz: UpliftingQuiz,
    currentQuestionIndex: Int,
    totalQuestions: Int,
    answeredCount: Int,
    onAnswerSelected: (Int, String) -> Unit,
    onBack: () -> Unit,
    onClose: () -> Unit
) {
    val question = quiz.questions[currentQuestionIndex]
    val progress = (currentQuestionIndex.toFloat() / totalQuestions.toFloat())
    
    // Animation for question transition
    var questionVisible by remember { mutableStateOf(false) }
    LaunchedEffect(currentQuestionIndex) {
        questionVisible = false
        delay(50)
        questionVisible = true
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(ArouraSpacing.screenHorizontal.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        // Header with back button and progress
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.2f))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = OffWhite
                )
            }
            
            // Progress indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${currentQuestionIndex + 1} of $totalQuestions",
                    style = MaterialTheme.typography.labelMedium,
                    color = OffWhite.copy(alpha = 0.7f)
                )
            }
            
            // Close button
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.2f))
            ) {
                Text(
                    text = "✕",
                    color = OffWhite,
                    fontWeight = FontWeight.Light,
                    fontSize = 18.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White.copy(alpha = 0.1f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animateFloatAsState(
                        targetValue = progress,
                        animationSpec = tween(300),
                        label = "progress"
                    ).value)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(quiz.accentColor),
                                Color(quiz.accentColor).copy(alpha = 0.7f)
                            )
                        )
                    )
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Quiz title and emoji
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = quiz.emoji, fontSize = 24.sp)
            Text(
                text = quiz.title,
                style = MaterialTheme.typography.titleMedium,
                color = OffWhite.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )
        }
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // Question with animation
        AnimatedVisibility(
            visible = questionVisible,
            enter = fadeIn(tween(300)) + slideInHorizontally(
                initialOffsetX = { 100 },
                animationSpec = tween(300)
            ),
            exit = fadeOut(tween(150)) + slideOutHorizontally(
                targetOffsetX = { -100 },
                animationSpec = tween(150)
            )
        ) {
            Column {
                Text(
                    text = question.text,
                    style = MaterialTheme.typography.headlineSmall,
                    color = OffWhite,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 32.sp
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Answer options
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    question.options.forEach { option ->
                        AnswerOption(
                            text = option.text,
                            accentColor = Color(quiz.accentColor),
                            onClick = {
                                onAnswerSelected(question.id, option.category)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnswerOption(
    text: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "optionScale"
    )
    
    val bgAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.15f else 0.08f,
        animationSpec = tween(100),
        label = "bgAlpha"
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(accentColor.copy(alpha = bgAlpha))
            .border(
                1.dp,
                accentColor.copy(alpha = if (isPressed) 0.4f else 0.15f),
                RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(20.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = OffWhite,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
private fun QuizResultScreen(
    quiz: UpliftingQuiz,
    result: QuizResultCategory,
    onClose: () -> Unit,
    onShare: () -> Unit,
    onSave: () -> Unit
) {
    val context = LocalContext.current
    var visible by remember { mutableStateOf(false) }
    var saved by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        visible = true
        // Auto-save result
        onSave()
    }
    
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(ArouraSpacing.screenHorizontal.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        // Close button (top right)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.2f))
            ) {
                Text(
                    text = "✕",
                    color = OffWhite,
                    fontWeight = FontWeight.Light,
                    fontSize = 18.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Result emoji with glow effect
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(500)) + scaleIn(
                initialScale = 0.5f,
                animationSpec = tween(500, easing = EaseOutBack)
            )
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(120.dp)
            ) {
                // Glow background
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(result.accentColor).copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                Text(
                    text = result.emoji,
                    fontSize = 64.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Result title
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(500, delayMillis = 200)) + slideInVertically(
                initialOffsetY = { 30 },
                animationSpec = tween(500, delayMillis = 200)
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "You are",
                    style = MaterialTheme.typography.titleMedium,
                    color = OffWhite.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Light
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = result.title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = OffWhite,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Description
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(500, delayMillis = 400))
        ) {
            Text(
                text = result.description,
                style = MaterialTheme.typography.bodyLarge,
                color = OffWhite.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                lineHeight = 26.sp
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Strengths
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(500, delayMillis = 600))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(result.accentColor).copy(alpha = 0.1f))
                    .padding(20.dp)
            ) {
                Text(
                    text = "Your Strengths",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(result.accentColor),
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                result.strengths.forEach { strength ->
                    Row(
                        modifier = Modifier.padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(result.accentColor),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = strength,
                            style = MaterialTheme.typography.bodyMedium,
                            color = OffWhite.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Affirmation
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(500, delayMillis = 800))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(result.accentColor).copy(alpha = 0.15f),
                                Color(result.accentColor).copy(alpha = 0.08f)
                            )
                        )
                    )
                    .border(
                        1.dp,
                        Color(result.accentColor).copy(alpha = 0.2f),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "✨ Remember",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(result.accentColor),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"${result.affirmation}\"",
                        style = MaterialTheme.typography.bodyLarge,
                        color = OffWhite,
                        fontWeight = FontWeight.Light,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        lineHeight = 26.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Action buttons
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(500, delayMillis = 1000)) + slideInVertically(
                initialOffsetY = { 30 },
                animationSpec = tween(500, delayMillis = 1000)
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Share button
                Button(
                    onClick = {
                        val shareText = "I took the '${quiz.title}' quiz and I'm ${result.title}! ${result.emoji}\n\n${result.affirmation}\n\nTake the quiz in A.R.O.U.R.A app!"
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share your result"))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(result.accentColor).copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = Color(result.accentColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Share",
                        color = Color(result.accentColor),
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // Done button
                Button(
                    onClick = onClose,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(result.accentColor)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Done",
                        color = MidnightCharcoal,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}
