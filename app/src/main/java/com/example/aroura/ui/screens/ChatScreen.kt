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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aroura.ui.components.ArouraBackground
import com.example.aroura.ui.theme.*
import com.example.aroura.ui.viewmodels.ChatUiState
import com.example.aroura.ui.viewmodels.ChatViewModel
import com.example.aroura.ui.viewmodels.ChatViewModelFactory
import com.example.aroura.ui.viewmodels.UIChatMessage
import kotlinx.coroutines.delay

/**
 * Chat Screen - AI-Powered Mental Health Companion
 * 
 * Features:
 * - Real-time AI responses from Gemini
 * - Two personas: Counselor and Best Friend
 * - Message persistence in MongoDB
 * - Smooth animations and premium UI
 * - Crisis detection and safety responses
 */
@Composable
fun ChatScreen(mode: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val persona = if (mode == "Counselor") "counselor" else "bestfriend"
    
    val viewModel: ChatViewModel = viewModel(
        key = "chat_$persona",
        factory = ChatViewModelFactory(context, persona)
    )
    
    val uiState by viewModel.uiState.collectAsState()
    
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    
    // Auto-scroll when new messages arrive
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }
    
    // Show error snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                actionLabel = "Retry",
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ArouraBackground()

        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                PremiumChatTopBar(
                    mode = mode,
                    onBack = onBack,
                    onNewChat = { viewModel.startNewConversation() }
                )
            },
            bottomBar = {
                PremiumChatInputBar(
                    text = messageText,
                    onTextChange = { messageText = it },
                    onSend = {
                        if (messageText.isNotBlank()) {
                            viewModel.sendMessage(messageText)
                            messageText = ""
                        }
                    },
                    enabled = !uiState.isSending
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
                
                // Welcome message when no messages
                if (uiState.showWelcome && uiState.messages.isEmpty()) {
                    item {
                        WelcomeMessage(mode = mode)
                    }
                }

                items(uiState.messages, key = { it.id }) { msg ->
                    PremiumChatBubble(message = msg, mode = mode)
                }
                
                // Typing indicator
                if (uiState.aiTyping) {
                    item {
                        TypingIndicator(mode = mode)
                    }
                }
            }
            
            // Loading overlay for initial load
            if (uiState.isLoading && uiState.messages.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DeepSurface.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MutedTeal)
                }
            }
        }
    }
}

@Composable
private fun WelcomeMessage(mode: String) {
    val accentColor = if (mode == "Counselor") SoftBlue else CalmingPeach
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
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
                modifier = Modifier.size(32.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = if (mode == "Counselor") 
                "Hello, I'm here to listen" 
            else 
                "Hey there! 👋",
            style = MaterialTheme.typography.titleLarge,
            color = OffWhite,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = if (mode == "Counselor")
                "Share what's on your mind. Everything you say stays private."
            else
                "I'm your buddy! Tell me what's going on in your life.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextDarkSecondary,
            modifier = Modifier.padding(horizontal = 32.dp),
            lineHeight = 22.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PremiumChatTopBar(mode: String, onBack: () -> Unit, onNewChat: () -> Unit) {
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
                // New chat button
                IconButton(onClick = onNewChat) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "New Chat",
                        tint = TextDarkSecondary
                    )
                }
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
private fun PremiumChatBubble(message: UIChatMessage, mode: String) {
    // FIXED: Use message.id as key so animation only runs once per unique message
    // Previously used LaunchedEffect(Unit) which caused re-animation on every recomposition
    val accentColor = if (mode == "Counselor") SoftBlue else CalmingPeach
    
    // Simple fade-in without heavy animation overhead
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!message.isFromUser) {
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
            color = when {
                message.isLoading -> DeepSurface.copy(alpha = 0.5f)
                message.isError -> Color(0xFF5C2A2A).copy(alpha = 0.7f)
                message.isCrisisResponse -> Color(0xFF2A3D5C).copy(alpha = 0.8f)
                message.isFromUser -> accentColor.copy(alpha = 0.15f)
                else -> DeepSurface.copy(alpha = 0.7f)
            },
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = if (message.isFromUser) 20.dp else 6.dp,
                bottomEnd = if (message.isFromUser) 6.dp else 20.dp
            ),
            border = if (message.isFromUser) null else ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.08f),
                        Color.Transparent
                    )
                )
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            if (message.isLoading) {
                // Show loading indicator
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(accentColor.copy(alpha = 0.5f), CircleShape)
                        )
                    }
                }
            } else {
                Column {
                    if (message.isCrisisResponse) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = SoftBlue,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Support Resources Available",
                                style = MaterialTheme.typography.labelSmall,
                                color = SoftBlue
                            )
                        }
                    }
                    Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (message.isError) Color(0xFFEF9A9A) else OffWhite,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        lineHeight = 24.sp
                    )
                }
            }
        }
    }
}

// Simplified typing indicator with single animation
@Composable
private fun TypingIndicator(mode: String) {
    val accentColor = if (mode == "Counselor") SoftBlue else CalmingPeach
    
    // Single animation for all dots to reduce overhead
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dots"
    )
    
    // Calculate each dot's alpha based on progress with phase offset
    val dot1Alpha = ((animationProgress * 3f) % 1f).coerceIn(0.4f, 0.9f)
    val dot2Alpha = (((animationProgress + 0.33f) * 3f) % 1f).coerceIn(0.4f, 0.9f)
    val dot3Alpha = (((animationProgress + 0.66f) * 3f) % 1f).coerceIn(0.4f, 0.9f)
    
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
                        .background(accentColor.copy(alpha = dot1Alpha), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(accentColor.copy(alpha = dot2Alpha), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(accentColor.copy(alpha = dot3Alpha), CircleShape)
                )
            }
        }
    }
}

@Composable
private fun PremiumChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    enabled: Boolean = true
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
                modifier = Modifier.size(40.dp),
                enabled = enabled
            ) {
                Icon(
                    Icons.Default.Add, 
                    null, 
                    tint = if (enabled) TextDarkSecondary else TextDarkSecondary.copy(alpha = 0.3f),
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
                        text = if (enabled) "Type a message..." else "Waiting for response...",
                        color = TextDarkSecondary.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                BasicTextField(
                    value = text,
                    onValueChange = onTextChange,
                    enabled = enabled,
                    textStyle = TextStyle(
                        color = OffWhite,
                        fontSize = 16.sp
                    ),
                    cursorBrush = SolidColor(MutedTeal),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Send button - simplified without heavy scale animation
            val sendEnabled = text.isNotBlank() && enabled
            
            IconButton(
                onClick = onSend,
                enabled = sendEnabled,
                modifier = Modifier
                    .size(40.dp)
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
