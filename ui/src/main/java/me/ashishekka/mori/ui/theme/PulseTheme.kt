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
 * The main theme wrapper for the Pulse Design System.
 * Injects [PulseColors] and [PulseTypography] into the composition.
 * 
 * @param worldState The current state of the world used to drive the dynamic palette.
 * @param paletteOverride An optional full palette to override the base logic.
 */
@Composable
fun PulseTheme(
    worldState: WorldState = WorldState(),
    paletteOverride: PulseColors? = null,
    content: @Composable () -> Unit
) {
    // Use the override if provided, otherwise calculate based on worldState
    val pulseColors = paletteOverride ?: rememberPulseColors(worldState)
    
    // Map our dynamic tokens to standard Material 3 slots
    val colorScheme = if (pulseColors.isDark) {
        darkColorScheme(
            primary = pulseColors.accent,
            surface = pulseColors.surface,
            onSurface = pulseColors.onSurface
        )
    } else {
        lightColorScheme(
            primary = pulseColors.accent,
            surface = pulseColors.surface,
            onSurface = pulseColors.onSurface
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = pulseColors.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !pulseColors.isDark
        }
    }

    CompositionLocalProvider(
        LocalPulseColors provides pulseColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = PulseTypography,
            content = content
        )
    }
}
