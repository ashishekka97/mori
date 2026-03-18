package me.ashishekka.mori.engine.renderer

import me.ashishekka.mori.engine.core.MoriEngineState
import me.ashishekka.mori.engine.core.interfaces.EngineCanvas

/**
 * A basic renderer that fills the canvas with a solid color.
 * This is used as a safety fallback if complex renders fail.
 */
class StaticFallbackRenderer(
    private val color: Int
) : EffectRenderer {

    override val zOrder: Int = Int.MIN_VALUE

    override fun onSurfaceChanged(width: Int, height: Int, density: Float) {
        // No pre-calculation needed for solid color
    }

    override fun update(state: MoriEngineState) {
        // Static color, nothing to update
    }

    override fun render(canvas: EngineCanvas) {
        canvas.drawColor(color)
    }
}
