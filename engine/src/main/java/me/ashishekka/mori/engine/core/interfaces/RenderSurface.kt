package me.ashishekka.mori.engine.core.interfaces

/**
 * Represents an abstract drawing canvas.
 * This will eventually mirror the methods we need from android.graphics.Canvas,
 * but without the platform dependency.
 */
interface EngineCanvas {
    fun drawColor(colorInt: Int)
    fun drawRect(left: Float, top: Float, right: Float, bottom: Float, color: Int)
    // Future: drawBitmap, etc.
}

/**
 * Platform-agnostic interface for acquiring and releasing a drawing surface.
 */
interface RenderSurface {
    /**
     * Attempts to acquire the canvas for drawing.
     * Returns null if the surface is not currently available.
     */
    fun lockCanvas(): EngineCanvas?

    /**
     * Submits the drawn canvas to be displayed.
     */
    fun unlockCanvasAndPost(canvas: EngineCanvas)
}
