package com.example.aroura.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.draw.scale
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
import com.example.aroura.ui.components.ArouraBackground
import com.example.aroura.ui.components.ArouraProfileIcon
import com.example.aroura.ui.components.ArouraCard
import com.example.aroura.ui.components.ArouraSectionTitle
import com.example.aroura.ui.theme.*

/**
 * Home Screen - Main Entry Point
 * 
 * Premium redesign with:
 * - Consistent header with unified profile icon
 * - Calm morning/evening greeting
 * - Smooth animated navigation bar
 * - Premium card layouts
 * - Proper alignment throughout
 */
@Composable
fun HomeScreen(onNavigateToChat: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    
    // Navigation States
    var chatScreenState by remember { mutableStateOf("selection") }
    var chatMode by remember { mutableStateOf("Counselor") }
    
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

    // Helpers
    val navigateToChatTab = {
        selectedTab = 1
        chatScreenState = "selection"
    }
    
    val navigateToCalmTab = {
        selectedTab = 2
        calmNavigationState = "list"
    }

    val navigateToSupportTab = {
        selectedTab = 4
        supportNavigationState = "menu"
    }
    
    val openProfile = {
        showProfile = true
        profileNavigationState = "menu"
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ArouraBackground()

        if (showPanicScreen) {
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
                when (profileNavigationState) {
                    "menu" -> ProfileScreen(
                        onBack = { showProfile = false },
                        onNavigate = { dest -> profileNavigationState = dest },
                        onLogout = onNavigateToChat // This triggers navigation back to Welcome screen
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
                                if (it != 1) chatScreenState = "selection"
                                if (it != 2) calmNavigationState = "list"
                                if (it != 3) reflectNavigationState = "menu"
                                if (it != 4) supportNavigationState = "menu"
                            }
                        )
                    }
                }
            ) { paddingValues ->
                val bottomPadding = if (
                    (selectedTab == 1 && chatScreenState == "chatting") || 
                    (selectedTab == 2 && calmNavigationState == "player") ||
                    (selectedTab == 3 && reflectNavigationState != "menu") ||
                    (selectedTab == 4 && supportNavigationState != "menu")
                ) 0.dp else paddingValues.calculateBottomPadding()
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = bottomPadding)
                ) {
                    when(selectedTab) {
                        0 -> HomeContent(
                            onNavigateToChat = navigateToChatTab, 
                            onNavigateToCalm = navigateToCalmTab,
                            onNavigateToSupport = navigateToSupportTab,
                            onOpenBreathing = { showBreathingScreen = true },
                            onOpenGrounding = { showGroundingScreen = true },
                            onOpenPanic = { showPanicScreen = true },
                            onProfileClick = openProfile
                        ) 
                        1 -> {
                            if (chatScreenState == "selection") {
                                ChatSelectionScreen(
                                    onChatSelected = { mode -> 
                                        chatMode = mode
                                        chatScreenState = "chatting" 
                                    },
                                    onProfileClick = openProfile
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
                                    onProfileClick = openProfile
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
                                    onProfileClick = openProfile
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
                                    onProfileClick = openProfile,
                                    onNavigate = { dest -> supportNavigationState = dest },
                                    onOpenPanic = { showPanicScreen = true }
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

// ═══════════════════════════════════════════════════════════════════════════════
// HOME CONTENT - Main scrollable content
// ═══════════════════════════════════════════════════════════════════════════════

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
            .padding(horizontal = ArouraSpacing.screenHorizontal.dp)
            .systemBarsPadding()
    ) {
        Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
        
        // ═══════════════════════════════════════════════════════════════════════
        // HEADER - Greeting + Profile
        // ═══════════════════════════════════════════════════════════════════════
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = getGreeting(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextDarkSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Sarah",
                    style = MaterialTheme.typography.headlineMedium,
                    color = OffWhite,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            ArouraProfileIcon(onClick = onProfileClick)
        }
        
        Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
        
        // ═══════════════════════════════════════════════════════════════════════
        // MOOD CHECK-IN
        // ═══════════════════════════════════════════════════════════════════════
        
        ArouraSectionTitle(text = "How are you feeling?")
        
        Spacer(modifier = Modifier.height(ArouraSpacing.md.dp))
        
        PremiumMoodSelector()
        
        Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
        
        // ═══════════════════════════════════════════════════════════════════════
        // FEATURED: CHAT WITH AROURA
        // ═══════════════════════════════════════════════════════════════════════
        
        PremiumChatCard(onClick = onNavigateToChat)
        
        Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
        
        // ═══════════════════════════════════════════════════════════════════════
        // QUICK ACTIONS GRID
        // ═══════════════════════════════════════════════════════════════════════
        
        ArouraSectionTitle(text = "Quick Relief")
        
        Spacer(modifier = Modifier.height(ArouraSpacing.md.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp),
            horizontalArrangement = Arrangement.spacedBy(ArouraSpacing.md.dp)
        ) {
            // Left Column
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(ArouraSpacing.md.dp)
            ) {
                PremiumQuickCard(
                    title = "Breathe",
                    subtitle = "Relax now",
                    icon = Icons.Default.FavoriteBorder,
                    accentColor = MutedTeal,
                    modifier = Modifier.weight(1.4f),
                    onClick = onOpenBreathing
                )
                PremiumQuickCard(
                    title = "Sounds",
                    subtitle = "Nature & Ambient",
                    icon = Icons.Default.PlayArrow,
                    accentColor = CalmingLavender,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToCalm
                )
            }
            
            // Right Column
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(ArouraSpacing.md.dp)
            ) {
                PremiumQuickCard(
                    title = "Panic",
                    subtitle = "Emergency",
                    icon = Icons.Default.Warning,
                    accentColor = GentleError,
                    modifier = Modifier.weight(0.8f),
                    onClick = onOpenPanic
                )
                PremiumQuickCard(
                    title = "Grounding",
                    subtitle = "5-4-3-2-1 Tool",
                    icon = Icons.Default.LocationOn,
                    accentColor = CalmingGreen,
                    modifier = Modifier.weight(1.2f),
                    onClick = onOpenGrounding
                )
                PremiumQuickCard(
                    title = "Music",
                    subtitle = "Soothing",
                    icon = Icons.Default.Star,
                    accentColor = CalmingPeach,
                    modifier = Modifier.weight(0.8f),
                    onClick = onNavigateToCalm
                )
            }
        }
        
        Spacer(modifier = Modifier.height(120.dp))
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// HELPER FUNCTIONS
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun getGreeting(): String {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Good Morning,"
        hour < 17 -> "Good Afternoon,"
        else -> "Good Evening,"
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// PREMIUM COMPONENTS
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun PremiumMoodSelector() {
    val moods = listOf("😔", "😐", "🙂", "😊", "🤩")
    val moodLabels = listOf("Sad", "Meh", "Okay", "Good", "Great")
    var selectedMood by remember { mutableIntStateOf(-1) }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        moods.forEachIndexed { index, mood ->
            val isSelected = selectedMood == index
            
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.15f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "moodScale$index"
            )
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) MutedTeal.copy(alpha = 0.2f)
                            else DeepSurface.copy(alpha = 0.5f)
                        )
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) MutedTeal else Color.White.copy(alpha = 0.05f),
                            shape = CircleShape
                        )
                        .clickable { selectedMood = index },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = mood, fontSize = 24.sp)
                }
                
                AnimatedVisibility(visible = isSelected) {
                    Text(
                        text = moodLabels[index],
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedTeal,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PremiumChatCard(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "chatCardScale"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(ArouraSpacing.cardRadius.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF1E2835),
                            Color(0xFF151A22)
                        )
                    )
                )
                .border(
                    1.dp,
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.1f),
                            Color.White.copy(alpha = 0.02f)
                        )
                    ),
                    RoundedCornerShape(ArouraSpacing.cardRadius.dp)
                )
        ) {
            // Glow accent
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = 40.dp)
                    .size(120.dp)
                    .background(SoftBlue.copy(alpha = 0.15f), CircleShape)
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(ArouraSpacing.cardPadding.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    SoftBlue.copy(alpha = 0.25f),
                                    MutedTeal.copy(alpha = 0.15f)
                                )
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = SoftBlue
                    )
                }
                
                Spacer(modifier = Modifier.width(ArouraSpacing.md.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Talk to Aroura",
                        style = MaterialTheme.typography.titleLarge,
                        color = OffWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "I'm here to listen, anytime.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextDarkSecondary
                    )
                }
                
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = TextDarkSecondary
                )
            }
        }
    }
}

