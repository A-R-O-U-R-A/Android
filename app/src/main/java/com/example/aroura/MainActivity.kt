package com.example.aroura

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.aroura.ui.screens.HomeScreen
import com.example.aroura.ui.screens.LoginScreen
import com.example.aroura.ui.screens.WelcomeScreen
import com.example.aroura.ui.theme.ArouraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ArouraTheme {
                // Simple Navigation State
                var currentScreen by remember { mutableStateOf("welcome") }

                when (currentScreen) {
                    "welcome" -> WelcomeScreen(
                        onGetStarted = { currentScreen = "login" }
                    )
                    "login" -> LoginScreen(
                        onLoginSuccess = { currentScreen = "home" }
                    )
                    "home" -> HomeScreen(
                        onNavigateToChat = { /* The tab state inside HomeScreen will handle this */ }
                    )
                }
            }
        }
    }
}