package com.runanywhere.startup_hackathon20.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme


import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = cyan_500,
    onPrimary = dark_blue_900,
    primaryContainer = dark_blue_700,
    onPrimaryContainer = cyan_300,
    secondary = cyan_accent,
    onSecondary = dark_blue_900,
    secondaryContainer = cyan_700,
    onSecondaryContainer = white,
    background = dark_blue_900,
    onBackground = cyan_300,
    surface = dark_blue_700,
    onSurface = cyan_300,
    surfaceVariant = dark_blue_500,
    onSurfaceVariant = cyan_300
)

private val LightColorScheme = lightColorScheme(
    primary = dark_blue_500,
    onPrimary = white,
    primaryContainer = cyan_300,
    onPrimaryContainer = dark_blue_900,
    secondary = cyan_700,
    onSecondary = white,
    secondaryContainer = cyan_500,
    onSecondaryContainer = dark_blue_900,
    background = white,
    onBackground = dark_blue_900,
    surface = cyan_300,
    onSurface = dark_blue_900,
    surfaceVariant = cyan_500,
    onSurfaceVariant = dark_blue_900
)

@Composable
fun EventPlannerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
