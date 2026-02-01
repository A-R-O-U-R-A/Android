package com.example.aroura.ui.screens

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.ui.components.AdvancedAuroraBackground
import com.example.aroura.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onBack: () -> Unit, onNavigate: (String) -> Unit = {}) {
    Box(modifier = Modifier.fillMaxSize()) {
        AdvancedAuroraBackground()
        
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Profile", color = OffWhite) },
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
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // User Header
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .background(MutedTeal.copy(alpha = 0.2f), CircleShape)
                                .border(2.dp, MutedTeal, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, null, tint = OffWhite, modifier = Modifier.size(50.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("User Name", style = MaterialTheme.typography.titleLarge, color = OffWhite)
                        Text("user@example.com", style = MaterialTheme.typography.bodyMedium, color = TextDarkSecondary)
                    }
                }

                item { Divider(color = DeepSurface) }

                // Settings
                item { SettingsSectionTitle("General") }
                item { 
                    SettingsItem("Language", "English", Icons.Default.Info) { onNavigate("language") }
                }
                item { 
                    ToggleSettingsItem("AI Memory", "Allow AI to remember context", true, Icons.Default.Settings) 
                }

                item { SettingsSectionTitle("Content") }
                item { 
                    SettingsItem("Devotional Preferences", "All Religions", Icons.Default.Favorite) { onNavigate("devotional") }
                }

                item { SettingsSectionTitle("Privacy & Safety") }
                item { 
                    SettingsItem("Privacy & Data", "Manage your data", Icons.Default.Lock) { onNavigate("privacy") }
                }
                item { 
                    SettingsItem("Ethics & Disclaimers", "Read our manifesto", Icons.Default.Info) { onNavigate("ethics") }
                }
                
                item { Spacer(modifier = Modifier.height(40.dp)) }
                
                item {
                    Button(
                        onClick = { /* Logout */ },
                        colors = ButtonDefaults.buttonColors(containerColor = GentleError.copy(alpha = 0.2f), contentColor = GentleError),
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text("Log Out")
                    }
                }
                
                item { Spacer(modifier = Modifier.height(40.dp)) }
            }
        }
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
