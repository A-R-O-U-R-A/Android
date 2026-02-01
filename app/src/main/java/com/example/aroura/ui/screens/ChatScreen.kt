package com.example.aroura.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.ui.components.ArouraBackground
import com.example.aroura.ui.theme.*
import kotlinx.coroutines.delay

data class Message(val text: String, val isUser: Boolean, val timestamp: Long = System.currentTimeMillis())

/**
 * Chat Screen - Premium Redesign
 * 
 * Features:
 * - Calm, focused conversation space
 * - Smooth message animations
 * - Premium typing indicator
 * - Gentle glass-morphism bubbles
 * - Refined input bar
 */
@Composable
fun ChatScreen(mode: String, onBack: () -> Unit) {
    var messageText by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val messages = remember {
        mutableStateListOf(
            Message(
                if (mode == "Counselor") 
                    "Hello. I'm here to listen and support you. How are you feeling today?" 
                else 
                    "Hey there! 👋 I'm your buddy. What's on your mind?", 
                false
            )
        )
    }
    
    // Auto-scroll when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ArouraBackground()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                PremiumChatTopBar(mode, onBack)
            },
            bottomBar = {
                PremiumChatInputBar(
                    text = messageText,
                    onTextChange = { messageText = it },
                    onSend = {
                        if (messageText.isNotBlank()) {
                            messages.add(Message(messageText, true))
                            val userMessage = messageText
                            messageText = ""
                            
                            // Simulate AI response
                            isTyping = true
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = ArouraSpacing.md.dp),
                verticalArrangement = Arrangement.spacedBy(ArouraSpacing.md.dp),
                contentPadding = PaddingValues(
                    top = ArouraSpacing.md.dp, 
                    bottom = ArouraSpacing.md.dp
                )
            ) {
                // Mode indicator
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            color = DeepSurface.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (mode == "Counselor") "🌙 Professional Support Mode" else "⭐ Friendly Chat Mode",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (mode == "Counselor") SoftBlue else CalmingPeach,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                items(messages, key = { it.timestamp }) { msg ->
                    PremiumChatBubble(message = msg, mode = mode)
                }
                
                // Typing indicator
                if (isTyping) {
                    item {
                        TypingIndicator(mode = mode)
                        
                        // Simulate response after delay
                        LaunchedEffect(Unit) {
                            delay(1500)
                            isTyping = false
                            messages.add(
                                Message(
                                    if (mode == "Counselor")
                                        "I hear you. Thank you for sharing that with me. Would you like to tell me more about how that makes you feel?"
                                    else
                                        "I totally get that! Want to talk about it more? I'm all ears 👂",
                                    false
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PremiumChatTopBar(mode: String, onBack: () -> Unit) {
    Surface(
        color = DeepSurface.copy(alpha = 0.8f),
        modifier = Modifier.fillMaxWidth()
    ) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = if (mode == "Counselor")
                                        listOf(SoftBlue.copy(alpha = 0.3f), MutedTeal.copy(alpha = 0.2f))
                                    else
                                        listOf(CalmingPeach.copy(alpha = 0.3f), CalmingLavender.copy(alpha = 0.2f))
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (mode == "Counselor") SoftBlue else CalmingPeach,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = "A.R.O.U.R.A",
                            style = MaterialTheme.typography.titleMedium,
                            color = OffWhite,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (mode == "Counselor") "Counselor" else "Companion",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextDarkSecondary
                        )
                    }
                }
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
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = TextDarkSecondary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )
    }
}

@Composable
private fun PremiumChatBubble(message: Message, mode: String) {
    // Entrance animation
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    
    val accentColor = if (mode == "Counselor") SoftBlue else CalmingPeach
    
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(300)) + slideInVertically(
            initialOffsetY = { 20 },
            animationSpec = tween(300, easing = EaseOutCubic)
        )
    ) {
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
                            brush = Brush.linearGradient(
                                colors = listOf(accentColor.copy(alpha = 0.3f), MutedTeal.copy(alpha = 0.2f))
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Surface(
                color = if (message.isUser) 
                    accentColor.copy(alpha = 0.15f)
                else 
                    DeepSurface.copy(alpha = 0.7f),
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp,
                    bottomStart = if (message.isUser) 20.dp else 6.dp,
                    bottomEnd = if (message.isUser) 6.dp else 20.dp
                ),
                border = if (message.isUser) null else ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                ),
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = OffWhite,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    lineHeight = 24.sp
                )
            }
        }
    }
}

@Composable
private fun TypingIndicator(mode: String) {
    val accentColor = if (mode == "Counselor") SoftBlue else CalmingPeach
    
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    val dot1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )
    val dot2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 150),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )
    val dot3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 300),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        // AI Avatar
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(accentColor.copy(alpha = 0.3f), MutedTeal.copy(alpha = 0.2f))
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(16.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Surface(
            color = DeepSurface.copy(alpha = 0.7f),
            shape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 6.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .scale(0.6f + dot1 * 0.4f)
                        .background(accentColor.copy(alpha = 0.4f + dot1 * 0.4f), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .scale(0.6f + dot2 * 0.4f)
                        .background(accentColor.copy(alpha = 0.4f + dot2 * 0.4f), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .scale(0.6f + dot3 * 0.4f)
                        .background(accentColor.copy(alpha = 0.4f + dot3 * 0.4f), CircleShape)
                )
            }
        }
    }
}

@Composable
private fun PremiumChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Surface(
        color = DeepSurface.copy(alpha = 0.95f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(ArouraSpacing.md.dp),
        shape = RoundedCornerShape(28.dp),
        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.1f),
                    Color.White.copy(alpha = 0.02f)
                )
            )
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Default.Add, 
                    null, 
                    tint = TextDarkSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
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
                    cursorBrush = SolidColor(MutedTeal),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Send button with animation
            val sendEnabled = text.isNotBlank()
            val sendScale by animateFloatAsState(
                targetValue = if (sendEnabled) 1f else 0.8f,
                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                label = "sendScale"
            )
            
            IconButton(
                onClick = onSend,
                enabled = sendEnabled,
                modifier = Modifier
                    .size(40.dp)
                    .scale(sendScale)
                    .background(
                        if (sendEnabled) MutedTeal.copy(alpha = 0.2f) else Color.Transparent,
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (sendEnabled) MutedTeal else TextDarkSecondary.copy(alpha = 0.3f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
