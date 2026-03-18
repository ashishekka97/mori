package me.ashishekka.mori.engine.renderer

import me.ashishekka.mori.engine.core.MoriEngineState
import me.ashishekka.mori.engine.core.interfaces.EngineCanvas

/**
 * The strict contract for any visual element rendered by Mori.
 * All implementations MUST adhere to zero-allocation rules inside update and render.
 */
interface EffectRenderer {

    /**
     * Called when the drawing surface size or density changes.
     * This is the place to pre-calculate geometry or coordinates
     * based on the surface dimensions.
     */
    fun onSurfaceChanged(width: Int, height: Int, density: Float)

    /**
     * Phase 1: Update. Use this to prepare visual state for drawing.
     * MUST NOT perform any memory allocations.
     * 
     * @param state The current engine state snapshot (Mirror).
     */
    fun update(state: MoriEngineState)

    /**
     * Phase 2: Render. Use this to draw on the provided canvas.
     * MUST NOT perform any memory allocations.
     *
     * @param canvas The platform-agnostic canvas.
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
