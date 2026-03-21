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
 * @param accentOverride An optional color to override the base accent (e.g., from the Engine).
 */
@Composable
fun rememberAtmosphereColors(
    worldState: WorldState,
    accentOverride: Color? = null
): AtmosphereColors {
    return remember(worldState.chronosSunAltitude, worldState.energyThermalStress, accentOverride) {
        // 1. Calculate Time Factor (Normalize -1..1 to 0..1)
        val timeFactor = ((worldState.chronosSunAltitude + 1f) / 2f).coerceIn(0f, 1f)

        // 2. Interpolate Day vs Night tokens
        val baseAccent = accentOverride ?: lerp(NightAccent, DayAccent, timeFactor)
        val baseSurface = lerp(NightSurface, DaySurface, timeFactor)
        val baseOnSurface = lerp(NightOnSurface, DayOnSurface, timeFactor)

        // 3. Apply Thermal Stress (Desaturation)
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
