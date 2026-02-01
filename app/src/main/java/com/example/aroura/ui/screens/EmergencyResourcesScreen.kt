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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.ui.components.ArouraBackground
import com.example.aroura.ui.theme.*

/**
 * Emergency Resources Screen - Premium Redesign
 * 
 * Features:
 * - Categorized sections
 * - Premium resource cards
 * - Location-based nearby services
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyResourcesScreen(onBack: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val services = listOf(
        Triple("Police", "100", GentleError),
        Triple("Ambulance", "102", MutedTeal),
        Triple("Fire", "101", CalmingPeach)
    )
    
    val hospitals = listOf(
        Pair("City General Hospital", "2.4 km away"),
        Pair("Apollo Medical Center", "5.1 km away")
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
                        "Emergency Resources",
                        style = MaterialTheme.typography.titleLarge,
                        color = OffWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(ArouraSpacing.md.dp),
                contentPadding = PaddingValues(bottom = ArouraSpacing.xxl.dp)
            ) {
                // Emergency Services Section
                item {
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(400, delayMillis = 100))
                    ) {
                        Text(
                            "Emergency Services",
                            style = MaterialTheme.typography.titleMedium,
                            color = MutedTeal,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(ArouraSpacing.sm.dp))
                }
                
                itemsIndexed(services) { index, (name, number, color) ->
                    val delay = 150 + (index * 60)
                    
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(400, delayMillis = delay)) + slideInVertically(
                            initialOffsetY = { 30 },
                            animationSpec = tween(400, delayMillis = delay, easing = EaseOutCubic)
                        )
                    ) {
                        PremiumResourceCard(
                            title = name,
                            subtitle = number,
                            icon = Icons.Default.Phone,
                            accentColor = color
                        )
                    }
                }
                
                // Nearby Hospitals Section
                item {
                    Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
                    
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(400, delayMillis = 350))
                    ) {
                        Text(
                            "Nearby Hospitals",
                            style = MaterialTheme.typography.titleMedium,
                            color = MutedTeal,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(ArouraSpacing.sm.dp))
                }
                
                itemsIndexed(hospitals) { index, (name, distance) ->
                    val delay = 400 + (index * 60)
                    
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(400, delayMillis = delay)) + slideInVertically(
                            initialOffsetY = { 30 },
                            animationSpec = tween(400, delayMillis = delay, easing = EaseOutCubic)
                        )
                    ) {
                        PremiumResourceCard(
                            title = name,
                            subtitle = distance,
                            icon = Icons.Default.LocationOn,
                            accentColor = SoftBlue
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PremiumResourceCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "resourceScale"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = DeepSurface.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(ArouraSpacing.cardRadius.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .scale(scale)
            .border(
                1.dp,
                accentColor.copy(alpha = 0.15f),
                RoundedCornerShape(ArouraSpacing.cardRadius.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { /* Action */ }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = ArouraSpacing.lg.dp),
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
            
            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    color = OffWhite,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = accentColor
                )
            }
        }
    }
}

// Legacy compatibility
@Composable
fun ResourceCard(title: String, subtitle: String, icon: ImageVector) {
    PremiumResourceCard(title, subtitle, icon, MutedTeal)
}
