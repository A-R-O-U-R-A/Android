package com.example.aroura.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.aroura.data.local.PreferencesManager
import com.example.aroura.ui.components.AdvancedAuroraBackground
import com.example.aroura.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevotionalPreferencesScreen(
    preferencesManager: PreferencesManager,
    onBack: () -> Unit
) {
    val options = listOf("Hinduism", "Islam", "Christianity", "Sikhism", "Buddhism", "Jainism", "Secular / Nature")
    val savedTraditions by preferencesManager.devotionalTraditionsFlow.collectAsState(initial = emptySet())
    val selected = remember(savedTraditions) { savedTraditions.toMutableList().toMutableStateList() }
    val scope = rememberCoroutineScope()

    // Save whenever selection changes
    fun toggleAndSave(option: String) {
        if (selected.contains(option)) selected.remove(option) else selected.add(option)
        scope.launch { preferencesManager.saveDevotionalTraditions(selected.toSet()) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AdvancedAuroraBackground()
        
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Devotional Preferences", color = OffWhite) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = OffWhite)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            LazyColumn(modifier = Modifier.padding(padding).padding(horizontal = 24.dp)) {
                item {
                    Text(
                        "Select the traditions you resonate with to personalize your Calm feed.",
                        color = TextDarkSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
                
                items(options) { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { toggleAndSave(option) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selected.contains(option),
                            onCheckedChange = null, // Handled by Row click
                            colors = CheckboxDefaults.colors(checkedColor = MutedTeal, uncheckedColor = TextDarkSecondary)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(option, style = MaterialTheme.typography.bodyLarge, color = OffWhite)
                    }
                }
            }
        }
    }
}
