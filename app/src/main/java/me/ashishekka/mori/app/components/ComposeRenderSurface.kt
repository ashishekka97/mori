package me.ashishekka.mori.app.components

import me.ashishekka.mori.engine.core.interfaces.EngineCanvas
import me.ashishekka.mori.engine.core.interfaces.RenderSurface

/**
 * A bridge implementation of [RenderSurface] for Compose.
 * It is "Passive" – it doesn't own the canvas, it just provides
 * the [ComposeEngineCanvas] during the draw pass.
 */
class ComposeRenderSurface(
    private val composeCanvas: ComposeEngineCanvas
) : RenderSurface {

    override fun lockCanvas(): EngineCanvas? {
        // Only return the canvas if the drawScope is actually attached
        return if (composeCanvas.drawScope != null) composeCanvas else null
    }

    override fun unlockCanvasAndPost(canvas: EngineCanvas) {
        // In Compose, drawing is immediate. No "posting" needed.
    }
}
