package com.example.smriti.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Warm, accessible, calming nature-inspired palette (Forest & Emerald)
val ForestGreen = Color(0xFF1B4332)
val PineGreen = Color(0xFF2D6A4F)
val JadeGreen = Color(0xFF40916C)
val EmeraldGreen = Color(0xFF52B788)
val MintLight = Color(0xFF74C69D)
val MintPastel = Color(0xFFE8F5E9)

val SandBackground = Color(0xFFF4F6F4)
val SurfaceCard = Color(0xFFFFFFFF)
val TextDark = Color(0xFF1B4332)
val TextMuted = Color(0xFF4A5568)

val WarmAmber = Color(0xFFD97706)
val AmberBackground = Color(0xFFFFFBEB)
val AlertRed = Color(0xFFDC2626)
val AlertBackground = Color(0xFFFEF2F2)

val BlueSky = Color(0xFF2563EB)
val BlueBackground = Color(0xFFEFF6FF)

val SmritiTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        color = TextDark,
        lineHeight = 36.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        color = TextDark,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        color = TextDark,
        lineHeight = 24.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        color = TextDark,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        color = TextMuted,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    )
)
