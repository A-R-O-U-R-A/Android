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
import com.example.aroura.ui.components.home.*
import com.example.aroura.ui.theme.*
import com.example.aroura.data.ReflectTestId
import com.example.aroura.ui.screens.reflect.ReflectLibraryScreen
import com.example.aroura.ui.screens.reflect.TestFlowScreen

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
fun HomeScreen(
    onNavigateToChat: () -> Unit,
    onNavigateToCalmAnxiety: () -> Unit = {},
    onNavigateToMoodJournal: () -> Unit = {}
) {
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
    var currentTestId by remember { mutableStateOf<ReflectTestId?>(null) }
    var completedTests by remember { mutableStateOf(setOf<ReflectTestId>()) }
    
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
                            onProfileClick = openProfile,
                            onNavigateToCalmAnxiety = onNavigateToCalmAnxiety,
                            onNavigateToMoodJournal = onNavigateToMoodJournal
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
                                            ReflectOption.Assessments -> "library"
                                        }
                                    },
                                    onProfileClick = openProfile
                                )
                                "library" -> ReflectLibraryScreen(
                                    completedTests = completedTests,
                                    onBack = { reflectNavigationState = "menu" },
                                    onTestClick = { testId ->
                                        currentTestId = testId
                                        reflectNavigationState = "test_flow"
                                    }
                                )
                                "test_flow" -> currentTestId?.let { testId ->
                                    TestFlowScreen(
                                        testId = testId,
                                        onBack = { reflectNavigationState = "library" },
                                        onComplete = { result ->
                                            completedTests = completedTests + result.testId
                                        }
                                    )
                                } ?: run { reflectNavigationState = "library" }
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
// HOME CONTENT - Complete Redesigned Experience
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun HomeContent(
    onNavigateToChat: () -> Unit, 
    onNavigateToCalm: () -> Unit,
    onNavigateToSupport: () -> Unit,
    onOpenBreathing: () -> Unit,
    onOpenGrounding: () -> Unit,
    onOpenPanic: () -> Unit,
    onProfileClick: () -> Unit,
    onNavigateToCalmAnxiety: () -> Unit = {},
    onNavigateToMoodJournal: () -> Unit = {}
) {
    // State for all sections
    var selectedMood by remember { mutableIntStateOf(-1) }
    var selectedDay by remember { mutableIntStateOf(java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK) - 2) }
    
    // Sample routine tasks
    val routineTasks = remember {
        listOf(
            RoutineTask("1", "Journaling", "Track Your Mood", false, CalmingLavender),
            RoutineTask("2", "Journaling", "Calm Your Anxiety", false, SoftBlue),
            RoutineTask("3", "Mindfulness", "5-Minute Breathing", true, MutedTeal)
        )
    }
    
    // Sample test previews
    val testPreviews = remember {
        listOf(
            TestPreview("1", "Personality", "🧬", CalmingLavender, false),
            TestPreview("2", "Childhood", "👶", CalmingPeach, false),
            TestPreview("3", "Love Language", "❤️", GentleError.copy(alpha = 0.8f), false),
            TestPreview("4", "Attachment", "🔗", MutedTeal, false),
            TestPreview("5", "Emotional IQ", "💜", SoftBlue, false)
        )
    }
    
    // Sample quizzes
    val quizzes = remember {
        listOf(
            QuizPreview("1", "What's Your Superpower?", "✨", CalmingPeach),
            QuizPreview("2", "Self-Care Style", "🌿", CalmingGreen),
            QuizPreview("3", "Your Inner Animal", "🦋", CalmingLavender)
        )
    }
    
    // Daily affirmation
    val affirmation = "You are allowed to be exactly who you are."
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = ArouraSpacing.screenHorizontal.dp)
            .systemBarsPadding()
    ) {
        Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
        
        // ═══════════════════════════════════════════════════════════════════════
        // SECTION 1: GREETING & EMOTIONAL CHECK-IN
        // ═══════════════════════════════════════════════════════════════════════
        
        GreetingSection(
            userName = "Sarah",
            onProfileClick = onProfileClick
        )
        
        Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
        
        EmotionalCheckInSection(
            selectedMood = selectedMood,
            onMoodSelected = { selectedMood = it }
        )
        
        Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
        
        // ═══════════════════════════════════════════════════════════════════════
        // SECTION 2: PRIMARY CTA - TALK TO AROURA
        // ═══════════════════════════════════════════════════════════════════════
        
        TalkToArouraCard(onClick = onNavigateToChat)
        
        Spacer(modifier = Modifier.height(ArouraSpacing.lg.dp))
        
        // ═══════════════════════════════════════════════════════════════════════
        // SECTION 3: FIND A SPECIALIST
        // ═══════════════════════════════════════════════════════════════════════
        
        FindSpecialistCard(onClick = onNavigateToSupport)
        
        Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
        
        // ═══════════════════════════════════════════════════════════════════════
        // SECTION 4: DAILY AFFIRMATION
        // ═══════════════════════════════════════════════════════════════════════
        
        DailyAffirmationCard(
            affirmation = affirmation,
            onShare = { /* Share functionality */ }
        )
        
        Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
        
        // ═══════════════════════════════════════════════════════════════════════
        // SECTION 5: SPONSORED / OPPORTUNITIES
        // ═══════════════════════════════════════════════════════════════════════
        
        SponsoredSection(
            title = "BetterHelp Therapy",
            description = "Professional counseling, on your schedule",
            ctaText = "Learn More",
            onClick = { }
        )
        
        Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
        
        // ═══════════════════════════════════════════════════════════════════════
        // SECTION 6: YOUR ROUTINE (NO PETAL GAME)
        // ═══════════════════════════════════════════════════════════════════════
        
        YourRoutineSection(
            selectedDay = selectedDay.coerceIn(0, 6),
            onDaySelected = { selectedDay = it },
            tasks = routineTasks,
            onTaskClick = { task ->
                // Navigate to appropriate screen based on task
                when (task.title) {
                    "Track Your Mood" -> onNavigateToMoodJournal()
                    "Calm Your Anxiety" -> onNavigateToCalmAnxiety()
                    "5-Minute Breathing" -> onOpenBreathing()
                    else -> { /* Other tasks */ }
                }
            }
        )
        
        Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
        
        // ═══════════════════════════════════════════════════════════════════════
        // SECTION 7: SELF-DISCOVERY QUEST
        // ═══════════════════════════════════════════════════════════════════════
        
        SelfDiscoveryQuestCard(
            progress = 0,
            total = 3,
            onClick = { /* Navigate to tests */ }
        )
        
        Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
        
        // ═══════════════════════════════════════════════════════════════════════
        // SECTION 8: TEST RESULTS OVERVIEW
        // ═══════════════════════════════════════════════════════════════════════
        
        TestResultsSection(
            completedCount = 0,
            totalTests = 37,
            tests = testPreviews,
            onTestClick = { /* Navigate to test */ },
            onViewAll = { /* Navigate to all tests */ }
        )
        
        Spacer(modifier = Modifier.height(ArouraSpacing.xl.dp))
        
        // ═══════════════════════════════════════════════════════════════════════
        // SECTION 9: UPLIFTING QUIZZES
        // ═══════════════════════════════════════════════════════════════════════
        
        UpliftingQuizzesSection(
            quizzes = quizzes,
            onQuizClick = { /* Navigate to quiz */ }
        )
        
        // Bottom padding for navigation bar
        Spacer(modifier = Modifier.height(120.dp))
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