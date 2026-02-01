package com.example.aroura.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * A.R.O.U.R.A Premium Color System
 * Calm, premium, emotionally safe color palette
 */

// ═══════════════════════════════════════════════════════════════════════════
// DARK THEME (PRIMARY)
// ═══════════════════════════════════════════════════════════════════════════

// Backgrounds
val MidnightCharcoal = Color(0xFF0D0F12)       // Deepest background
val DeepSurface = Color(0xFF151820)            // Card/surface background
val ElevatedSurface = Color(0xFF1C2028)        // Elevated cards

// Primary Accent - Muted Teal (calm, trustworthy)
val MutedTeal = Color(0xFF80CBC4)
val MutedTealDark = Color(0xFF4DB6AC)
val MutedTealLight = Color(0xFFB2DFDB)

// Secondary Accent - Soft Blue (serene, safe)
val SoftBlue = Color(0xFF90CAF9)
val SoftBlueDark = Color(0xFF64B5F6)
val SoftBlueLight = Color(0xFFBBDEFB)

// Text Colors
val OffWhite = Color(0xFFF5F7FA)               // Primary text
val TextDarkSecondary = Color(0xFF9AA5B4)      // Secondary text
val TextDarkTertiary = Color(0xFF5C6573)       // Tertiary/disabled text

// ═══════════════════════════════════════════════════════════════════════════
// LIGHT THEME (SECONDARY)
// ═══════════════════════════════════════════════════════════════════════════

val WarmBeige = Color(0xFFFDFBF7)
val LightSurface = Color(0xFFFFFDF5)
val PastelTeal = Color(0xFF4DB6AC)
val SoftTextPrimary = Color(0xFF37474F)
val SoftTextSecondary = Color(0xFF546E7A)

// ═══════════════════════════════════════════════════════════════════════════
// SEMANTIC COLORS
// ═══════════════════════════════════════════════════════════════════════════

// Calming Accents
val CalmingGreen = Color(0xFFA5D6A7)           // Nature, growth
val CalmingLavender = Color(0xFFCE93D8)        // Relaxation
val CalmingPeach = Color(0xFFFFCC80)           // Warmth
val CalmingBlue = Color(0xFF81D4FA)            // Serenity

// Error (Gentle, not alarming)
val GentleError = Color(0xFFEF9A9A)            // Soft red
val GentleErrorDark = Color(0xFFE57373)

// Panic Mode (Intentionally strong but calming)
val PanicRed = Color(0xFFD84315)
val PanicRedLight = Color(0xFFFF8A65)

// ═══════════════════════════════════════════════════════════════════════════
// AURORA BACKGROUND COLORS
// ═══════════════════════════════════════════════════════════════════════════

val AuroraDeepNight = Color(0xFF0A0D14)        // Deepest layer
val AuroraGreen = Color(0xFF00E676)            // Vibrant green bands
val AuroraTeal = Color(0xFF1DE9B6)             // Teal ribbons
val AuroraPurple = Color(0xFF651FFF)           // Purple glow
val AuroraBlue = Color(0xFF2979FF)             // Blue accents
val AuroraIndigo = Color(0xFF3949AB)           // Deep indigo base

// ═══════════════════════════════════════════════════════════════════════════
// GRADIENT PRESETS
// ═══════════════════════════════════════════════════════════════════════════

val PrimaryGradientColors = listOf(MutedTeal, SoftBlue)
val AuroraGradientColors = listOf(AuroraDeepNight, AuroraPurple.copy(alpha = 0.3f), AuroraBlue.copy(alpha = 0.2f))
val CardGradientColors = listOf(DeepSurface.copy(alpha = 0.8f), ElevatedSurface.copy(alpha = 0.6f))