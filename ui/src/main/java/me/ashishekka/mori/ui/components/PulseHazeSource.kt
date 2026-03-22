package me.ashishekka.mori.ui.components

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.layer.GraphicsLayer

/**
 * Carries the captured wallpaper texture and its metadata.
 */
@Immutable
data class PulseHazeSource(
    val layer: GraphicsLayer? = null
)

/**
 * CompositionLocal used to provide the [PulseHazeSource] to any glassmorphic component.
 */
val LocalPulseHazeSource = staticCompositionLocalOf { PulseHazeSource() }
