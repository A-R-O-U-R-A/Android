package com.example.aroura.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aroura.ui.components.AdvancedAuroraBackground
import com.example.aroura.ui.theme.*

@Composable
fun HomeScreen(onNavigateToChat: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    
    // Navigation States
    var chatScreenState by remember { mutableStateOf("selection") }
    var calmNavigationState by remember { mutableStateOf("list") }
    var currentMediaItem by remember { mutableStateOf<CalmMediaItem?>(null) }
    var reflectNavigationState by remember { mutableStateOf("menu") }
    var showProfile by remember { mutableStateOf(false) }

    // Helper to switch to chat tab
    val navigateToChatTab = {
        selectedTab = 1
        chatScreenState = "selection"
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        AdvancedAuroraBackground()

        if (showProfile) {
            // Overlay Profile Screen
            // Z-index 1 (highest)
            Box(modifier = Modifier.fillMaxSize()) { // Wrap to ensure it's on top if using Box children order
                ProfileScreen(onBack = { showProfile = false })
            }
        } else {
            Scaffold(
                containerColor = Color.Transparent,
                bottomBar = {
                    val hideBottomBar = (selectedTab == 1 && chatScreenState == "chatting") ||
                                        (selectedTab == 2 && calmNavigationState == "player") ||
                                        (selectedTab == 3 && reflectNavigationState != "menu")
                    
                    if (!hideBottomBar) {
                        ArouraBottomNavigation(
                            selectedTab = selectedTab,
                            onTabSelected = { 
                                selectedTab = it
                                // Reset states
                                if (it != 1) chatScreenState = "selection"
                                if (it != 2) calmNavigationState = "list"
                                if (it != 3) reflectNavigationState = "menu"
                            }
                        )
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            bottom = if (
                                (selectedTab == 1 && chatScreenState == "chatting") || 
                                (selectedTab == 2 && calmNavigationState == "player") ||
                                (selectedTab == 3 && reflectNavigationState != "menu")
                            ) 0.dp else paddingValues.calculateBottomPadding()
                        )
                ) {
                    when(selectedTab) {
                        0 -> HomeContent(onNavigateToChat = navigateToChatTab, onProfileClick = { showProfile = true }) 
                        1 -> {
                            if (chatScreenState == "selection") {
                                ChatSelectionScreen(
                                    onChatSelected = { _ -> chatScreenState = "chatting" },
                                    onProfileClick = { showProfile = true }
                                )
                            } else {
                                ChatScreen(onBack = { chatScreenState = "selection" })
                            }
                        }
                        2 -> {
                            if (calmNavigationState == "list") {
                                CalmScreen(
                                    onItemClick = { item ->
                                        currentMediaItem = item
                                        calmNavigationState = "player"
                                    },
                                    onProfileClick = { showProfile = true }
                                )
                            } else {
                                currentMediaItem?.let { item ->
                                    CalmPlayerScreen(
                                        item = item,
                                        onBack = { calmNavigationState = "list" }
                                    )
                                }
                            }
                        }
                        3 -> {
                            when(reflectNavigationState) {
                                "menu" -> ReflectMenuScreen(
                                    onNavigate = { option ->
                                        reflectNavigationState = when(option) {
                                            ReflectOption.MoodCheckIn -> "mood"
                                            ReflectOption.Journal -> "journal"
                                            ReflectOption.GuidedReflection -> "guided"
                                            ReflectOption.EmotionTracker -> "tracker"
                                        }
                                    },
                                    onProfileClick = { showProfile = true }
                                )
                                "mood" -> MoodCheckInScreen(onBack = { reflectNavigationState = "menu" })
                                "journal" -> JournalScreen(onBack = { reflectNavigationState = "menu" })
                                "guided" -> GuidedReflectionScreen(onBack = { reflectNavigationState = "menu" })
                                "tracker" -> EmotionTrackerScreen(onBack = { reflectNavigationState = "menu" })
                            }
                        }
                        4 -> {
                            SupportScreen(onProfileClick = { showProfile = true })
                        }
                    }
                }
            }
        }
    }
}

// --- HOME CONTENT ---

