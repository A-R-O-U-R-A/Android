package com.example.aroura.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.aroura.ui.components.AdvancedAuroraBackground
import com.example.aroura.ui.theme.*

@Composable
fun HomeScreen(onNavigateToChat: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    
    // Navigation States
    var chatScreenState by remember { mutableStateOf("selection") }
    var chatMode by remember { mutableStateOf("Counselor") } // Added State
    
    // Calm States
    var calmNavigationState by remember { mutableStateOf("list") }
    var currentMediaItem by remember { mutableStateOf<CalmMediaItem?>(null) }
    var currentAudioListTitle by remember { mutableStateOf("") }
    var currentAudioList by remember { mutableStateOf<List<CalmMediaItem>>(emptyList()) }
    
    // Reflect States
    var reflectNavigationState by remember { mutableStateOf("menu") }
    
    // Support States
    var supportNavigationState by remember { mutableStateOf("menu") }
    
    // Profile States
    var showProfile by remember { mutableStateOf(false) }
    var profileNavigationState by remember { mutableStateOf("menu") }

    // Home Overlays
    var showBreathingScreen by remember { mutableStateOf(false) }
    var showGroundingScreen by remember { mutableStateOf(false) }
    var showPanicScreen by remember { mutableStateOf(false) }

    // Helper to switch to chat tab
    val navigateToChatTab = {
        selectedTab = 1
        chatScreenState = "selection"
    }
    
    // Helper to switch to calm tab
    val navigateToCalmTab = {
        selectedTab = 2
        calmNavigationState = "list"
    }

    // Helper to switch to support tab (for panic)
    val navigateToSupportTab = {
        selectedTab = 4
        supportNavigationState = "menu"
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AdvancedAuroraBackground()

        if (showPanicScreen) {
             // Fullscreen Panic Overlay (Highest Priority)
             Box(modifier = Modifier.fillMaxSize().zIndex(10f)) {
                 PanicScreen(
                     onClose = { showPanicScreen = false },
                     onNavigateToBreathing = {
                         showPanicScreen = false
                         showBreathingScreen = true
                     }
                 )
             }
        } else if (showBreathingScreen) {
             Box(modifier = Modifier.fillMaxSize().zIndex(9f)) {
                 BreathingScreen(onClose = { showBreathingScreen = false })
             }
        } else if (showGroundingScreen) {
            Box(modifier = Modifier.fillMaxSize().zIndex(9f)) {
                GroundingScreen(onBack = { showGroundingScreen = false })
            }
        } else if (showProfile) {
            Box(modifier = Modifier.fillMaxSize().zIndex(8f)) {
                // Profile Navigation
                when (profileNavigationState) {
                    "menu" -> ProfileScreen(
                        onBack = { showProfile = false },
                        onNavigate = { dest -> profileNavigationState = dest }
                    )
                    "language" -> LanguageScreen(onBack = { profileNavigationState = "menu" })
                    "privacy" -> PrivacyScreen(onBack = { profileNavigationState = "menu" })
                    "devotional" -> DevotionalPreferencesScreen(onBack = { profileNavigationState = "menu" })
                    "ethics" -> EthicsScreen(onBack = { profileNavigationState = "menu" })
                }
            }
        } else {
            Scaffold(
                containerColor = Color.Transparent,
                bottomBar = {
                    val hideBottomBar = (selectedTab == 1 && chatScreenState == "chatting") ||
                                        (selectedTab == 2 && calmNavigationState == "player") ||
                                        (selectedTab == 3 && reflectNavigationState != "menu") ||
                                        (selectedTab == 4 && supportNavigationState != "menu")
                    
                    if (!hideBottomBar) {
                        ArouraBottomNavigation(
                            selectedTab = selectedTab,
                            onTabSelected = { 
                                selectedTab = it
                                // Reset states when switching tabs
                                if (it != 1) chatScreenState = "selection"
                                if (it != 2) calmNavigationState = "list"
                                if (it != 3) reflectNavigationState = "menu"
                                if (it != 4) supportNavigationState = "menu"
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
                                (selectedTab == 3 && reflectNavigationState != "menu") ||
                                (selectedTab == 4 && supportNavigationState != "menu")
                            ) 0.dp else paddingValues.calculateBottomPadding()
                        )
                ) {
                    when(selectedTab) {
                        0 -> HomeContent(
                            onNavigateToChat = navigateToChatTab, 
                            onNavigateToCalm = navigateToCalmTab,
                            onNavigateToSupport = navigateToSupportTab,
                            onOpenBreathing = { showBreathingScreen = true },
                            onOpenGrounding = { showGroundingScreen = true },
                            onOpenPanic = { showPanicScreen = true },
                            onProfileClick = { 
                                showProfile = true 
                                profileNavigationState = "menu"
                            }
                        ) 
                        1 -> {
                            if (chatScreenState == "selection") {
                                ChatSelectionScreen(
                                    onChatSelected = { mode -> 
                                        chatMode = mode
                                        chatScreenState = "chatting" 
                                    },
                                    onProfileClick = { showProfile = true; profileNavigationState = "menu" }
                                )
                            } else {
                                ChatScreen(
                                    mode = chatMode,
                                    onBack = { chatScreenState = "selection" }
                                )
                            }
                        }
                        2 -> {
                            when (calmNavigationState) {
                                "list" -> CalmScreen(
                                    onItemClick = { item ->
                                        currentMediaItem = item
                                        calmNavigationState = "player"
                                    },
                                    onViewAllClick = { title, items ->
                                        currentAudioListTitle = title
                                        currentAudioList = items
                                        calmNavigationState = "audio_list"
                                    },
                                    onProfileClick = { showProfile = true; profileNavigationState = "menu" }
                                )
                                "player" -> currentMediaItem?.let { item ->
                                    CalmPlayerScreen(
                                        item = item,
                                        onBack = { calmNavigationState = "list" }
                                    )
                                }
                                "audio_list" -> AudioListScreen(
                                    title = currentAudioListTitle,
                                    items = currentAudioList,
                                    onItemClick = { item ->
                                        currentMediaItem = item
                                        calmNavigationState = "player"
                                    },
                                    onBack = { calmNavigationState = "list" }
                                )
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
                                    onProfileClick = { showProfile = true; profileNavigationState = "menu" }
                                )
                                "mood" -> MoodCheckInScreen(onBack = { reflectNavigationState = "menu" })
                                "journal" -> JournalScreen(
                                    onBack = { reflectNavigationState = "menu" },
                                    onNavigateToVoice = { reflectNavigationState = "voice_journal" }
                                )
                                "voice_journal" -> VoiceJournalScreen(onBack = { reflectNavigationState = "journal" })
                                "guided" -> GuidedReflectionScreen(onBack = { reflectNavigationState = "menu" })
                                "tracker" -> EmotionTrackerScreen(onBack = { reflectNavigationState = "menu" })
                            }
                        }
                        4 -> {
                            when (supportNavigationState) {
                                "menu" -> SupportScreen(
                                    onProfileClick = { showProfile = true; profileNavigationState = "menu" },
                                    onNavigate = { dest -> supportNavigationState = dest },
                                    onOpenPanic = { showPanicScreen = true } // Add direct panic access from Support
                                )
                                "helplines" -> HelplineScreen(onBack = { supportNavigationState = "menu" })
                                "psychiatrist" -> PsychiatristContactScreen(onBack = { supportNavigationState = "menu" })
                                "trusted" -> TrustedContactsScreen(onBack = { supportNavigationState = "menu" })
                                "emergency" -> EmergencyResourcesScreen(onBack = { supportNavigationState = "menu" })
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- HOME CONTENT ---

@Composable
fun HomeContent(
    onNavigateToChat: () -> Unit, 
    onNavigateToCalm: () -> Unit,
    onNavigateToSupport: () -> Unit,
    onOpenBreathing: () -> Unit,
    onOpenGrounding: () -> Unit,
    onOpenPanic: () -> Unit,
    onProfileClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Good Morning,", // Dynamic greeting could go here
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextDarkSecondary
                )
                Text(
                    text = "Sarah", // Dynamic Name
                    style = MaterialTheme.typography.headlineMedium,
                    color = OffWhite,
                    fontWeight = FontWeight.SemiBold
                )
            }
            IconButton(onClick = onProfileClick) {
                 Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(DeepSurface, CircleShape)
                        .border(1.dp, MutedTeal.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, "Profile", tint = OffWhite, modifier = Modifier.size(20.dp))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Mood Check-in
        Text(
            text = "How are you feeling?",
            style = MaterialTheme.typography.titleMedium,
            color = OffWhite,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        MoodSelector()

        Spacer(modifier = Modifier.height(32.dp))

        // --- MAIN ACTIONS GRID ---
        
        // 1. Instant Chat (Featured)
        FeaturedChatCard(onClick = onNavigateToChat)
        
        Spacer(modifier = Modifier.height(16.dp))

        // 2. Bento Grid
        Row(
            modifier = Modifier.fillMaxWidth().height(280.dp), // Fixed height for grid alignment
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left Column (Breathing + Music)
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Breathing (Tall)
                HomeCard(
                    title = "Breathe",
                    subtitle = "Relax now",
                    icon = Icons.Default.FavoriteBorder, // Heart/Lungs
                    color = Color(0xFF80CBC4), // Muted Teal
                    modifier = Modifier.weight(1.5f),
                    onClick = onOpenBreathing
                )
                // Music (Short)
                HomeCard(
                    title = "Sounds",
                    subtitle = "Nature & Ambient",
                    icon = Icons.Default.PlayArrow,
                    color = Color(0xFF9FA8DA), // Soft Indigo
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToCalm
                )
            }
            
            // Right Column (Panic + Grounding)
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Panic (Short) - High priority visual but small footprint
                HomeCard(
                    title = "Panic",
                    subtitle = "Emergency",
                    icon = Icons.Default.Warning,
                    color = Color(0xFFEF9A9A), // Soft Red
                    modifier = Modifier.weight(0.8f),
                    onClick = onOpenPanic
                )
                
                // Grounding (Tall)
                HomeCard(
                    title = "Grounding",
                    subtitle = "5-4-3-2-1 Tool",
                    icon = Icons.Default.LocationOn, // Anchor/Place
                    color = Color(0xFFA5D6A7), // Soft Green
                    modifier = Modifier.weight(1.2f),
                    onClick = onOpenGrounding
                )
                 // Calm Music (Short)
                HomeCard(
                    title = "Music",
                    subtitle = "Soothing",
                    icon = Icons.Default.Star,
                    color = Color(0xFFCE93D8), // Lavender
                    modifier = Modifier.weight(0.8f),
                    onClick = onNavigateToCalm
                )
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun FeaturedChatCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF2E3B4E), // Dark Blue Grey
                            Color(0xFF21252B)
                        )
                    )
                )
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
        ) {
             // Glow
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = 30.dp)
                    .size(100.dp)
                    .background(SoftBlue.copy(alpha = 0.2f), CircleShape)
            )

            Row(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(SoftBlue.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                     Icon(
                        imageVector = Icons.Default.Email, // Chat Icon
                        contentDescription = null,
                        tint = SoftBlue
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = "Talk to Aroura",
                        style = MaterialTheme.typography.titleMedium,
                        color = OffWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "I'm here to listen.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextDarkSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun HomeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            color.copy(alpha = 0.15f),
                            color.copy(alpha = 0.05f)
                        )
                    )
                )
                .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(color.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        color = OffWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextDarkSecondary,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MoodSelector() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val moods = listOf(
            "😔", "😐", "🙂", "😊", "🤩"
        )
        
        moods.forEach { mood ->
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(DeepSurface.copy(alpha = 0.5f), CircleShape)
                    .border(1.dp, Color.White.copy(alpha = 0.05f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = mood, fontSize = 24.sp)
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