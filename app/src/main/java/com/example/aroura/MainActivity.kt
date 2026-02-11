package com.example.aroura

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aroura.data.api.ApiClient
import com.example.aroura.data.local.PreferencesManager
import com.example.aroura.data.local.TokenManager
import com.example.aroura.data.repository.UserRepository
import com.example.aroura.ui.screens.HomeScreen
import com.example.aroura.ui.screens.LoginScreen
import com.example.aroura.ui.screens.SplashScreen
import com.example.aroura.ui.screens.WelcomeScreen
import com.example.aroura.ui.screens.calm.CalmAnxietyFlowScreen
import com.example.aroura.ui.screens.mood.MoodJournalFlowScreen
import com.example.aroura.ui.screens.QuizFlowScreen
import com.example.aroura.ui.screens.quest.SelfDiscoveryQuestScreen
import com.example.aroura.ui.theme.ArouraTheme
import com.example.aroura.ui.viewmodels.AuthViewModel
import com.example.aroura.ui.viewmodels.AuthViewModelFactory
import com.example.aroura.ui.viewmodels.HomeViewModel
import com.example.aroura.ui.viewmodels.HomeViewModelFactory
import com.example.aroura.ui.viewmodels.ProfileViewModel
import com.example.aroura.ui.viewmodels.ProfileViewModelFactory
import com.example.aroura.ui.viewmodels.ReflectViewModel
import com.example.aroura.ui.viewmodels.ReflectViewModelFactory
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * A.R.O.U.R.A - Main Activity
 * 
 * Premium mental health companion app with:
 * - Fast splash screen for quick perceived startup
 * - Calm, professional UI
 * - Smooth screen transitions  
 * - Proper authentication flow
 * 
 * OPTIMIZED: Lazy initialization of heavy components
 */
class MainActivity : ComponentActivity() {
    
    // Lazy initialization of managers/services outside compose
    private val tokenManager by lazy { TokenManager.getInstance(applicationContext) }
    private val preferencesManager by lazy { PreferencesManager(applicationContext) }
    private val userApiService by lazy { ApiClient.createUserApiService(tokenManager) }
    private val userRepository by lazy { UserRepository(userApiService, tokenManager, applicationContext) }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ArouraTheme {
                // Track splash state
                var showSplash by remember { mutableStateOf(true) }
                
                if (showSplash) {
                    SplashScreen(
                        onSplashComplete = { showSplash = false }
                    )
                } else {
                    // Run one-time migration from legacy DataStore
                    LaunchedEffect(Unit) {
                        tokenManager.migrateFromLegacyDataStoreIfNeeded()
                    }
                    
                    // Use pre-initialized lazy values
                    val localTokenManager = remember { tokenManager }
                    val localPreferencesManager = remember { preferencesManager }
                    val localUserRepository = remember { userRepository }
                    
                    // Create AuthViewModel with factory
                    val authViewModel: AuthViewModel = viewModel(
                        factory = AuthViewModelFactory(application)
                    )
                    
                    // Create ProfileViewModel with factory
                    val profileViewModel: ProfileViewModel = viewModel(
                        factory = ProfileViewModelFactory(application, localUserRepository)
                    )
                    
                    // Create HomeViewModel with factory
                    val homeViewModel: HomeViewModel = viewModel(
                        factory = HomeViewModelFactory(application)
                    )
                    
                    // Create ReflectViewModel with factory
                    val reflectViewModel: ReflectViewModel = viewModel(
                        factory = ReflectViewModelFactory(application)
                    )
                    
                    // Collect onboarding and login state
                    val hasCompletedOnboarding by localTokenManager.hasCompletedOnboardingFlow.collectAsState(initial = false)
                    val isLoggedIn by localTokenManager.isLoggedInFlow.collectAsState(initial = false)
                    
                    // Determine initial screen based on onboarding and login status
                    var currentScreen by remember { mutableStateOf("loading") }
                    var currentQuizId by remember { mutableStateOf("") }
                    
                    // Update screen based on auth state
                    LaunchedEffect(hasCompletedOnboarding, isLoggedIn) {
                        currentScreen = when {
                            !hasCompletedOnboarding -> "welcome"
                            isLoggedIn -> "home"
                            else -> "login"
                        }
                    }

                    // Smooth crossfade transition between screens
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            fadeIn(
                                animationSpec = tween(400, easing = EaseOutCubic)
                            ) togetherWith fadeOut(
                                animationSpec = tween(300, easing = EaseInCubic)
                            )
                        },
                        label = "screenTransition"
                    ) { screen ->
                        when (screen) {
                            "loading" -> {
                                // Empty loading state while we determine initial screen
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Optional: Add loading indicator
                                }
                            }
                            "welcome" -> WelcomeScreen(
                                onGetStarted = { 
                                    // Mark onboarding as complete
                                    MainScope().launch {
                                        localTokenManager.setOnboardingCompleted()
                                    }
                                    currentScreen = "login" 
                                }
                            )
                            "login" -> LoginScreen(
                                viewModel = authViewModel,
                                onLoginSuccess = { currentScreen = "home" },
                                onBack = { currentScreen = "welcome" }
                            )
                            "home" -> HomeScreen(
                                profileViewModel = profileViewModel,
                                homeViewModel = homeViewModel,
                                reflectViewModel = reflectViewModel,
                                preferencesManager = localPreferencesManager,
                                onNavigateToChat = { 
                                    // Logout - navigate back to login (not welcome)
                                    authViewModel.logout()
                                    currentScreen = "login"
                                },
                                onNavigateToCalmAnxiety = {
                                    currentScreen = "calm_anxiety"
                                },
                                onNavigateToMoodJournal = {
                                    currentScreen = "mood_journal"
                                },
                                onNavigateToQuiz = { quizId ->
                                    currentQuizId = quizId
                                    currentScreen = "quiz_flow"
                                },
                                onNavigateToSelfDiscoveryQuest = {
                                    currentScreen = "self_discovery_quest"
                                }
                            )
                            "calm_anxiety" -> CalmAnxietyFlowScreen(
                                onClose = { currentScreen = "home" },
                                onNavigateToBreathing = { 
                                    currentScreen = "home"
                                    // TODO: Navigate to breathing exercise
                                },
                                onNavigateToChat = { 
                                    currentScreen = "home"
                                    // TODO: Navigate to chat
                                },
                                onFlowComplete = {
                                    // Mark routine task as complete when anxiety flow is finished
                                    homeViewModel.completeRoutineTask("calm_anxiety", "Journaling", "Calm Your Anxiety")
                                }
                            )
                            "mood_journal" -> MoodJournalFlowScreen(
                                onClose = { currentScreen = "home" },
                                onSaveComplete = { 
                                    // Mark routine task as complete when mood is saved
                                    homeViewModel.completeRoutineTask("track_mood", "Journaling", "Track Your Mood")
                                    currentScreen = "home" 
                                }
                            )
                            "quiz_flow" -> QuizFlowScreen(
                                quizId = currentQuizId,
                                onClose = { currentScreen = "home" },
                                onSaveResult = { quizId, quizTitle, resultMessage, score ->
                                    // TODO: Save quiz result to database via ViewModel
                                    currentScreen = "home"
                                }
                            )
                            "self_discovery_quest" -> SelfDiscoveryQuestScreen(
                                onClose = { currentScreen = "home" },
                                onStartTest = { questId, testId ->
                                    // TODO: Navigate to specific test within quest
                                    // For now, just show the test library
                                    currentScreen = "home"
                                },
                                completedQuests = emptySet(), // TODO: Load from ViewModel
                                completedTests = emptySet()    // TODO: Load from ViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}