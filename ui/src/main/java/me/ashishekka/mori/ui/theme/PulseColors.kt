package me.ashishekka.mori.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import me.ashishekka.mori.persona.state.WorldState
import me.ashishekka.mori.ui.theme.util.ColorUtils

/**
 * A reactive set of color tokens that shift based on the [WorldState].
 */
@Immutable
data class PulseColors(
    val accent: Color,
    val surface: Color,
    val onSurface: Color,
    val isDark: Boolean
)

/**
 * CompositionLocal used to provide [PulseColors] throughout the UI tree.
 */
val LocalPulseColors = staticCompositionLocalOf {
    PulseColors(
        accent = Color(0xFFFFB74D), // Default Amber
        surface = Color(0x44FFFFFF),
        onSurface = Color(0xFF333333),
        isDark = false
    )
}

/**
 * Global access point for Mori's atmospheric tokens.
 */
object PulseTheme {
    val colors: PulseColors
        @Composable
        @ReadOnlyComposable
        get() = LocalPulseColors.current
}

/**
 * Calculates the current atmospheric palette using perceptual OKLab blending.
 */
@Composable
fun rememberPulseColors(
    worldState: WorldState
): PulseColors {
    return remember(worldState.chronosSunAltitude, worldState.energyThermalStress) {
        val sun = worldState.chronosSunAltitude
        
        // 1. Determine Perceptual "Dark" state
        val isDark = sun <= 0.2f

        // 2. Calculate Continuous Foundation (Background context for luminance)
        val foundation = when {
            sun < -0.5f -> Color(0xFF0D0221)
            sun < 0f -> ColorUtils.lerpColorOklab(Color(0xFF0D0221), Color(0xFF240B36), (sun + 0.5f) * 2f)
            sun < 0.5f -> ColorUtils.lerpColorOklab(Color(0xFF240B36), Color(0xFF00B0FF), sun * 2f)
            else -> Color(0xFF40C4FF)
        }

        // 3. Calculate Continuous Accent
        val baseAccent = when {
            sun < 0f -> ColorUtils.lerpColorOklab(Color(0xFFE040FB), Color(0xFFFF5252), (sun + 1f))
            else -> ColorUtils.lerpColorOklab(Color(0xFFFF5252), Color(0xFF00E676), sun)
        }

        // 4. Perceptual Contrast Guard
        val surfaceLuminance = foundation.luminance()
        val highContrastText = if (surfaceLuminance > 0.5f) {
            Color(0xFF000000)
        } else {
            Color(0xFFFFFFFF)
        }

        // 5. Thermal Stress Refinement
        val finalAccent = if (worldState.energyThermalStress > 0.5f) {
            ColorUtils.lerpColorOklab(baseAccent, Color.Black.copy(alpha = 0.6f), (worldState.energyThermalStress - 0.5f) * 2f)
        } else {
            baseAccent
        }

        // 6. Final Surface (Glass)
        val surfaceAlpha = if (isDark) 0.3f else 0.3f
        val baseSurface = foundation.copy(alpha = surfaceAlpha)

        PulseColors(
            accent = finalAccent,
            surface = baseSurface,
            onSurface = highContrastText,
            isDark = isDark
        )
    }
}
