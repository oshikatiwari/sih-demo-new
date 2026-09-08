package com.example.smriti.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PineGreen,
    onPrimary = Color.White,
    primaryContainer = MintPastel,
    onPrimaryContainer = ForestGreen,
    secondary = EmeraldGreen,
    onSecondary = Color.White,
    secondaryContainer = MintPastel,
    onSecondaryContainer = ForestGreen,
    tertiary = WarmAmber,
    onTertiary = Color.White,
    tertiaryContainer = AmberBackground,
    onTertiaryContainer = Color(0xFF78350F),
    background = SandBackground,
    onBackground = TextDark,
    surface = SurfaceCard,
    onSurface = TextDark,
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = TextMuted,
    error = AlertRed,
    errorContainer = AlertBackground,
    onError = Color.White,
    onErrorContainer = Color(0xFF7F1D1D)
)

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldGreen,
    onPrimary = Color.Black,
    primaryContainer = ForestGreen,
    onPrimaryContainer = Color.White,
    secondary = MintLight,
    onSecondary = Color.Black,
    background = Color(0xFF0F2419),
    onBackground = Color(0xFFE2E8F0),
    surface = Color(0xFF143022),
    onSurface = Color(0xFFF1F5F9)
)

@Composable
fun SmritiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SmritiTypography,
        content = content
    )
}
