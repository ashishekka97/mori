package me.ashishekka.mori.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
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
 * Calculates and animates the current atmospheric palette based on the WorldState.
 */
@Composable
fun rememberAtmosphereColors(worldState: WorldState): AtmosphereColors {
    return remember(worldState.chronosSunAltitude, worldState.energyThermalStress) {
        // 1. Calculate Time Factor (Normalize -1..1 to 0..1)
        // 0.0 = Midnight, 0.5 = Sunset/Sunrise, 1.0 = Noon
        val timeFactor = ((worldState.chronosSunAltitude + 1f) / 2f).coerceIn(0f, 1f)

        // 2. Interpolate Day vs Night tokens
        val baseAccent = lerp(NightAccent, DayAccent, timeFactor)
        val baseSurface = lerp(NightSurface, DaySurface, timeFactor)
        val baseOnSurface = lerp(NightOnSurface, DayOnSurface, timeFactor)

        // 3. Apply Thermal Stress (Desaturation)
        // If stress is high, we pull the accent toward a neutral grey.
        val finalAccent = if (worldState.energyThermalStress > 0.5f) {
            lerp(baseAccent, StressMuted, (worldState.energyThermalStress - 0.5f) * 2f)
        } else {
            baseAccent
        }

        AtmosphereColors(
            accent = finalAccent,
            surface = baseSurface,
            onSurface = baseOnSurface,
            isDark = worldState.chronosSunAltitude <= 0f
        )
    }
}
