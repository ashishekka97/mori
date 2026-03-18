package me.ashishekka.mori.engine.renderer

import android.graphics.Canvas

/**
 * A failsafe renderer that draws a solid color.
 * Used when complex rendering layers fail or are in an invalid state.
 *
 * @param fallbackColor The color to fill the canvas with.
 */
class StaticFallbackRenderer(
    private val fallbackColor: Int
) : EffectRenderer {

    override fun updateAndDraw(canvas: Canvas) {
        canvas.drawColor(fallbackColor)
    }
}