@Composable
private fun PremiumQuickCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "quickCardScale"
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(ArouraSpacing.cardRadius.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.12f),
                            accentColor.copy(alpha = 0.04f)
                        )
                    )
                )
                .border(
                    1.dp,
                    accentColor.copy(alpha = 0.15f),
                    RoundedCornerShape(ArouraSpacing.cardRadius.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(ArouraSpacing.md.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(accentColor.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
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
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// BOTTOM NAVIGATION - Premium animated navigation bar
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun ArouraBottomNavigation(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val items = listOf(
        NavItem("Home", Icons.Default.Home, Icons.Default.Home),
        NavItem("Chat", Icons.Default.FavoriteBorder, Icons.Default.FavoriteBorder),
        NavItem("Calm", Icons.Default.ThumbUp, Icons.Default.ThumbUp),
        NavItem("Reflect", Icons.Default.Edit, Icons.Default.Edit),
        NavItem("Support", Icons.Default.Info, Icons.Default.Info)
    )
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        shape = RoundedCornerShape(28.dp),
        color = DeepSurface.copy(alpha = 0.95f),
        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.08f),
                    Color.White.copy(alpha = 0.02f)
                )
            )
        ),
        tonalElevation = 0.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = selectedTab == index
                
                NavBarItem(
                    item = item,
                    isSelected = isSelected,
                    onClick = { onTabSelected(index) }
                )
            }
        }
    }
}

@Composable
private fun NavBarItem(
    item: NavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "navScale"
    )
    
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) OffWhite else TextDarkSecondary,
        animationSpec = tween(300),
        label = "navColor"
    )
    
    Column(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = item.selectedIcon,
            contentDescription = item.title,
            modifier = Modifier.size(24.dp),
            tint = iconColor
        )
        
        AnimatedVisibility(
            visible = isSelected,
            enter = fadeIn(tween(300)) + expandVertically(),
            exit = fadeOut(tween(200)) + shrinkVertically()
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.labelSmall,
                color = OffWhite,
                fontSize = 10.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

data class NavItem(val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector)