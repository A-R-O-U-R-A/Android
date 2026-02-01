package com.example.aroura.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.ui.components.AdvancedAuroraBackground
import com.example.aroura.ui.theme.*

enum class ReflectOption {
    MoodCheckIn,
    Journal,
    GuidedReflection,
    EmotionTracker
}

@Composable
fun ReflectMenuScreen(onNavigate: (ReflectOption) -> Unit, onProfileClick: () -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentPadding = PaddingValues(top = 40.dp, bottom = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            // Header with Profile
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = onProfileClick,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                     Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(DeepSurface, CircleShape)
                            .border(1.dp, MutedTeal.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, "Profile", tint = OffWhite, modifier = Modifier.size(16.dp))
                    }
                }
                
                Column(modifier = Modifier.align(Alignment.Center)) {
                    Text(
                        text = "Reflect",
                        style = MaterialTheme.typography.headlineMedium,
                        color = OffWhite,
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Check in with your heart.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextDarkSecondary
            )
            Spacer(modifier = Modifier.height(48.dp))
        }

        item {
            ReflectOptionCard(
                title = "How are you feeling?",
                subtitle = "Check in and reflect on your mood.",
                icon = Icons.Default.Face, // SentimentSatisfiedAlt replacement
                iconColor = Color(0xFFFFCC80),
                onClick = { onNavigate(ReflectOption.MoodCheckIn) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            ReflectOptionCard(
                title = "Journal Your Thoughts",
                subtitle = "Write or speak your feelings",
                icon = Icons.Default.Edit,
                iconColor = Color(0xFF90CAF9),
                onClick = { onNavigate(ReflectOption.Journal) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            ReflectOptionCard(
                title = "Guided Self-Reflection",
                subtitle = "Explore prompts for deeper insight",
                icon = Icons.Default.Info, // Visibility replacement
                iconColor = Color(0xFFCE93D8),
                onClick = { onNavigate(ReflectOption.GuidedReflection) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            ReflectOptionCard(
                title = "Track Your Emotions",
                subtitle = "Notice patterns and gain insight",
                icon = Icons.Default.DateRange, // Timeline replacement
                iconColor = Color(0xFFA5D6A7),
                onClick = { onNavigate(ReflectOption.EmotionTracker) }
            )
        }
    }
}

@Composable
fun ReflectOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(DeepSurface.copy(alpha = 0.5f))
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.1f),
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = OffWhite,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextDarkSecondary
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = TextDarkSecondary.copy(alpha = 0.5f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodCheckInScreen(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        AdvancedAuroraBackground()
        Scaffold(
            containerColor = Color.Transparent,
            topBar = { SimpleBackTopBar("Mood Check-In", onBack) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "How are you feeling?",
                    style = MaterialTheme.typography.headlineMedium,
                    color = OffWhite
                )
                Spacer(modifier = Modifier.height(48.dp))
                
                val moods = listOf(
                    Pair("Happy", Icons.Default.ThumbUp),
                    Pair("Calm", Icons.Default.Star),
                    Pair("Sad", Icons.Default.Clear), // ThumbDown replacement
                    Pair("Anxious", Icons.Default.Warning),
                    Pair("Tired", Icons.Default.Home),
                    Pair("Angry", Icons.Default.Close)
                )
                
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    moods.chunked(2).forEach { rowMoods ->
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            rowMoods.forEach { item ->
                                val name = item.first
                                val icon = item.second
                                Surface(
                                    modifier = Modifier.weight(1f).height(100.dp),
                                    color = DeepSurface.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(20.dp),
                                    onClick = { /* Save Mood */ }
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(icon, null, tint = MutedTeal)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(name, color = OffWhite, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(onBack: () -> Unit, onNavigateToVoice: () -> Unit) {
    var text by remember { mutableStateOf("") }
    Box(modifier = Modifier.fillMaxSize()) {
        AdvancedAuroraBackground()
        Scaffold(
            containerColor = Color.Transparent,
            topBar = { SimpleBackTopBar("Journal", onBack) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp)
            ) {
                Text(
                    text = "What's on your mind today?",
                    style = MaterialTheme.typography.titleLarge,
                    color = OffWhite.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(24.dp))
                
                // Toggle / Switch to Voice
                Button(
                    onClick = onNavigateToVoice,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepSurface.copy(alpha = 0.6f), contentColor = MutedTeal),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Icon(Icons.Default.Star, null) // Mic replacement
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Switch to Voice Journal")
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(DeepSurface.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                        .padding(24.dp)
                ) {
                    BasicTextField(
                        value = text,
                        onValueChange = { text = it },
                        textStyle = TextStyle(
                            color = OffWhite,
                            fontSize = 18.sp,
                            lineHeight = 28.sp
                        ),
                        cursorBrush = SolidColor(MutedTeal),
                        modifier = Modifier.fillMaxSize()
                    )
                    if (text.isEmpty()) {
                        Text("Start writing here...", color = TextDarkSecondary.copy(alpha = 0.5f), fontSize = 18.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { /* Save */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MutedTeal, contentColor = MidnightCharcoal),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text("Save Entry", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuidedReflectionScreen(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        AdvancedAuroraBackground()
        Scaffold(
            containerColor = Color.Transparent,
            topBar = { SimpleBackTopBar("Guided Reflection", onBack) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.8f)
                        .background(
                            brush = Brush.linearGradient(listOf(Color(0xFFCE93D8).copy(alpha = 0.2f), DeepSurface)),
                            shape = RoundedCornerShape(32.dp)
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(32.dp))
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Daily Prompt",
                            style = MaterialTheme.typography.labelMedium,
                            color = MutedTeal
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "What is one small thing you can do for yourself today?",
                            style = MaterialTheme.typography.headlineSmall,
                            color = OffWhite,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { /* Next */ }, colors = ButtonDefaults.outlinedButtonColors(contentColor = OffWhite)) {
                    Text("New Prompt")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmotionTrackerScreen(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        AdvancedAuroraBackground()
        Scaffold(
            containerColor = Color.Transparent,
            topBar = { SimpleBackTopBar("Tracker", onBack) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange, // Timeline replacement
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MutedTeal
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Your Emotional Journey",
                    style = MaterialTheme.typography.titleLarge,
                    color = OffWhite
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tracking coming soon.",
                    color = TextDarkSecondary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleBackTopBar(title: String, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(title, style = MaterialTheme.typography.titleMedium, color = OffWhite) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = OffWhite)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}