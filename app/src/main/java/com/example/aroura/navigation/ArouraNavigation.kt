package com.example.aroura.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.aroura.ui.screens.*

/**
 * Navigation Routes for A.R.O.U.R.A
 * Clean, type-safe navigation architecture
 */
sealed class Route(val path: String) {
    // Auth Flow
    data object Welcome : Route("welcome")
    data object Login : Route("login")
    
    // Main App (Bottom Navigation)
    data object Main : Route("main")
    
    // Profile Flow (Overlay)
    data object Profile : Route("profile")
    data object Language : Route("profile/language")
    data object Privacy : Route("profile/privacy")
    data object DevotionalPreferences : Route("profile/devotional")
    data object Ethics : Route("profile/ethics")
    
    // Overlays (Modal experiences)
    data object Breathing : Route("overlay/breathing")
    data object Grounding : Route("overlay/grounding")
    data object Panic : Route("overlay/panic")
    
    // Chat Flow
    data object ChatSelection : Route("chat/selection")
    data object Chat : Route("chat/{mode}") {
        fun createRoute(mode: String) = "chat/$mode"
    }
    
    // Calm Flow
    data object CalmPlayer : Route("calm/player/{itemId}") {
        fun createRoute(itemId: String) = "calm/player/$itemId"
    }
    data object AudioList : Route("calm/list/{title}")
    
    // Reflect Flow
    data object MoodCheckIn : Route("reflect/mood")
    data object Journal : Route("reflect/journal")
    data object VoiceJournal : Route("reflect/voice_journal")
    data object GuidedReflection : Route("reflect/guided")
    data object EmotionTracker : Route("reflect/tracker")
    
    // Support Flow
    data object Helplines : Route("support/helplines")
    data object Psychiatrist : Route("support/psychiatrist")
    data object TrustedContacts : Route("support/trusted")
    data object Emergency : Route("support/emergency")
}

/**
 * Premium transition specifications
 * Calm, intentional motion design
 */
object ArouraTransitions {
    
    // Fade + Slide for main navigation
    val enterTransition: EnterTransition
        @Composable get() = fadeIn(
            animationSpec = tween(
                durationMillis = 400,
                easing = FastOutSlowInEasing
            )
        ) + slideInHorizontally(
            initialOffsetX = { 60 },
            animationSpec = tween(
                durationMillis = 400,
                easing = FastOutSlowInEasing
            )
        )
    
    val exitTransition: ExitTransition
        @Composable get() = fadeOut(
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        )
    
    val popEnterTransition: EnterTransition
        @Composable get() = fadeIn(
            animationSpec = tween(
                durationMillis = 400,
                easing = FastOutSlowInEasing
            )
        ) + slideInHorizontally(
            initialOffsetX = { -60 },
            animationSpec = tween(
                durationMillis = 400,
                easing = FastOutSlowInEasing
            )
        )
    
    val popExitTransition: ExitTransition
        @Composable get() = fadeOut(
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        ) + slideOutHorizontally(
            targetOffsetX = { 60 },
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        )
    
    // Overlay transitions (bottom sheet / modal style)
    val overlayEnter: EnterTransition
        @Composable get() = fadeIn(
            animationSpec = tween(500, easing = FastOutSlowInEasing)
        ) + slideInVertically(
            initialOffsetY = { it / 4 },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    
    val overlayExit: ExitTransition
        @Composable get() = fadeOut(
            animationSpec = tween(300, easing = FastOutSlowInEasing)
        ) + slideOutVertically(
            targetOffsetY = { it / 4 },
            animationSpec = tween(300, easing = FastOutSlowInEasing)
        )
}

/**
 * Extension function to navigate with clear back stack
 */
fun NavHostController.navigateAndClear(route: String) {
    navigate(route) {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
    }
}

/**
 * Extension for single top navigation
 */
fun NavHostController.navigateSingleTop(route: String) {
    navigate(route) {
        launchSingleTop = true
    }
}
