package com.example.aroura.ui.screens

import android.Manifest
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.aroura.ui.components.AdvancedAuroraBackground
import com.example.aroura.ui.theme.*
import kotlinx.coroutines.delay
import java.io.File

private const val TAG = "VoiceJournal"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceJournalScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    var recordingDuration by remember { mutableLongStateOf(0L) }
    var hasPermission by remember { mutableStateOf(false) }
    var recorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var outputFile by remember { mutableStateOf<File?>(null) }
    
    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }
    
    // Timer effect
    LaunchedEffect(isRecording) {
        if (isRecording) {
            val startTime = System.currentTimeMillis()
            while (isRecording) {
                recordingDuration = System.currentTimeMillis() - startTime
                delay(100)
            }
        }
    }
    
    // Cleanup on disposal
    DisposableEffect(Unit) {
        onDispose {
            try {
                recorder?.stop()
                recorder?.release()
            } catch (e: Exception) {
                Log.w(TAG, "Recorder cleanup: ${e.message}")
            }
        }
    }
    
    fun startRecording() {
        try {
            val file = File(context.cacheDir, "voice_journal_${System.currentTimeMillis()}.m4a")
            outputFile = file
            
            val mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }
            
            mediaRecorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioSamplingRate(44100)
                setAudioEncodingBitRate(128000)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }
            
            recorder = mediaRecorder
            isRecording = true
            recordingDuration = 0
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start recording", e)
        }
    }
    
    fun stopRecording() {
        try {
            recorder?.stop()
            recorder?.release()
        } catch (e: Exception) {
            Log.w(TAG, "Stop recording: ${e.message}")
        }
        recorder = null
        isRecording = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AdvancedAuroraBackground()
        
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Voice Journal", color = OffWhite) },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (isRecording) stopRecording()
                            onBack()
                        }) {
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
                // Visualizer
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
                            onClick = { stopRecording() },
                            modifier = Modifier.size(80.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = GentleError)
                        ) {
                            Box(modifier = Modifier.size(30.dp).background(Color.White, RoundedCornerShape(4.dp)))
                        }
                    } else {
                        // Record Button
                        Button(
                            onClick = {
                                if (hasPermission) {
                                    startRecording()
                                } else {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            },
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
                        onClick = { onBack() }, // Save recording (file already stored in cache)
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
