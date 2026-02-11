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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.aroura.data.local.PreferencesManager
import com.example.aroura.ui.components.ArouraBackground
import com.example.aroura.ui.theme.*
import kotlinx.coroutines.launch

/**
 * Trusted Contacts Screen - Premium Redesign
 * 
 * Features:
 * - Persisted contact list via PreferencesManager
 * - Animated contact list
 * - Premium delete interaction
 * - Floating add button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrustedContactsScreen(
    preferencesManager: PreferencesManager,
    onBack: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    val savedContacts by preferencesManager.trustedContactsFlow.collectAsState(initial = emptyList())
    val contacts = remember(savedContacts) { savedContacts.toMutableList().toMutableStateList() }
    val scope = rememberCoroutineScope()
    
    fun saveContacts() {
        scope.launch { preferencesManager.saveTrustedContacts(contacts.toList()) }
    }
    
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                            "Trusted Contacts",
                            style = MaterialTheme.typography.titleLarge,
                            color = OffWhite,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                
                // Add Button
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(400, delayMillis = 200)) + scaleIn(
                        initialScale = 0.8f,
                        animationSpec = tween(400, delayMillis = 200)
                    )
                ) {
                    IconButton(
                        onClick = { 
                            contacts.add("New Contact")
                            saveContacts()
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .background(MutedTeal.copy(alpha = 0.15f), CircleShape)
                    ) {
                        Icon(Icons.Default.Add, "Add", tint = MutedTeal)
                    }
                }
            }
            
            // Subtitle
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(400, delayMillis = 100))
            ) {
                Text(
                    "People you trust in moments of need.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDarkSecondary,
                    modifier = Modifier.padding(start = ArouraSpacing.sm.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
            
            if (contacts.isEmpty()) {
                // Empty State
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(500, delayMillis = 200))
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(80.dp))
                        Icon(
                            Icons.Default.Person,
                            null,
                            tint = TextDarkSecondary.copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(ArouraSpacing.md.dp))
                        Text(
                            "No contacts yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextDarkSecondary
                        )
                        Spacer(modifier = Modifier.height(ArouraSpacing.xs.dp))
                        Text(
                            "Tap + to add someone you trust",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextDarkSecondary.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(ArouraSpacing.md.dp),
                    contentPadding = PaddingValues(bottom = ArouraSpacing.xxl.dp)
                ) {
                    itemsIndexed(contacts) { index, contact ->
                        val delay = 150 + (index * 60)
                        
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(tween(400, delayMillis = delay)) + slideInHorizontally(
                                initialOffsetX = { 30 },
                                animationSpec = tween(400, delayMillis = delay, easing = EaseOutCubic)
                            )
                        ) {
                            PremiumContactCard(
                                name = contact,
                                onDelete = { 
                                    contacts.remove(contact)
                                    saveContacts()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PremiumContactCard(name: String, onDelete: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "contactScale"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = DeepSurface.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(ArouraSpacing.cardRadius.dp),
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .border(
                1.dp,
                Color.White.copy(alpha = 0.08f),
                RoundedCornerShape(ArouraSpacing.cardRadius.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = ArouraSpacing.lg.dp, vertical = ArouraSpacing.md.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(CalmingPeach.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        name.first().toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = CalmingPeach,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Spacer(modifier = Modifier.width(ArouraSpacing.md.dp))
                
                Text(
                    name,
                    style = MaterialTheme.typography.titleMedium,
                    color = OffWhite,
                    fontWeight = FontWeight.Medium
                )
            }
            
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(40.dp)
                    .background(GentleError.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(
                    Icons.Default.Delete,
                    null,
                    tint = GentleError,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
