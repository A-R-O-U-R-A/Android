package com.example.aroura

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.aroura.ui.screens.HomeScreen
import com.example.aroura.ui.screens.LoginScreen
import com.example.aroura.ui.screens.WelcomeScreen
import com.example.aroura.ui.screens.calm.CalmAnxietyFlowScreen
import com.example.aroura.ui.screens.mood.MoodJournalFlowScreen
import com.example.aroura.ui.theme.ArouraTheme

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
                // Navigation state - starts at welcome for proper auth flow
                var currentScreen by remember { mutableStateOf("welcome") }

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
                        "welcome" -> WelcomeScreen(
                            onGetStarted = { currentScreen = "login" }
                        )
                        "login" -> LoginScreen(
                            onLoginSuccess = { currentScreen = "home" },
                            onBack = { currentScreen = "welcome" }
                        )
                        "home" -> HomeScreen(
                            onNavigateToChat = { 
                                // Logout - navigate back to welcome
                                currentScreen = "welcome"
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