package me.ashishekka.mori.engine

import android.graphics.Canvas

/**
 * The strict contract for any visual element rendered by Mori.
 * All implementations MUST adhere to zero-allocation rules inside updateAndDraw.
 */
interface EffectRenderer {
    /**
     * Called 30-60 times a second.
     * @param canvas The hardware-accelerated canvas to draw on.
     */
    fun updateAndDraw(canvas: Canvas)
}
