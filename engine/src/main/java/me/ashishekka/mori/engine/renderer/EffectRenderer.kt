package me.ashishekka.mori.engine.renderer

import me.ashishekka.mori.engine.core.MoriEngineState
import me.ashishekka.mori.engine.core.interfaces.EngineCanvas

/**
 * A container for color tokens contributed by a renderer.
 * Now includes all themeable color aspects.
 */
data class RendererPalette(
    val accent: Int? = null,
    val foundation: Int? = null,
    val surface: Int? = null,
    val onSurface: Int? = null
)

/**
 * The strict contract for any visual element rendered by Mori.
 */
interface EffectRenderer {

    /**
     * Higher Z-Order layers are drawn on top.
     */
    val zOrder: Int

    /**
     * Reports the colors this renderer is currently using.
     * This method MUST be allocation-free during steady-state rendering.
     * It should return a cached or pre-allocated object.
     */
    fun getPaletteContribution(): RendererPalette? = null

    /**
     * Called when the drawing surface changes.
     */
    fun onSurfaceChanged(width: Int, height: Int, density: Float)

    /**
     * Updates the internal simulation state based on the provided [state].
     */
    fun update(state: MoriEngineState)

    /**
     * Draws the effect onto the provided platform-agnostic [canvas].
     */
    fun render(canvas: EngineCanvas)

    /**
     * Composite method for easy execution.
     */
    fun updateAndDraw(state: MoriEngineState, canvas: EngineCanvas) {
        update(state)
        render(canvas)
    }
}