@Composable
fun HomeContent(onNavigateToChat: () -> Unit, onProfileClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
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
                    text = "A . R . O . U . R . A",
                    style = MaterialTheme.typography.titleLarge,
                    color = OffWhite.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Light,
                    letterSpacing = 4.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "How are you feeling right now?",
            style = MaterialTheme.typography.bodyLarge,
            color = TextDarkSecondary,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Mood Tracker (Mock Visual)
        MoodTrackerBar()

        Spacer(modifier = Modifier.height(32.dp))

        // 3. Bento Grid Layout
        Row(
            modifier = Modifier.fillMaxWidth().height(340.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // LEFT COLUMN (Tall Card)
            BreathingCard(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )

            // RIGHT COLUMN (Stacked Cards)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DevotionalCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                GroundingCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Panic / Support Bar
        PanicSupportBar()

        Spacer(modifier = Modifier.height(100.dp)) // Bottom padding for nav bar
    }
}

@Composable
fun MoodTrackerBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(DeepSurface.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        // Track Line
        Canvas(modifier = Modifier.fillMaxWidth().height(4.dp).padding(horizontal = 16.dp)) {
            drawLine(
                color = TextDarkSecondary.copy(alpha = 0.2f),
                start = Offset(0f, center.y),
                end = Offset(size.width, center.y),
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
        }
        
        // Selected Indicator (Mock)
        Box(
            modifier = Modifier
                .padding(start = 12.dp)
                .size(36.dp)
                .background(OffWhite, CircleShape)
                .border(2.dp, MutedTeal.copy(alpha = 0.5f), CircleShape)
        )
        
        // Progress Text
        Row(
            modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star, 
                contentDescription = null, 
                tint = MutedTeal, 
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "3/3", color = TextDarkSecondary, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun BreathingCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1E2830), // Dark Blue Grey
                            Color(0xFF101418)  // Almost Black
                        )
                    )
                )
        ) {
            // Background Glow (Teal)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(150.dp)
                    .offset(x = (-40).dp, y = 40.dp)
                    // .blur(60.dp) // Removed blur for compatibility safety, using gradient brush instead if needed, but keeping simple for now
                    .background(MutedTeal.copy(alpha = 0.3f), CircleShape)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Text(
                    text = "CALM YOUR BREATH",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextDarkSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Follow your breath",
                    style = MaterialTheme.typography.titleMedium,
                    color = OffWhite,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Follow the breathing exercise.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextDarkSecondary.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                // Visual Breathing Circle
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(100.dp)) {
                        drawCircle(
                            color = MutedTeal.copy(alpha = 0.2f),
                            style = Stroke(width = 2.dp.toPx())
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(MutedTeal.copy(alpha = 0.6f), Color.Transparent)
                            ),
                            radius = 30.dp.toPx()
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Begin Button
                Button(
                    onClick = { /* TODO */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeepSurface.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MutedTeal
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Begin", color = OffWhite)
                }
            }
        }
    }
}

@Composable
fun DevotionalCard(modifier: Modifier = Modifier) {
    val devotionalGold = Color(0xFFFFB74D) // Warm Orange/Gold

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2D2420), // Dark Warm Grey
                            Color(0xFF181210)  // Darker Warm
                        )
                    )
                )
        ) {
            // Lamp Glow Effect
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(120.dp)
                    .offset(x = 20.dp, y = 20.dp)
                    // .blur(50.dp)
                    .background(devotionalGold.copy(alpha = 0.4f), CircleShape)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "DEVOTIONAL CALM",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextDarkSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Listen to devotional music",
                    style = MaterialTheme.typography.titleSmall,
                    color = OffWhite,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.weight(1f))

                // Small Action Chip
                Surface(
                    color = DeepSurface.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow, // Using PlayArrow as generic listen icon
                            contentDescription = null,
                            tint = devotionalGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Explore", color = OffWhite, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun GroundingCard(modifier: Modifier = Modifier) {
    val natureGreen = Color(0xFFA5D6A7)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF202824), // Dark Greenish Grey
                            Color(0xFF0F1412)
                        )
                    )
                )
        ) {
            // Stone/Nature Glow
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(100.dp)
                    .offset(x = 20.dp, y = 20.dp)
                    // .blur(40.dp)
                    .background(natureGreen.copy(alpha = 0.3f), CircleShape)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "GROUND YOURSELF",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextDarkSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Try a 5-4-3-2-1 grounding",
                    style = MaterialTheme.typography.titleSmall,
                    color = OffWhite,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.weight(1f))

                // Small Action Chip
                Surface(
                    color = DeepSurface.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite, // Using Heart/Favorite as nature/care
                            contentDescription = null,
                            tint = natureGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Start", color = OffWhite, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun PanicSupportBar() {
    val panicGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF4E342E), // Deep muted red/brown
            Color(0xFF3E2723)
        )
    )
    val buttonGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFD84315), // Orange/Red
            Color(0xFFFF8A65)  // Light Orange
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(panicGradient, RoundedCornerShape(24.dp))
            .border(1.dp, Color(0xFF5D4037), RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "PANIC BUTTON",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextDarkSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tap for quick help",
                    style = MaterialTheme.typography.bodyLarge,
                    color = OffWhite,
                    fontWeight = FontWeight.Normal
                )
            }

            Button(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .height(48.dp)
                    .width(160.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(buttonGradient, RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Get Support",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ArouraBottomNavigation(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    // Floating Nav Bar
    Surface(
        color = DeepSurface.copy(alpha = 0.9f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .clip(RoundedCornerShape(32.dp)),
        tonalElevation = 8.dp
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            modifier = Modifier.height(72.dp)
        ) {
            val items = listOf(
                NavItem("Home", Icons.Default.Home, Icons.Default.Home),
                NavItem("Chat", Icons.Default.FavoriteBorder, Icons.Default.FavoriteBorder), // Chat bubble
                NavItem("Calm", Icons.Default.ThumbUp, Icons.Default.ThumbUp), // Star/Calm
                NavItem("Reflect", Icons.Default.Edit, Icons.Default.Edit), // Pencil/Journal
                NavItem("Support", Icons.Default.Info, Icons.Default.Info) // Life ring
            )

            items.forEachIndexed { index, item ->
                val isSelected = selectedTab == index
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onTabSelected(index) },
                    icon = {
                        val iconColor = if (isSelected) OffWhite else TextDarkSecondary
                        Icon(
                            imageVector = item.selectedIcon,
                            contentDescription = item.title,
                            modifier = Modifier.size(24.dp),
                            tint = iconColor
                        )
                    },
                    label = {
                        if (isSelected) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.labelSmall,
                                color = OffWhite,
                                fontSize = 10.sp
                            )
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent // No pill indicator behind icon
                    ),
                    alwaysShowLabel = false
                )
            }
        }
    }
}

data class NavItem(val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector)