package com.example.aroura.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.ui.components.AdvancedAuroraBackground
import com.example.aroura.ui.theme.*

data class Message(val text: String, val isUser: Boolean)

@Composable
fun ChatScreen(onBack: () -> Unit) {
    var messageText by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            Message("Hello, I'm A.R.O.U.R.A.\nWhat's on your mind?", false),
            Message("Hi A.R.O.U.R.A. I'm feeling a bit overwhelmed right now...", true),
            Message("I'm here with you. It's okay to feel overwhelmed. Would you like to talk about what's going on, or perhaps take a few calming breaths together?", false)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AdvancedAuroraBackground()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                ChatTopBar(onBack)
            },
            bottomBar = {
                ChatInputBar(
                    text = messageText,
                    onTextChange = { messageText = it },
                    onSend = {
                        if (messageText.isNotBlank()) {
                            messages.add(Message(messageText, true))
                            messageText = ""
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
            ) {
                // Header Subtitle
                item {
                    Text(
                        text = "I'm here for you. What's on your mind?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextDarkSecondary.copy(alpha = 0.7f),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                items(messages) { msg ->
                    ChatBubble(msg)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Talk to A.R.O.U.R.A",
                style = MaterialTheme.typography.titleMedium,
                color = OffWhite
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = OffWhite
                )
            }
        },
        actions = {
            IconButton(onClick = { /* TODO */ }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = OffWhite
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
fun ChatBubble(message: Message) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!message.isUser) {
            // AI Avatar
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
                            colors = listOf(MutedTeal, SoftBlue)
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Simple sparkle
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = OffWhite,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            color = if (message.isUser) 
                DeepSurface.copy(alpha = 0.6f) 
            else 
                Color(0xFFE3F2FD).copy(alpha = 0.15f), // Glassy white/blue
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = if (message.isUser) 20.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 20.dp
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyLarge,
                color = OffWhite,
                modifier = Modifier.padding(16.dp),
                lineHeight = 24.sp
            )
        }
    }
}

@Composable
fun ChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Surface(
        color = DeepSurface.copy(alpha = 0.9f),
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(32.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* TODO: Voice */ }, modifier = Modifier.size(32.dp)) {
                // Using Add instead of Mic if Mic is missing
                Icon(Icons.Default.Add, null, tint = TextDarkSecondary)
            }
            
            Spacer(modifier = Modifier.width(8.dp))

            Box(modifier = Modifier.weight(1f)) {
                if (text.isEmpty()) {
                    Text(
                        text = "Type a message...",
                        color = TextDarkSecondary.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                BasicTextField(
                    value = text,
                    onValueChange = onTextChange,
                    textStyle = TextStyle(
                        color = OffWhite,
                        fontSize = 16.sp
                    ),
                    cursorBrush = SolidColor(MutedTeal)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = onSend) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send, 
                    contentDescription = "Send", 
                    tint = MutedTeal
                )
            }
        }
    }
}
