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
import com.example.aroura.data.local.TokenManager
import com.example.aroura.data.repository.UserRepository
import com.example.aroura.ui.screens.HomeScreen
import com.example.aroura.ui.screens.LoginScreen
import com.example.aroura.ui.screens.WelcomeScreen
import com.example.aroura.ui.screens.calm.CalmAnxietyFlowScreen
import com.example.aroura.ui.screens.mood.MoodJournalFlowScreen
import com.example.aroura.ui.theme.ArouraTheme
import com.example.aroura.ui.viewmodels.AuthViewModel
import com.example.aroura.ui.viewmodels.AuthViewModelFactory
import com.example.aroura.ui.viewmodels.ProfileViewModel
import com.example.aroura.ui.viewmodels.ProfileViewModelFactory
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * A.R.O.U.R.A - Main Activity
 * 
 * Premium mental health companion app with:
 * - Calm, professional UI
 * - Smooth screen transitions  
 * - Proper authentication flow
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ArouraTheme {
                // Create TokenManager and repositories
                val tokenManager = remember { TokenManager(applicationContext) }
                val userApiService = remember { ApiClient.createUserApiService(tokenManager) }
                val userRepository = remember { UserRepository(userApiService, tokenManager, applicationContext) }
                
                // Create AuthViewModel with factory
                val authViewModel: AuthViewModel = viewModel(
                    factory = AuthViewModelFactory(application)
                )
                
                // Create ProfileViewModel with factory
                val profileViewModel: ProfileViewModel = viewModel(
                    factory = ProfileViewModelFactory(application, userRepository)
                )
                
                // Collect onboarding and login state
                val hasCompletedOnboarding by tokenManager.hasCompletedOnboardingFlow.collectAsState(initial = false)
                val isLoggedIn by tokenManager.isLoggedInFlow.collectAsState(initial = false)
                
                // Determine initial screen based on onboarding and login status
                // - First time install: show welcome
                // - Completed onboarding but logged out: show login
                // - Logged in: show home
                var currentScreen by remember { mutableStateOf("loading") }
                
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
                                    tokenManager.setOnboardingCompleted()
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
                            }
                        )
                        "calm_anxiety" -> CalmAnxietyFlowScreen(
                            onClose = { currentScreen = "home" },
                            onNavigateToBreathing = { 
                                // TODO: Navigate to breathing exercise
                                currentScreen = "home" 
                            },
                            onNavigateToChat = { 
                                // TODO: Navigate to chat
                                currentScreen = "home" 
                            }
                        )
                        "mood_journal" -> MoodJournalFlowScreen(
                            onClose = { currentScreen = "home" },
                            onSaveComplete = { currentScreen = "home" }
                        )
                    }
                }
            }
        }
    }
}