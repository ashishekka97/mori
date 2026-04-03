package me.ashishekka.mori.engine.core.interfaces

/**
 * Represents an abstract drawing canvas.
 * This will eventually mirror the methods we need from android.graphics.Canvas,
 * but without the platform dependency.
 */
interface EngineCanvas {
    fun drawColor(colorInt: Int)
    fun drawRect(left: Float, top: Float, right: Float, bottom: Float, color: Int, isFilled: Boolean = true, thickness: Float = 4f)
    fun drawCircle(centerX: Float, centerY: Float, radius: Float, color: Int, isFilled: Boolean = true, thickness: Float = 4f)
    fun drawPolygon(points: FloatArray, pointCount: Int, color: Int, isFilled: Boolean = true, thickness: Float = 4f)
    
    /** 
     * Draws a pre-loaded bitmap asset within the specified virtual bounds.
     * The implementation in the platform layer (e.g., Android) will look up 
     * the actual Bitmap using the [AssetRegistry].
     */
    fun drawBitmap(resId: Int, left: Float, top: Float, right: Float, bottom: Float, alpha: Float = 1.0f)

    /**
     * Draws a custom AGSL shader.
     * @param resId The registered ID of the shader asset.
     * @param uniforms The 16-slot PropertyBuffer from the Rule Engine.
     */
     fun drawShader(resId: Int, left: Float, top: Float, right: Float, bottom: Float, uniforms: FloatArray, complexity: Float = 1.0f)

     /**
     * Draws a platform-specific path geometry.
     * The implementation in the platform layer (e.g., Android) will look up
     * the actual Path using the [AssetRegistry].
     */
     fun drawPath(resId: Int, color: Int, isFilled: Boolean = true, thickness: Float = 4f)

    fun save()
    fun restore()
    fun rotate(degrees: Float, pivotX: Float = 0f, pivotY: Float = 0f)
    fun translate(dx: Float, dy: Float)
    fun scale(sx: Float, sy: Float, pivotX: Float = 0f, pivotY: Float = 0f)
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
