package me.ashishekka.mori.engine.renderer

import me.ashishekka.mori.engine.core.MoriEngineState
import me.ashishekka.mori.engine.core.interfaces.EngineCanvas

/**
 * A container for color tokens contributed by a renderer.
 * Includes specific weights for each color token to allow granular influence.
 */
data class RendererPalette(
    val accent: Int? = null,
    val accentWeight: Float = 1.0f,
    
    val foundation: Int? = null,
    val foundationWeight: Float = 1.0f,
    
    val surface: Int? = null,
    val surfaceWeight: Float = 1.0f,
    
    val onSurface: Int? = null,
    val onSurfaceWeight: Float = 1.0f
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
