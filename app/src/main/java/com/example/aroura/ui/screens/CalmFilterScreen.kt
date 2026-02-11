package com.example.aroura.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.ui.theme.*

/**
 * Calm Filter Screen - Premium Redesign
 * 
 * Features:
 * - Animated bottom sheet entrance
 * - Premium filter chips with scale feedback
 * - Gradient apply button
 * - Smooth transitions
 */
@Composable
fun CalmFilterScreen(
    onClose: () -> Unit,
    onApply: (String, String) -> Unit
) {
    var selectedReligion by remember { mutableStateOf("All") }
    var selectedLanguage by remember { mutableStateOf("English") }
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) { visible = true }

    val religions = listOf("All", "Hindu", "Islamic", "Christian", "Sikh", "Buddhist")
    val languages = listOf("English", "Hindi", "Sanskrit", "Arabic", "Punjabi")

    // Overlay background with fade
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClose() },
        contentAlignment = Alignment.BottomCenter
    ) {
        // Bottom Sheet Content with slide animation
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(400, easing = EaseOutCubic)
            ) + fadeIn(tween(300)),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(300, easing = EaseInOutSine)
            )
        ) {
            Surface(
                color = DeepSurface,
                shape = RoundedCornerShape(topStart = ArouraSpacing.xl.dp, topEnd = ArouraSpacing.xl.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = false) {}
            ) {
                Column(
                    modifier = Modifier.padding(ArouraSpacing.screenHorizontal.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Spacer(modifier = Modifier.height(ArouraSpacing.md.dp))
                    
                    // Drag Handle
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(40.dp)
                            .height(4.dp)
                            .background(
                                TextDarkSecondary.copy(alpha = 0.4f),
                                RoundedCornerShape(2.dp)
                            )
                    )
                    
                    Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
                    
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Filter Content",
                            style = MaterialTheme.typography.headlineSmall,
                            color = OffWhite,
                            fontWeight = FontWeight.SemiBold
                        )
                        IconButton(onClick = onClose) {
                            Icon(Icons.Default.Close, null, tint = TextDarkSecondary)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))

                    // Religion Filter
                    Text(
                        "Religion / Spirituality",
                        style = MaterialTheme.typography.titleMedium,
                        color = MutedTeal,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(ArouraSpacing.md.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(ArouraSpacing.sm.dp)) {
                        items(religions) { religion ->
                            PremiumFilterChip(
                                label = religion,
                                selected = religion == selectedReligion,
                                onClick = { selectedReligion = religion }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))

                    // Language Filter
                    Text(
                        "Language",
                        style = MaterialTheme.typography.titleMedium,
                        color = MutedTeal,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(ArouraSpacing.md.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(ArouraSpacing.sm.dp)) {
                        items(languages) { language ->
                            PremiumFilterChip(
                                label = language,
                                selected = language == selectedLanguage,
                                onClick = { selectedLanguage = language }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(ArouraSpacing.xxl.dp))

                    // Apply Button
                    PremiumApplyButton(
                        onClick = { onApply(selectedReligion, selectedLanguage) }
                    )
                    
                    Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
                }
            }
        }
    }
}

@Composable
private fun PremiumFilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "chipScale"
    )
    
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) MutedTeal else Color.Transparent,
        animationSpec = tween(200),
        label = "chipBg"
    )
    
    val borderColor by animateColorAsState(
        targetValue = if (selected) MutedTeal else TextDarkSecondary.copy(alpha = 0.5f),
        animationSpec = tween(200),
        label = "chipBorder"
    )

    Surface(
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selected) {
                Icon(
                    Icons.Default.Check,
                    null,
                    modifier = Modifier.size(16.dp),
                    tint = MidnightCharcoal
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = label,
                color = if (selected) MidnightCharcoal else TextDarkSecondary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun PremiumApplyButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "applyScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(MutedTeal, MutedTeal.copy(alpha = 0.8f))
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
            "Apply Filters",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MidnightCharcoal
        )
    }
}
