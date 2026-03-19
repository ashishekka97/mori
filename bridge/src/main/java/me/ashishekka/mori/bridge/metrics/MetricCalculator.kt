package me.ashishekka.mori.bridge.metrics

import kotlin.math.max
import kotlin.math.min

/**
 * The "Virtual Camera" of Mori.
 * Responsible for all density and aspect-ratio math to keep the Engine "dumb" and fast.
 */
class MetricCalculator {

    var width: Int = 0
        private set
    var height: Int = 0
        private set
    var density: Float = 1f
        private set
    var orientation: Orientation = Orientation.PORTRAIT
        private set

    /**
     * Updates the internal metrics. Usually called from onSurfaceChanged.
     */
    fun updateMetrics(width: Int, height: Int, density: Float) {
        this.width = width
        this.height = height
        this.density = density
        this.orientation = if (width > height) Orientation.LANDSCAPE else Orientation.PORTRAIT
    }

    /**
     * Converts DP units to raw pixels based on device density.
     */
    fun dpToPx(dp: Float): Float = dp * density

    /**
     * Calculates the scale factor needed to map a reference canvas to the screen.
     * 
     * @param refW The width of the artist's design canvas.
     * @param refH The height of the artist's design canvas.
     * @param mode How to handle aspect ratio mismatch (FIT vs FILL).
     */
    fun calculateScaleFactor(refW: Float, refH: Float, mode: ScaleMode): Float {
        if (refW <= 0f || refH <= 0f || width <= 0 || height <= 0) return 1f

        val scaleX = width.toFloat() / refW
        val scaleY = height.toFloat() / refH

        return when (mode) {
            ScaleMode.FIT -> min(scaleX, scaleY)
            ScaleMode.FILL -> max(scaleX, scaleY)
        }
    }

    /**
     * Calculates the X offset needed to center a reference canvas on the screen.
     */
    fun getCenterXOffset(refW: Float, scaleFactor: Float): Float {
        val scaledWidth = refW * scaleFactor
        return (width - scaledWidth) / 2f
    }

    /**
     * Calculates the Y offset needed to center a reference canvas on the screen.
     */
    fun getCenterYOffset(refH: Float, scaleFactor: Float): Float {
        val scaledHeight = refH * scaleFactor
        return (height - scaledHeight) / 2f
    }
}
