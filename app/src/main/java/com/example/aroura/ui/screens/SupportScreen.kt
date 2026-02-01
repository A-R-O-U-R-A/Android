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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.ui.components.ArouraProfileIcon
import com.example.aroura.ui.theme.*

/**
 * Support Screen - Premium Redesign
 * 
 * Features:
 * - Staggered entrance animations
 * - Premium crisis card with pulsing animation
 * - Interactive cards with scale feedback
 * - Unified spacing
 */
@Composable
fun SupportScreen(
    onProfileClick: () -> Unit,
    onNavigate: (String) -> Unit,
    onOpenPanic: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = ArouraSpacing.screenHorizontal.dp),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Header
        item {
            Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .systemBarsPadding()
                    .padding(vertical = ArouraSpacing.md.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(400)) + slideInHorizontally(
                        initialOffsetX = { -30 },
                        animationSpec = tween(400)
                    )
                ) {
                    Text(
                        text = "Support",
                        style = MaterialTheme.typography.headlineMedium,
                        color = OffWhite,
                        fontWeight = FontWeight.Light
                    )
                }
                ArouraProfileIcon(onClick = onProfileClick)
            }
            
            Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
        }

        // Panic Guidance
        item {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500, delayMillis = 100)) + scaleIn(
                    initialScale = 0.95f,
                    animationSpec = tween(500, delayMillis = 100, easing = EaseOutCubic)
                )
            ) {
                PremiumCrisisCard(onOpenPanic = onOpenPanic)
            }
            
            Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
        }

        // Helplines
        item {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(400, delayMillis = 200))
            ) {
                Text(
                    "Mental Health Helplines",
                    style = MaterialTheme.typography.titleMedium,
                    color = OffWhite,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(ArouraSpacing.md.dp))
            
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(400, delayMillis = 250)) + slideInVertically(
                    initialOffsetY = { 20 },
                    animationSpec = tween(400, delayMillis = 250)
                )
            ) {
                PremiumSupportOptionCard(
                    "View All Helplines",
                    "Verified Numbers",
                    Icons.Default.Phone,
                    SoftBlue
                ) {
                    onNavigate("helplines")
                }
            }
            
            Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
        }

        // Psychiatrist
        item {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(400, delayMillis = 300))
            ) {
                Text(
                    "Professional Help",
                    style = MaterialTheme.typography.titleMedium,
                    color = OffWhite,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(ArouraSpacing.md.dp))
            
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(400, delayMillis = 350))
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(ArouraSpacing.md.dp)) {
                    PremiumCompactCard("Chat", Icons.Default.Email, MutedTeal, Modifier.weight(1f)) { 
                        onNavigate("psychiatrist") 
                    }
                    PremiumCompactCard("Call", Icons.Default.Call, CalmingPeach, Modifier.weight(1f)) { 
                        onNavigate("psychiatrist") 
                    }
                    PremiumCompactCard("Video", Icons.Default.Face, CalmingLavender, Modifier.weight(1f)) { 
                        onNavigate("psychiatrist") 
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
        }

        // Trusted Contacts
        item {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(400, delayMillis = 400))
            ) {
                Text(
                    "Trusted Contacts",
                    style = MaterialTheme.typography.titleMedium,
                    color = OffWhite,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(ArouraSpacing.md.dp))
            
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(400, delayMillis = 450)) + slideInVertically(
                    initialOffsetY = { 20 },
                    animationSpec = tween(400, delayMillis = 450)
                )
            ) {
                PremiumAddContactCard { onNavigate("trusted") }
            }
            
            Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
        }
        
        // Emergency Resources
        item {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(400, delayMillis = 500))
            ) {
                Text(
                    "Emergency Resources",
                    style = MaterialTheme.typography.titleMedium,
                    color = OffWhite,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(ArouraSpacing.md.dp))
            
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(400, delayMillis = 550)) + slideInVertically(
                    initialOffsetY = { 20 },
                    animationSpec = tween(400, delayMillis = 550)
                )
            ) {
                PremiumSupportOptionCard(
                    "Hospitals & Police",
                    "Near you",
                    Icons.Default.LocationOn,
                    GentleError
                ) {
                    onNavigate("emergency")
                }
            }
        }
    }
}

@Composable
private fun PremiumCrisisCard(onOpenPanic: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Pulsing glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "crisisGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "crisisScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(ArouraSpacing.cardRadius.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFD84315).copy(alpha = glowAlpha),
                        Color(0xFFFF8A65).copy(alpha = glowAlpha)
                    )
                )
            )
            .border(
                1.dp,
                Color.White.copy(alpha = 0.15f),
                RoundedCornerShape(ArouraSpacing.cardRadius.dp)
            )
            .padding(ArouraSpacing.lg.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Warning, null, tint = Color.White, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(ArouraSpacing.md.dp))
                Text(
                    "Crisis Guidance",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
            
            Text(
                "1. Breathe in for 4 seconds.\n2. Hold for 7 seconds.\n3. Exhale for 8 seconds.",
                color = Color.White.copy(alpha = 0.95f),
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 26.sp
            )
            
            Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .background(Color.White, RoundedCornerShape(26.dp))
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { onOpenPanic() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Activate Panic Mode",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD84315)
                )
            }
        }
    }
}

@Composable
private fun PremiumSupportOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
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

    Surface(
        color = DeepSurface.copy(alpha = 0.5f),
        shape = RoundedCornerShape(ArouraSpacing.cardRadius.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .scale(scale)
            .border(
                1.dp,
                accentColor.copy(alpha = 0.2f),
                RoundedCornerShape(ArouraSpacing.cardRadius.dp)
            ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = ArouraSpacing.lg.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(accentColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = accentColor, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(ArouraSpacing.md.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    color = OffWhite,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    subtitle,
                    color = TextDarkSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                null,
                tint = accentColor.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun PremiumCompactCard(
    text: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "compactScale"
    )

    Surface(
        color = DeepSurface.copy(alpha = 0.5f),
        shape = RoundedCornerShape(ArouraSpacing.cardRadius.dp),
        modifier = modifier
            .height(90.dp)
            .scale(scale)
            .border(
                1.dp,
                accentColor.copy(alpha = 0.15f),
                RoundedCornerShape(ArouraSpacing.cardRadius.dp)
            ),
        onClick = onClick
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(accentColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = accentColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(ArouraSpacing.sm.dp))
            Text(
                text,
                color = OffWhite,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun PremiumAddContactCard(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "addScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .scale(scale)
            .clip(RoundedCornerShape(30.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MutedTeal.copy(alpha = 0.15f),
                        MutedTeal.copy(alpha = 0.08f)
                    )
                )
            )
            .border(
                1.dp,
                MutedTeal.copy(alpha = 0.3f),
                RoundedCornerShape(30.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .padding(horizontal = ArouraSpacing.lg.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Add, null, tint = MutedTeal)
            Spacer(modifier = Modifier.width(ArouraSpacing.md.dp))
            Text(
                "Add / Manage Trusted Contacts",
                color = MutedTeal,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// Legacy compatibility
@Composable
fun SupportOptionCard(title: String, subtitle: String, icon: ImageVector, onClick: () -> Unit) = 
    PremiumSupportOptionCard(title, subtitle, icon, MutedTeal, onClick)

@Composable
fun CompactSupportCard(text: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) = 
    PremiumCompactCard(text, icon, MutedTeal, modifier, onClick)
