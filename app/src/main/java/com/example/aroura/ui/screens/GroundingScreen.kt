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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.ui.components.AdvancedAuroraBackground
import com.example.aroura.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroundingScreen(onBack: () -> Unit) {
    // 5-4-3-2-1 Technique State
    var currentStep by remember { mutableIntStateOf(5) }
    
    // Steps Data
    val steps = mapOf(
        5 to "Acknowledge 5 things you see around you.",
        4 to "Acknowledge 4 things you can touch.",
        3 to "Acknowledge 3 things you hear.",
        2 to "Acknowledge 2 things you can smell.",
        1 to "Acknowledge 1 thing you can taste."
    )

    Box(modifier = Modifier.fillMaxSize()) {
        AdvancedAuroraBackground()
        
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Ground Yourself", color = OffWhite) },
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
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "5-4-3-2-1 Technique",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MutedTeal,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Use your senses to bring yourself back to the present moment.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDarkSecondary,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(48.dp))
                
                // Current Step Display
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(DeepSurface.copy(alpha = 0.5f), CircleShape)
                        .border(2.dp, MutedTeal.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$currentStep",
                        fontSize = 120.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = OffWhite.copy(alpha = 0.9f)
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Instruction Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = DeepSurface.copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = steps[currentStep] ?: "Breathe.",
                            style = MaterialTheme.typography.titleLarge,
                            color = OffWhite,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                if (currentStep > 1) {
                                    currentStep--
                                } else {
                                    // Reset or Finish
                                    currentStep = 5
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MutedTeal, contentColor = MidnightCharcoal),
                            modifier = Modifier.fillMaxWidth().height(56.dp)
                        ) {
                            if (currentStep == 1) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Refresh, null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Start Over")
                                }
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Check, null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("I've done this")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
