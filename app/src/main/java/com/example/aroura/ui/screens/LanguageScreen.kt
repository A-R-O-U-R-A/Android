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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.aroura.ui.components.ArouraBackground
import com.example.aroura.ui.theme.*

/**
 * Language Screen - Premium Redesign
 * 
 * Features:
 * - Animated selection
 * - Premium language cards
 * - Smooth transitions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScreen(onBack: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    val languages = listOf("English", "Hindi", "Spanish", "French", "German", "Chinese", "Japanese")
    var selectedLanguage by remember { mutableStateOf("English") }
    
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
                        "Language",
                        style = MaterialTheme.typography.titleLarge,
                        color = OffWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            // Subtitle
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(400, delayMillis = 100))
            ) {
                Text(
                    "Choose your preferred language",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDarkSecondary,
                    modifier = Modifier.padding(start = ArouraSpacing.sm.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(ArouraSpacing.sm.dp)
            ) {
                itemsIndexed(languages) { index, language ->
                    val delay = 150 + (index * 50)
                    
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(400, delayMillis = delay)) + slideInHorizontally(
                            initialOffsetX = { 30 },
                            animationSpec = tween(400, delayMillis = delay, easing = EaseOutCubic)
                        )
                    ) {
                        PremiumLanguageItem(
                            language = language,
                            isSelected = selectedLanguage == language,
                            onClick = { selectedLanguage = language }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PremiumLanguageItem(
    language: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "langScale"
    )
    
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MutedTeal.copy(alpha = 0.12f) else Color.Transparent,
        animationSpec = tween(200),
        label = "langBg"
    )
    
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MutedTeal.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.05f),
        animationSpec = tween(200),
        label = "langBorder"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .scale(scale)
            .background(backgroundColor, RoundedCornerShape(ArouraSpacing.cardRadius.dp))
            .border(1.dp, borderColor, RoundedCornerShape(ArouraSpacing.cardRadius.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .padding(horizontal = ArouraSpacing.lg.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            language,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) OffWhite else TextDarkSecondary,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
        
        AnimatedVisibility(
            visible = isSelected,
            enter = scaleIn(animationSpec = spring(stiffness = Spring.StiffnessMedium)) + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(MutedTeal.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Check,
                    null,
                    tint = MutedTeal,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
