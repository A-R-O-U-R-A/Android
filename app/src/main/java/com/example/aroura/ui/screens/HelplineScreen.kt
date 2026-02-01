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
import androidx.compose.material.icons.filled.Phone
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
 * Helpline Screen - Premium Redesign
 * 
 * Features:
 * - Staggered entrance animations
 * - Premium cards with call action
 * - Interactive feedback
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelplineScreen(onBack: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    
    val helplines = listOf(
        Pair("Vandrevala Foundation", "1860-266-2345"),
        Pair("iCall (Tata Institute)", "022-25521111"),
        Pair("Kiran Mental Health", "1800-599-0019"),
        Pair("Samaritans Mumbai", "+91 84229 84528"),
        Pair("Sneha Foundation", "044-24640050")
    )

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
                        "Helplines",
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
                    "Reach out — you're not alone.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDarkSecondary,
                    modifier = Modifier.padding(start = ArouraSpacing.sm.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(ArouraSpacing.md.dp),
                contentPadding = PaddingValues(bottom = ArouraSpacing.xxl.dp)
            ) {
                itemsIndexed(helplines) { index, (name, number) ->
                    val delay = 150 + (index * 60)
                    
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(400, delayMillis = delay)) + slideInVertically(
                            initialOffsetY = { 30 },
                            animationSpec = tween(400, delayMillis = delay, easing = EaseOutCubic)
                        )
                    ) {
                        PremiumHelplineCard(name = name, number = number)
                    }
                }
            }
        }
    }
}

@Composable
private fun PremiumHelplineCard(name: String, number: String) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "helplineScale"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = DeepSurface.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(ArouraSpacing.cardRadius.dp),
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .border(
                1.dp,
                SoftBlue.copy(alpha = 0.15f),
                RoundedCornerShape(ArouraSpacing.cardRadius.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { /* Call */ }
    ) {
        Row(
            modifier = Modifier.padding(ArouraSpacing.lg.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    name,
                    style = MaterialTheme.typography.titleMedium,
                    color = OffWhite,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    number,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftBlue
                )
            }
            
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(SoftBlue.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Phone,
                    null,
                    tint = SoftBlue,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
