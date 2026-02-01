package com.example.aroura.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ChatSelectionScreen(onChatSelected: (String) -> Unit, onProfileClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Header with Profile
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                onClick = onProfileClick,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                 Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(DeepSurface, CircleShape)
                        .border(1.dp, MutedTeal.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, "Profile", tint = OffWhite, modifier = Modifier.size(16.dp))
                }
            }
        }

        Text(
            text = "Who do you feel like talking to?",
            style = MaterialTheme.typography.headlineSmall,
            color = OffWhite,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Light
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Cards Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Counselor Card
            ChatOptionCard(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(0.7f),
                title = "AI Mental Health\nCOUNSELOR",
                description = "Your gentle guide,\nhere to support you.",
                buttonText = "Talk to Counselor",
                onClick = { onChatSelected("Counselor") }
            ) {
                // Moon Visual
                MoonVisual()
            }

            // Companion Card
            ChatOptionCard(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(0.7f),
                title = "AI Companion\nBUDDY / BEST FRIEND",
                description = "Your friendly AI buddy,\nhere to chat and listen.",
                buttonText = "Talk to Companion",
                onClick = { onChatSelected("Companion") }
            ) {
                // Star Visual
                StarVisual()
            }
        }
        
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun ChatOptionCard(
    modifier: Modifier,
    title: String,
    description: String,
    buttonText: String,
    onClick: () -> Unit,
    visualContent: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DeepSurface.copy(alpha = 0.4f),
                        DeepSurface.copy(alpha = 0.6f)
                    )
                ),
                shape = RoundedCornerShape(32.dp)
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.1f),
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(32.dp)
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = OffWhite,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold
            )

            // Visual
            Box(
                modifier = Modifier.size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                visualContent()
            }

            // Description
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TextDarkSecondary,
                textAlign = TextAlign.Center,
                fontSize = 12.sp
            )

            // Button
            Button(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.15f),
                    contentColor = OffWhite
                ),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = buttonText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun MoonVisual() {
    Canvas(modifier = Modifier.size(100.dp)) {
        // Outer Glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF90CAF9).copy(alpha = 0.3f),
                    Color.Transparent
                )
            ),
            radius = size.width / 1.5f
        )
        // Moon Body
        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFE3F2FD),
                    Color(0xFF42A5F5)
                ),
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height)
            ),
            radius = size.width / 3f
        )
        // Sparkles
        drawCircle(color = Color.White, radius = 4f, center = Offset(10f, 20f))
        drawCircle(color = Color.White, radius = 3f, center = Offset(90f, 80f))
    }
}

@Composable
fun StarVisual() {
    Canvas(modifier = Modifier.size(100.dp)) {
        // Glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFE082).copy(alpha = 0.3f),
                    Color.Transparent
                )
            ),
            radius = size.width / 1.5f
        )

        val cx = size.width / 2
        val cy = size.height / 2
        val outerRadius = size.width / 2.5f
        val innerRadius = outerRadius / 2f
        
        val starPath = Path().apply {
            var angle = -Math.PI / 2 // Start at top
            val step = Math.PI / 5 // 5 points
            
            moveTo(
                (cx + outerRadius * cos(angle)).toFloat(),
                (cy + outerRadius * sin(angle)).toFloat()
            )
            
            for (i in 1..5) {
                angle += step
                lineTo(
                    (cx + innerRadius * cos(angle)).toFloat(),
                    (cy + innerRadius * sin(angle)).toFloat()
                )
                angle += step
                lineTo(
                    (cx + outerRadius * cos(angle)).toFloat(),
                    (cy + outerRadius * sin(angle)).toFloat()
                )
            }
            close()
        }

        // Draw Soft Rounded Star (simulated by using CornerPathEffect if possible, but standard draw is fine)
        drawPath(
            path = starPath,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFFFF59D),
                    Color(0xFFFFB74D)
                ),
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height)
            )
        )

        // Cute Face
        // Eyes
        drawCircle(Color.Black.copy(alpha = 0.7f), radius = 4f, center = Offset(cx - 12f, cy - 5f))
        drawCircle(Color.Black.copy(alpha = 0.7f), radius = 4f, center = Offset(cx + 12f, cy - 5f))
        // Mouth
        drawArc(
            color = Color.Black.copy(alpha = 0.7f),
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(cx - 8f, cy - 2f),
            size = androidx.compose.ui.geometry.Size(16f, 10f),
            style = Stroke(width = 3f)
        )
    }
}