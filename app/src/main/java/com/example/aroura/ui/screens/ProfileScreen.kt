package com.example.aroura.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import com.example.aroura.ui.components.ArouraBackground
import com.example.aroura.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit, 
    onNavigate: (String) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        ArouraBackground()
        
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            "Profile", 
                            color = OffWhite,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Light
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = OffWhite)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = ArouraSpacing.screenHorizontal.dp),
                verticalArrangement = Arrangement.spacedBy(ArouraSpacing.lg.dp),
                contentPadding = PaddingValues(bottom = ArouraSpacing.xxl.dp)
            ) {
                // User Header
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = ArouraSpacing.lg.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Profile Avatar with subtle pulse
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            MutedTeal.copy(alpha = 0.3f),
                                            MutedTeal.copy(alpha = 0.1f)
                                        )
                                    ),
                                    CircleShape
                                )
                                .border(2.dp, MutedTeal.copy(alpha = 0.6f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person, 
                                null, 
                                tint = OffWhite, 
                                modifier = Modifier.size(48.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(ArouraSpacing.md.dp))
                        
                        Text(
                            "Sarah", 
                            style = MaterialTheme.typography.headlineSmall, 
                            color = OffWhite,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            "sarah@example.com", 
                            style = MaterialTheme.typography.bodyMedium, 
                            color = TextDarkSecondary
                        )
                    }
                }

                item { 
                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.05f),
                        thickness = 1.dp
                    ) 
                }

                // General Settings
                item { SettingsSectionTitle("General") }
                item { 
                    SettingsItem(
                        "Language", 
                        "English", 
                        Icons.Default.Info
                    ) { onNavigate("language") }
                }
                item { 
                    ToggleSettingsItem(
                        "AI Memory", 
                        "Allow AI to remember context", 
                        true, 
                        Icons.Default.Settings
                    ) 
                }

                // Content Settings
                item { SettingsSectionTitle("Content") }
                item { 
                    SettingsItem(
                        "Devotional Preferences", 
                        "All Religions", 
                        Icons.Default.Favorite
                    ) { onNavigate("devotional") }
                }

                // Privacy Settings
                item { SettingsSectionTitle("Privacy & Safety") }
                item { 
                    SettingsItem(
                        "Privacy & Data", 
                        "Manage your data", 
                        Icons.Default.Lock
                    ) { onNavigate("privacy") }
                }
                item { 
                    SettingsItem(
                        "Ethics & Disclaimers", 
                        "Read our manifesto", 
                        Icons.Default.Info
                    ) { onNavigate("ethics") }
                }
                
                item { Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp)) }
                
                // Logout Button
                item {
                    PremiumLogoutButton(onClick = onLogout)
                }
            }
        }
    }
}

@Composable
private fun PremiumLogoutButton(onClick: () -> Unit) {
    var showConfirmation by remember { mutableStateOf(false) }
    
    if (showConfirmation) {
        AlertDialog(
            onDismissRequest = { showConfirmation = false },
            containerColor = DeepSurface,
            title = {
                Text(
                    "Log Out?",
                    color = OffWhite,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Text(
                    "Are you sure you want to log out? You'll need to sign in again to continue.",
                    color = TextDarkSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmation = false
                        onClick()
                    }
                ) {
                    Text("Log Out", color = GentleError)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmation = false }) {
                    Text("Cancel", color = MutedTeal)
                }
            }
        )
    }
    
    Button(
        onClick = { showConfirmation = true },
        colors = ButtonDefaults.buttonColors(
            containerColor = GentleError.copy(alpha = 0.12f), 
            contentColor = GentleError
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(ArouraSpacing.cardRadius.dp),
        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
            brush = androidx.compose.ui.graphics.SolidColor(GentleError.copy(alpha = 0.3f))
        )
    ) {
        Icon(
            Icons.Default.Close,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "Log Out",
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun SettingsSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MutedTeal,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun SettingsItem(title: String, subtitle: String, icon: ImageVector, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = TextDarkSecondary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = OffWhite)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextDarkSecondary)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = TextDarkSecondary)
    }
}

@Composable
fun ToggleSettingsItem(title: String, subtitle: String, initialChecked: Boolean, icon: ImageVector) {
    var checked by remember { mutableStateOf(initialChecked) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = TextDarkSecondary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = OffWhite)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextDarkSecondary)
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
