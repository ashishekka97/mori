package me.ashishekka.mori.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontLoadingStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import me.ashishekka.mori.ui.R

/**
 * Custom font family for the Pulse Design System.
 * Uses Satoshi for a clean, modern, and atmospheric feel.
 */
val PulseFontFamily = FontFamily(
    Font(resId = R.font.satoshi_light, weight = FontWeight.Light, loadingStrategy = FontLoadingStrategy.Async),
    Font(resId = R.font.satoshi_regular, weight = FontWeight.Normal, loadingStrategy = FontLoadingStrategy.Async),
    Font(resId = R.font.satoshi_medium, weight = FontWeight.Medium, loadingStrategy = FontLoadingStrategy.Async),
    Font(resId = R.font.satoshi_bold, weight = FontWeight.Bold, loadingStrategy = FontLoadingStrategy.Async),
    Font(resId = R.font.satoshi_black, weight = FontWeight.Black, loadingStrategy = FontLoadingStrategy.Async)
)

/**
 * The core typography system for Pulse.
 * Encodes brand-specific traits like high letter-spacing and varied weights.
 */
val PulseTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = PulseFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 8.sp // Brand signature
    ),
    headlineMedium = TextStyle(
        fontFamily = PulseFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 2.sp
    ),
    titleLarge = TextStyle(
        fontFamily = PulseFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 1.5.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = PulseFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    labelLarge = TextStyle(
        fontFamily = PulseFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 1.sp
    ),
    labelSmall = TextStyle(
        fontFamily = PulseFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 2.sp // Metadata signature
    )
)

/**
 * Extension properties for easy access to Pulse-specific text styles.
 */
val Typography.pulseHeadline: TextStyle
    @Composable
    get() = MaterialTheme.typography.headlineMedium.copy(
        color = PulseTheme.colors.onSurface
    )

val Typography.pulseBody: TextStyle
    @Composable
    get() = MaterialTheme.typography.bodyLarge.copy(
        color = PulseTheme.colors.onSurface
    )

val PulseTheme.typography: Typography
    get() = PulseTypography
