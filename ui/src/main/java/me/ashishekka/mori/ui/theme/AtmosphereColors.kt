package me.ashishekka.mori.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
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
 * Calculates and animates the current atmospheric palette based on the WorldState.
 * 
 * @param worldState The current device state.
 */
@Composable
fun rememberAtmosphereColors(
    worldState: WorldState
): AtmosphereColors {
    return remember(worldState.chronosSunAltitude, worldState.energyThermalStress) {
        // 1. Calculate Time Factor (Normalize -1..1 to 0..1)
        val timeFactor = ((worldState.chronosSunAltitude + 1f) / 2f).coerceIn(0f, 1f)

        // 2. Interpolate base tokens
        val baseAccent = lerp(NightAccent, DayAccent, timeFactor)
        val baseSurface = lerp(NightSurface, DaySurface, timeFactor)
        
        // 3. READABILITY FIX: Dawn/Dusk High-Contrast Guard
        val sunAltitude = worldState.chronosSunAltitude
        val baseOnSurface = when {
            sunAltitude > 0.2f -> DayOnSurface
            sunAltitude < -0.2f -> NightOnSurface
            else -> if (sunAltitude > 0) DayOnSurface else NightOnSurface
        }

        // 4. THERMAL STRESS REFINEMENT:
        // We desaturate the accent color for UI elements (thumbs, tracks), 
        // but we keep it legible. We use a darker/lighter grey depending on theme mode
        // rather than a generic neutral grey that might blend with the surface.
        val stressTarget = if (sunAltitude > 0) {
            Color.Black.copy(alpha = 0.6f) // Dark grey for light mode
        } else {
            Color.White.copy(alpha = 0.6f) // Light grey for dark mode
        }

        val finalAccent = if (worldState.energyThermalStress > 0.5f) {
            lerp(baseAccent, stressTarget, (worldState.energyThermalStress - 0.5f) * 2f)
        } else {
            baseAccent
        }

        AtmosphereColors(
            accent = finalAccent,
            surface = baseSurface,
            onSurface = baseOnSurface,
            isDark = sunAltitude <= 0f
        )
    }
}
