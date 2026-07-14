package com.example.pf_secondlife_client.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Blue40,
    onPrimary = White,
    primaryContainer = Blue90,
    onPrimaryContainer = Blue10,

    secondary = Orange40,
    onSecondary = White,
    secondaryContainer = Orange90,
    onSecondaryContainer = Orange10,

    tertiary = Orange40,
    onTertiary = White,
    tertiaryContainer = Orange90,
    onTertiaryContainer = Orange10,

    background = White,
    onBackground = Grey10,
    surface = White,
    onSurface = Grey10,
    surfaceVariant = Grey95,
    onSurfaceVariant = Grey20,

    error = ErrorRed40,
    onError = White,
)

private val DarkColorScheme = darkColorScheme(
    primary = Blue80,
    onPrimary = Blue20,
    primaryContainer = Blue20,
    onPrimaryContainer = Blue90,

    secondary = Orange80,
    onSecondary = Orange20,
    secondaryContainer = Orange20,
    onSecondaryContainer = Orange90,

    tertiary = Orange80,
    onTertiary = Orange20,
    tertiaryContainer = Orange20,
    onTertiaryContainer = Orange90,

    background = Grey10,
    onBackground = Grey90,
    surface = Grey10,
    onSurface = Grey90,
    surfaceVariant = Grey20,
    onSurfaceVariant = Grey90,

    error = ErrorRed80,
    onError = Grey20,
)

@Composable
fun PF_SecondLife_clientTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}