package me.ashishekka.mori.engine.renderer

import me.ashishekka.mori.engine.core.MoriEngineState
import me.ashishekka.mori.engine.core.interfaces.EngineCanvas

/**
 * A basic renderer that fills the canvas with a solid color.
 * This is used as a safety fallback if complex renders fail.
 */
class StaticFallbackRenderer(
    private val defaultColor: Int = 0xFF121212.toInt()
) : EffectRenderer {

    override val zOrder: Int = Int.MIN_VALUE
    
    private var currentColor: Int = defaultColor

    override fun onSurfaceChanged(width: Int, height: Int, density: Float) {}

    override fun update(state: MoriEngineState) {
        // UNIFIED: Pull the background color from the centralized theme policy
        currentColor = state.dominantSurfaceColor
    }

    override fun render(canvas: EngineCanvas) {
        canvas.drawColor(currentColor)
    }
}
