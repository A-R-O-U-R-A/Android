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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.aroura.ui.components.ArouraBackground
import com.example.aroura.ui.theme.*

/**
 * Privacy Screen - Premium Redesign
 * 
 * Features:
 * - Animated toggle switches
 * - Premium action buttons
 * - Smooth entrance animations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyScreen(onBack: () -> Unit) {
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
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = ArouraSpacing.md.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = OffWhite)
                }
                
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(400)) + slideInHorizontally(
                        initialOffsetX = { -20 },
                        animationSpec = tween(400)
                    )
                ) {
                    Text(
                        "Privacy & Data",
                        style = MaterialTheme.typography.titleLarge,
                        color = OffWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            LazyColumn(
                contentPadding = PaddingValues(bottom = ArouraSpacing.xxl.dp),
                verticalArrangement = Arrangement.spacedBy(ArouraSpacing.md.dp)
            ) {
                // Privacy Toggles
                item {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(400, delayMillis = 100)) + slideInVertically(
                            initialOffsetY = { 20 },
                            animationSpec = tween(400, delayMillis = 100)
                        )
                    ) {
                        PremiumPrivacyToggle(
                            "Share Usage Data",
                            "Help us improve A.R.O.U.R.A",
                            true
                        )
                    }
                }
                
                item {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(400, delayMillis = 150)) + slideInVertically(
                            initialOffsetY = { 20 },
                            animationSpec = tween(400, delayMillis = 150)
                        )
                    ) {
                        PremiumPrivacyToggle(
                            "Personalized Suggestions",
                            "Tailor content to your mood",
                            true
                        )
                    }
                }
                
                item {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(400, delayMillis = 200)) + slideInVertically(
                            initialOffsetY = { 20 },
                            animationSpec = tween(400, delayMillis = 200)
                        )
                    ) {
                        PremiumPrivacyToggle(
                            "Allow Analytics",
                            "Anonymous performance tracking",
                            false
                        )
                    }
                }
                
                item { Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp)) }
                
                // Export Button
                item {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(400, delayMillis = 300)) + slideInVertically(
                            initialOffsetY = { 20 },
                            animationSpec = tween(400, delayMillis = 300)
                        )
                    ) {
                        PremiumActionButton(
                            text = "Export My Data",
                            icon = Icons.Default.Share,
                            accentColor = MutedTeal,
                            onClick = { /* Export */ }
                        )
                    }
                }
                
                // Delete Account Button
                item {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(400, delayMillis = 350)) + slideInVertically(
                            initialOffsetY = { 20 },
                            animationSpec = tween(400, delayMillis = 350)
                        )
                    ) {
                        PremiumActionButton(
                            text = "Delete Account",
                            icon = Icons.Default.Delete,
                            accentColor = GentleError,
                            onClick = { /* Delete */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PremiumPrivacyToggle(title: String, subtitle: String, initial: Boolean) {
    var checked by remember { mutableStateOf(initial) }
    
    val backgroundColor by animateColorAsState(
        targetValue = if (checked) MutedTeal.copy(alpha = 0.08f) else Color.Transparent,
        animationSpec = tween(200),
        label = "toggleBg"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(ArouraSpacing.cardRadius.dp))
            .border(
                1.dp,
                Color.White.copy(alpha = 0.05f),
                RoundedCornerShape(ArouraSpacing.cardRadius.dp)
            )
            .padding(ArouraSpacing.lg.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                color = OffWhite,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextDarkSecondary
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = { checked = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MutedTeal,
                checkedTrackColor = MutedTeal.copy(alpha = 0.3f),
                uncheckedThumbColor = TextDarkSecondary,
                uncheckedTrackColor = DeepSurface
            )
        )
    }
}

@Composable
private fun PremiumActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "actionScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale)
            .background(
                accentColor.copy(alpha = 0.1f),
                RoundedCornerShape(ArouraSpacing.cardRadius.dp)
            )
            .border(
                1.dp,
                accentColor.copy(alpha = 0.3f),
                RoundedCornerShape(ArouraSpacing.cardRadius.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                icon,
                null,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(ArouraSpacing.sm.dp))
            Text(
                text,
                color = accentColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// Legacy compatibility
@Composable
fun PrivacyToggle(title: String, subtitle: String, initial: Boolean) = 
    PremiumPrivacyToggle(title, subtitle, initial)
