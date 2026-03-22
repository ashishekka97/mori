package me.ashishekka.mori.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import me.ashishekka.mori.persona.state.WorldState

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
            onSurface = pulseColors.onSurface,
            // THEME SPEC FIX: Buttons and other primary components use this for their content color.
            onPrimary = pulseColors.onSurface 
        )
    } else {
        lightColorScheme(
            primary = pulseColors.accent,
            surface = pulseColors.surface,
            onSurface = pulseColors.onSurface,
            // THEME SPEC FIX: Buttons and other primary components use this for their content color.
            onPrimary = pulseColors.onSurface
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
            
            val controller = WindowCompat.getInsetsController(window, view)
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
