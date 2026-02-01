package com.example.aroura.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.aroura.ui.components.AdvancedAuroraBackground
import com.example.aroura.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EthicsScreen(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        AdvancedAuroraBackground()
        
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Ethics & Manifesto", color = OffWhite) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = OffWhite)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Our Promise to You", style = MaterialTheme.typography.headlineSmall, color = MutedTeal)
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    "1. Your mental health comes first.\n\n" +
                    "2. We do not sell your data.\n\n" +
                    "3. We use AI responsibly, not to replace human connection but to bridge the gap.\n\n" +
                    "4. We are committed to inclusivity across all cultures and beliefs.\n\n" +
                    "5. We are not a replacement for professional medical advice. If you are in crisis, please use the Support tab to contact emergency services.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = OffWhite
                )
                
                Spacer(modifier = Modifier.height(48.dp))
                Text("Version 1.0.0", color = TextDarkSecondary)
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}
