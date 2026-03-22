package me.ashishekka.mori.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import me.ashishekka.mori.persona.state.WorldState

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
        accent = DayAccent,
        surface = DaySurface,
        onSurface = DayOnSurface,
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
 * Calculates the current atmospheric palette using the exact same math as the Engine.
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
            sun < -0.5f -> Color(0xFF0A0E14)
            sun < 0f -> lerp(Color(0xFF0A0E14), Color(0xFF1A1C2E), (sun + 0.5f) * 2f)
            sun < 0.5f -> lerp(Color(0xFF1A1C2E), Color(0xFFF5F5F5), sun * 2f)
            else -> Color(0xFFFFFFFF)
        }

        // 3. Calculate Continuous Accent
        val baseAccent = when {
            sun < 0f -> lerp(Color(0xFFBB86FC), Color(0xFFFF7043), (sun + 1f))
            else -> lerp(Color(0xFFFF7043), Color(0xFFFF9800), sun)
        }

        // 4. Perceptual Contrast Guard (The "Monet" Principle)
        val surfaceLuminance = foundation.luminance()
        val highContrastText = if (surfaceLuminance > 0.5f) {
            Color(0xFF1A1A1A)
        } else {
            Color(0xFFF5F5F5)
        }

        // 5. Thermal Stress Refinement
        val stressTarget = if (surfaceLuminance > 0.5f) {
            Color.Black.copy(alpha = 0.6f)
        } else {
            Color.White.copy(alpha = 0.6f)
        }

        val finalAccent = if (worldState.energyThermalStress > 0.5f) {
            lerp(baseAccent, stressTarget, (worldState.energyThermalStress - 0.5f) * 2f)
        } else {
            baseAccent
        }

        // 6. Final Surface (Glass)
        val surfaceAlpha = if (isDark) 0.4f else 0.4f // Unified for better glass verification
        val baseSurface = foundation.copy(alpha = surfaceAlpha)

        PulseColors(
            accent = finalAccent,
            surface = baseSurface,
            onSurface = highContrastText,
            isDark = isDark
        )
    }
}
