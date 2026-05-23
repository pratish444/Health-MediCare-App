package com.example.healthmedicareapp.ui.theme

import androidx.compose.runtime.compositionLocalOf

/**
 * Provides the current dark-mode state and a toggle lambda anywhere in the
 * composition tree, so any composable can read or flip the theme without
 * prop-drilling through every layer.
 */
data class DarkModeState(
    val isDark: Boolean = false,
    val toggle: () -> Unit = {}
)

val LocalDarkMode = compositionLocalOf { DarkModeState() }
