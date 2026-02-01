package com.example.aroura.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.ui.components.AdvancedAuroraBackground
import com.example.aroura.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceJournalScreen(onBack: () -> Unit) {
    var isRecording by remember { mutableStateOf(false) }
    var recordingDuration by remember { mutableLongStateOf(0L) }
    
    LaunchedEffect(isRecording) {
        if (isRecording) {
            val startTime = System.currentTimeMillis()
            while (isRecording) {
                recordingDuration = System.currentTimeMillis() - startTime
                delay(100)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AdvancedAuroraBackground()
        
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Voice Journal", color = OffWhite) },
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Visualizer Placeholder
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.height(100.dp).fillMaxWidth()
                ) {
                    repeat(20) {
                        val height = if (isRecording) (20..80).random().dp else 10.dp
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .height(height)
                                .padding(horizontal = 2.dp)
                                .background(MutedTeal, CircleShape)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(48.dp))
                
                // Timer
                val seconds = (recordingDuration / 1000) % 60
                val minutes = (recordingDuration / 1000) / 60
                Text(
                    text = String.format("%02d:%02d", minutes, seconds),
                    style = MaterialTheme.typography.displayMedium,
                    color = OffWhite
                )
                
                Spacer(modifier = Modifier.height(64.dp))
                
                // Controls
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isRecording) {
                        // Stop Button
                        Button(
                            onClick = { isRecording = false },
                            modifier = Modifier.size(80.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = GentleError)
                        ) {
                            Box(modifier = Modifier.size(30.dp).background(Color.White, RoundedCornerShape(4.dp)))
                        }
                    } else {
                        // Record Button
                        Button(
                            onClick = { isRecording = true; recordingDuration = 0 },
                            modifier = Modifier.size(80.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = GentleError)
                        ) {
                            Box(modifier = Modifier.size(24.dp).background(Color.White, CircleShape))
                        }
                    }
                }
                
                if (!isRecording && recordingDuration > 0) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { onBack() }, // "Save"
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MutedTeal, contentColor = MidnightCharcoal)
                    ) {
                        Text("Save Recording")
                    }
                }
            }
        }
    }
}
