package me.ashishekka.mori.ui.components

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.layer.GraphicsLayer

/**
 * Carries the captured wallpaper texture and its metadata.
 */
@Immutable
data class HazeSource(
    val layer: GraphicsLayer? = null
)

/**
 * CompositionLocal used to provide the [HazeSource] to any glassmorphic component.
 */
val LocalHazeSource = staticCompositionLocalOf { HazeSource() }
