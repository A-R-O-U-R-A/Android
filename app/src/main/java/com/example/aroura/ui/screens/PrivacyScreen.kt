package com.example.aroura.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.aroura.ui.components.AdvancedAuroraBackground
import com.example.aroura.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyScreen(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        AdvancedAuroraBackground()
        
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Privacy & Data", color = OffWhite) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = OffWhite)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(horizontal = 24.dp)) {
                PrivacyToggle("Share Usage Data", "Help us improve A.R.O.U.R.A", true)
                PrivacyToggle("Personalized Suggestions", "Tailor content to your mood", true)
                PrivacyToggle("Allow Analytics", "Anonymous performance tracking", false)
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { /* Export */ },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MutedTeal),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Export My Data")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { /* Delete */ },
                    colors = ButtonDefaults.buttonColors(containerColor = GentleError.copy(alpha = 0.1f), contentColor = GentleError),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Delete Account")
                }
            }
        }
    }
}

@Composable
fun PrivacyToggle(title: String, subtitle: String, initial: Boolean) {
    var checked by remember { mutableStateOf(initial) }
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = OffWhite)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextDarkSecondary)
        }
        Switch(
            checked = checked,
            onCheckedChange = { checked = it },
            colors = SwitchDefaults.colors(checkedThumbColor = MutedTeal, checkedTrackColor = MutedTeal.copy(alpha = 0.3f))
        )
    }
}
