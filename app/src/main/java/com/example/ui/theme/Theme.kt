package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = CyanGlow,
    onPrimary = TacticalObsidian,
    primaryContainer = CyanSubtle,
    onPrimaryContainer = CyanGlow,
    secondary = TacticalAmber,
    onSecondary = TacticalObsidian,
    secondaryContainer = TacticalAmberDark,
    onSecondaryContainer = TextPrimary,
    tertiary = TacticalEmerald,
    onTertiary = TacticalObsidian,
    tertiaryContainer = TacticalEmeraldDark,
    onTertiaryContainer = TextPrimary,
    background = TacticalObsidian,
    onBackground = TextPrimary,
    surface = TacticalSurface,
    onSurface = TextPrimary,
    surfaceVariant = TacticalSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = TacticalBorder,
    outlineVariant = TacticalSurfaceElevated,
    error = AlertCrimson,
    onError = TextPrimary,
    errorContainer = AlertCrimsonDark,
    onErrorContainer = TextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Forced tactical dark scheme for AEGIS Command Center
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
