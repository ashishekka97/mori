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
data class AtmosphereColors(
    val accent: Color,
    val surface: Color,
    val onSurface: Color,
    val isDark: Boolean
)

/**
 * CompositionLocal used to provide [AtmosphereColors] throughout the UI tree.
 */
val LocalAtmosphereColors = staticCompositionLocalOf {
    AtmosphereColors(
        accent = DayAccent,
        surface = DaySurface,
        onSurface = DayOnSurface,
        isDark = false
    )
}

/**
 * Global access point for Mori's atmospheric tokens.
 */
object MoriTheme {
    val colors: AtmosphereColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAtmosphereColors.current
}

/**
 * Calculates and animates the current atmospheric palette using perceptually accurate contrast.
 */
@Composable
fun rememberAtmosphereColors(
    worldState: WorldState
): AtmosphereColors {
    return remember(worldState.chronosSunAltitude, worldState.energyThermalStress) {
        // 1. Base Interpolation (The "Vibe")
        val timeFactor = ((worldState.chronosSunAltitude + 1f) / 2f).coerceIn(0f, 1f)
        val baseAccent = lerp(NightAccent, DayAccent, timeFactor)
        val baseSurface = lerp(NightSurface, DaySurface, timeFactor)
        
        // 2. APPLY PERCEPTUAL LUMINANCE (The "Monet" Principle)
        // We calculate how "bright" the current surface is to the human eye.
        val surfaceLuminance = baseSurface.luminance()
        
        // If surface is bright (> 0.5), we need Dark text. 
        // If surface is dark (<= 0.5), we need Light text.
        // We use slightly off-white/black for better atmospheric feel.
        val highContrastText = if (surfaceLuminance > 0.5f) {
            Color(0xFF1A1A1A) // Deep Grey
        } else {
            Color(0xFFF5F5F5) // Off White
        }

        // 3. THERMAL STRESS REFINEMENT:
        // We desaturate the accent elements (toggles, sliders) 
        // while ensuring the target grey still contrasts with the surface.
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

        AtmosphereColors(
            accent = finalAccent,
            surface = baseSurface,
            onSurface = highContrastText,
            isDark = surfaceLuminance <= 0.5f
        )
    }
}
