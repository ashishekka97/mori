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
 */
@Composable
fun PulseTheme(
    worldState: WorldState = WorldState(),
    paletteOverride: PulseColors? = null,
    content: @Composable () -> Unit
) {
    val pulseColors = paletteOverride ?: rememberPulseColors(worldState)
    
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
            
            // SYSTEM BAR SYNC:
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
            
            val controller = WindowCompat.getInsetsController(window, view)
            
            // If the environment is DARK, we need LIGHT icons (AppearanceLight = false)
            // If the environment is LIGHT, we need DARK icons (AppearanceLight = true)
            controller.isAppearanceLightStatusBars = !pulseColors.isDark
            controller.isAppearanceLightNavigationBars = !pulseColors.isDark
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
