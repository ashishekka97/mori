package me.ashishekka.mori.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import me.ashishekka.mori.persona.state.WorldState

/**
 * The main theme wrapper for Mori.
 * Injects [AtmosphereColors] and [PulseTypography] into the composition.
 * 
 * @param worldState The current state of the world used to drive the dynamic palette.
 * @param paletteOverride An optional full palette to override the base logic (e.g., from the Engine).
 */
@Composable
fun MoriTheme(
    worldState: WorldState = WorldState(),
    paletteOverride: AtmosphereColors? = null,
    content: @Composable () -> Unit
) {
    // Use the override if provided, otherwise calculate based on worldState
    val atmosphereColors = paletteOverride ?: rememberAtmosphereColors(worldState)
    
    // Map our dynamic tokens to standard Material 3 slots
    val colorScheme = if (atmosphereColors.isDark) {
        darkColorScheme(
            primary = atmosphereColors.accent,
            surface = atmosphereColors.surface,
            onSurface = atmosphereColors.onSurface
        )
    } else {
        lightColorScheme(
            primary = atmosphereColors.accent,
            surface = atmosphereColors.surface,
            onSurface = atmosphereColors.onSurface
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = atmosphereColors.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !atmosphereColors.isDark
        }
    }

    CompositionLocalProvider(
        LocalAtmosphereColors provides atmosphereColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = PulseTypography,
            content = content
        )
    }
}
