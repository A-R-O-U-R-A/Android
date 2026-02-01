package com.example.aroura.ui.components.reflect

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.data.ReflectTest
import com.example.aroura.data.TestResult
import com.example.aroura.ui.theme.*

/**
 * Result Visualization Charts - Premium Composables
 * 
 * Beautiful, calm visualizations for test results:
 * - Pie Charts
 * - Radial/Spider Charts  
 * - Segmented Scales
 * - Gauge Meters
 * - Bar Charts
 */

// ═══════════════════════════════════════════════════════════════════════════════
// TEST RESULT SUMMARY SCREEN
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun TestResultScreen(
    test: ReflectTest,
    result: TestResult,
    onRetake: () -> Unit,
    onBack: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    
    val accentColor = Color(test.accentColorHex)
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(horizontal = ArouraSpacing.screenHorizontal.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            ReflectTopBar(title = "", onBack = onBack)
            
            Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
        }
        
        // Result Header
        item {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500)) + scaleIn(
                    initialScale = 0.9f,
                    animationSpec = tween(500, easing = EaseOutCubic)
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Your Result",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextDarkSecondary,
                        letterSpacing = 2.sp
                    )
                    
                    Spacer(modifier = Modifier.height(ArouraSpacing.sm.dp))
                    
                    Text(
                        text = result.primaryLabel,
                        style = MaterialTheme.typography.headlineMedium,
                        color = accentColor,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
        }
        
        // Main Visualization
        item {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600, delayMillis = 200)) + scaleIn(
                    initialScale = 0.8f,
                    animationSpec = tween(600, delayMillis = 200, easing = EaseOutCubic)
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .padding(ArouraSpacing.lg.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (result.categories.isNotEmpty()) {
                        // Show radial chart for multi-category results
                        RadialChart(
                            categories = result.categories,
                            accentColor = accentColor
                        )
                    } else {
                        // Show gauge for single-value results
                        GaugeMeter(
                            value = result.primaryScore,
                            label = result.primaryLabel,
                            accentColor = accentColor
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
        }
        
        // Category breakdown (if present)
        if (result.categories.isNotEmpty()) {
            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(500, delayMillis = 400))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                DeepSurface.copy(alpha = 0.5f),
                                RoundedCornerShape(ArouraSpacing.cardRadius.dp)
                            )
                            .padding(ArouraSpacing.lg.dp)
                    ) {
                        Text(
                            "Breakdown",
                            style = MaterialTheme.typography.titleMedium,
                            color = OffWhite,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.height(ArouraSpacing.md.dp))
                        
                        result.categories.forEach { (category, score) ->
                            CategoryBar(
                                category = category,
                                score = score,
                                accentColor = accentColor
                            )
                            Spacer(modifier = Modifier.height(ArouraSpacing.sm.dp))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
            }
        }
        
        // Description
        item {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500, delayMillis = 500))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    accentColor.copy(alpha = 0.1f),
                                    DeepSurface.copy(alpha = 0.5f)
                                )
                            ),
                            shape = RoundedCornerShape(ArouraSpacing.cardRadius.dp)
                        )
                        .border(
                            1.dp,
                            accentColor.copy(alpha = 0.2f),
                            RoundedCornerShape(ArouraSpacing.cardRadius.dp)
                        )
                        .padding(ArouraSpacing.lg.dp)
                ) {
                    Text(
                        text = result.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = OffWhite.copy(alpha = 0.9f),
                        lineHeight = 26.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
        }
        
        // Insights
        if (result.insights.isNotEmpty()) {
            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(500, delayMillis = 600))
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Key Insights",
                            style = MaterialTheme.typography.titleMedium,
                            color = OffWhite,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.height(ArouraSpacing.md.dp))
                        
                        result.insights.forEachIndexed { index, insight ->
                            InsightItem(
                                number = index + 1,
                                text = insight,
                                accentColor = accentColor
                            )
                            Spacer(modifier = Modifier.height(ArouraSpacing.sm.dp))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
            }
        }
        
        // Reflection
        if (result.reflection.isNotEmpty()) {
            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(500, delayMillis = 700))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                CalmingLavender.copy(alpha = 0.1f),
                                RoundedCornerShape(ArouraSpacing.cardRadius.dp)
                            )
                            .padding(ArouraSpacing.lg.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Star,
                                    null,
                                    tint = CalmingLavender,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(ArouraSpacing.sm.dp))
                                Text(
                                    "Reflection",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = CalmingLavender,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(ArouraSpacing.md.dp))
                            
                            Text(
                                text = result.reflection,
                                style = MaterialTheme.typography.bodyMedium,
                                color = OffWhite.copy(alpha = 0.85f),
                                lineHeight = 24.sp,
                                fontWeight = FontWeight.Light
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
            }
        }
        
        // Action Buttons
        item {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500, delayMillis = 800)) + slideInVertically(
                    initialOffsetY = { 30 },
                    animationSpec = tween(500, delayMillis = 800, easing = EaseOutCubic)
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Safety reminder for sensitive tests
                    test.safetyNote?.let {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Color(0xFF4A3728).copy(alpha = 0.3f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(ArouraSpacing.md.dp)
                        ) {
                            Text(
                                text = "Remember: This assessment is for self-reflection, not diagnosis. If you need support, please reach out to a mental health professional.",
                                style = MaterialTheme.typography.bodySmall,
                                color = CalmingPeach.copy(alpha = 0.9f),
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(ArouraSpacing.md.dp)
                    ) {
                        ResultActionButton(
                            text = "Retake Test",
                            isPrimary = false,
                            accentColor = accentColor,
                            onClick = onRetake,
                            modifier = Modifier.weight(1f)
                        )
                        
                        ResultActionButton(
                            text = "Done",
                            isPrimary = true,
                            accentColor = accentColor,
                            onClick = onBack,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// GAUGE METER - For single-value results
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun GaugeMeter(
    value: Float,
    label: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    var animatedValue by remember { mutableFloatStateOf(0f) }
    
    LaunchedEffect(value) {
        animate(
            initialValue = 0f,
            targetValue = value,
            animationSpec = tween(1500, easing = EaseOutCubic)
        ) { currentValue, _ ->
            animatedValue = currentValue
        }
    }
    
    Box(
        modifier = modifier.size(220.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 16.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val startAngle = 135f
            val sweepAngle = 270f
            
            // Background arc
            drawArc(
                color = DeepSurface,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(
                    (size.width - radius * 2) / 2,
                    (size.height - radius * 2) / 2
                ),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            
            // Value arc
            val valueSweep = (animatedValue / 100f) * sweepAngle
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.6f),
                        accentColor
                    )
                ),
                startAngle = startAngle,
                sweepAngle = valueSweep,
                useCenter = false,
                topLeft = Offset(
                    (size.width - radius * 2) / 2,
                    (size.height - radius * 2) / 2
                ),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        
        // Center content
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${animatedValue.toInt()}",
                style = MaterialTheme.typography.displayMedium,
                color = OffWhite,
                fontWeight = FontWeight.Light
            )
            Text(
                text = "out of 100",
                style = MaterialTheme.typography.labelSmall,
                color = TextDarkSecondary
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// RADIAL/SPIDER CHART - For multi-category results
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun RadialChart(
    categories: Map<String, Float>,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    var animationProgress by remember { mutableFloatStateOf(0f) }
    
    LaunchedEffect(Unit) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(1200, easing = EaseOutCubic)
        ) { value, _ ->
            animationProgress = value
        }
    }
    
    val categoryList = categories.entries.toList()
    val segmentAngle = 360f / categoryList.size
    
    Box(
        modifier = modifier.size(220.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val maxRadius = size.minDimension / 2 - 30.dp.toPx()
            
            // Draw background circles
            listOf(0.25f, 0.5f, 0.75f, 1f).forEach { ratio ->
                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    radius = maxRadius * ratio,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
            }
            
            // Draw category segments
            categoryList.forEachIndexed { index, (_, score) ->
                val angle = -90f + (index * segmentAngle)
                val normalizedScore = (score / 100f) * animationProgress
                val radius = maxRadius * normalizedScore
                
                val angleRad = Math.toRadians(angle.toDouble())
                val endPoint = Offset(
                    center.x + (radius * kotlin.math.cos(angleRad)).toFloat(),
                    center.y + (radius * kotlin.math.sin(angleRad)).toFloat()
                )
                
                // Draw line
                drawLine(
                    color = accentColor.copy(alpha = 0.6f),
                    start = center,
                    end = endPoint,
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
                
                // Draw point
                drawCircle(
                    color = accentColor,
                    radius = 6.dp.toPx(),
                    center = endPoint
                )
            }
        }
        
        // Center label
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${categoryList.size}",
                style = MaterialTheme.typography.headlineLarge,
                color = OffWhite,
                fontWeight = FontWeight.Light
            )
            Text(
                text = "dimensions",
                style = MaterialTheme.typography.labelSmall,
                color = TextDarkSecondary
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// PIE CHART - For distribution visualization
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun PieChart(
    segments: Map<String, Float>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    var animationProgress by remember { mutableFloatStateOf(0f) }
    
    LaunchedEffect(Unit) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(1000, easing = EaseOutCubic)
        ) { value, _ ->
            animationProgress = value
        }
    }
    
    val total = segments.values.sum()
    val segmentList = segments.entries.toList()
    
    Box(
        modifier = modifier.size(180.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 24.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            var startAngle = -90f
            
            segmentList.forEachIndexed { index, (_, value) ->
                val sweepAngle = (value / total) * 360f * animationProgress
                
                drawArc(
                    color = colors.getOrElse(index) { MutedTeal },
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(
                        (size.width - radius * 2) / 2,
                        (size.height - radius * 2) / 2
                    ),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                )
                
                startAngle += sweepAngle
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// SEGMENTED SCALE - For spectrum positioning
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun SegmentedScale(
    position: Float, // 0-100
    leftLabel: String,
    rightLabel: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    var animatedPosition by remember { mutableFloatStateOf(0f) }
    
    LaunchedEffect(position) {
        animate(
            initialValue = 0f,
            targetValue = position,
            animationSpec = tween(1000, easing = EaseOutCubic)
        ) { value, _ ->
            animatedPosition = value
        }
    }
    
    Column(modifier = modifier.fillMaxWidth()) {
        // Labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(leftLabel, style = MaterialTheme.typography.labelSmall, color = TextDarkSecondary)
            Text(rightLabel, style = MaterialTheme.typography.labelSmall, color = TextDarkSecondary)
        }
        
        Spacer(modifier = Modifier.height(ArouraSpacing.sm.dp))
        
        // Scale Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
        ) {
            // Background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(6.dp))
                    .background(DeepSurface)
            )
            
            // Indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedPosition / 100f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                accentColor.copy(alpha = 0.5f),
                                accentColor
                            )
                        )
                    )
            )
            
            // Position marker
            Box(
                modifier = Modifier
                    .offset(x = ((animatedPosition / 100f) * 300).dp - 8.dp) // Approximate positioning
                    .size(16.dp)
                    .align(Alignment.CenterStart)
                    .background(OffWhite, CircleShape)
                    .border(2.dp, accentColor, CircleShape)
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// CATEGORY BAR - For breakdown display
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun CategoryBar(
    category: String,
    score: Float,
    accentColor: Color
) {
    var animatedScore by remember { mutableFloatStateOf(0f) }
    
    LaunchedEffect(score) {
        animate(
            initialValue = 0f,
            targetValue = score,
            animationSpec = tween(800, easing = EaseOutCubic)
        ) { value, _ ->
            animatedScore = value
        }
    }
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = category,
                style = MaterialTheme.typography.bodySmall,
                color = OffWhite.copy(alpha = 0.9f)
            )
            Text(
                text = "${animatedScore.toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = accentColor
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(DeepSurface.copy(alpha = 0.5f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedScore / 100f)
                    .fillMaxHeight()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                accentColor.copy(alpha = 0.6f),
                                accentColor
                            )
                        ),
                        shape = RoundedCornerShape(3.dp)
                    )
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// INSIGHT ITEM
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun InsightItem(
    number: Int,
    text: String,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                DeepSurface.copy(alpha = 0.4f),
                RoundedCornerShape(12.dp)
            )
            .padding(ArouraSpacing.md.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(accentColor.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$number",
                style = MaterialTheme.typography.labelSmall,
                color = accentColor,
                fontWeight = FontWeight.Medium
            )
        }
        
        Spacer(modifier = Modifier.width(ArouraSpacing.md.dp))
        
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = OffWhite.copy(alpha = 0.85f),
            lineHeight = 22.sp
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// ACTION BUTTONS
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun ResultActionButton(
    text: String,
    isPrimary: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "btnScale"
    )

    Box(
        modifier = modifier
            .height(52.dp)
            .scale(scale)
            .then(
                if (isPrimary) {
                    Modifier.background(
                        accentColor,
                        RoundedCornerShape(26.dp)
                    )
                } else {
                    Modifier
                        .background(Color.Transparent, RoundedCornerShape(26.dp))
                        .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(26.dp))
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isPrimary) MidnightCharcoal else accentColor,
            fontWeight = FontWeight.Medium
        )
    }
}
