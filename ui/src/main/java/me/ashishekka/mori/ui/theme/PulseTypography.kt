package me.ashishekka.mori.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import me.ashishekka.mori.ui.R

/**
 * Custom font family for the Pulse Design System.
 * Uses Satoshi for a clean, modern, and atmospheric feel.
 * Referenced via pulse_font_family.xml for correct system resolution.
 */
val PulseFontFamily = FontFamily(
    Font(R.font.pulse_font_family)
)

/**
 * The core typography system for Mori.
 * Prioritizes legibility while maintaining an atmospheric aesthetic.
 */
val PulseTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = PulseFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = PulseFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = PulseFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = PulseFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = PulseFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
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
