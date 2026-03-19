package me.ashishekka.mori.engine.renderer

import me.ashishekka.mori.engine.core.MoriEngineState
import me.ashishekka.mori.engine.core.interfaces.EngineCanvas
import kotlin.math.sin

/**
 * A debug renderer that pulses the background color within the safe area.
 * Verifies that the rendering loop is active and the LayerManager is working.
 */
class DebugPulseRenderer : EffectRenderer {

    override val zOrder: Int = 0 // Background layer

    private var pulseTime = 0f
    private var colorValue = 0
    private lateinit var state: MoriEngineState

    override fun onSurfaceChanged(width: Int, height: Int, density: Float) {
        // No-op for pulse
    }

    override fun update(state: MoriEngineState) {
        this.state = state
        // Increment pulse based on a fixed speed (independent of FPS)
        pulseTime += 0.05f
        
        val intensity = ((sin(pulseTime) + 1f) / 2f) * 0.3f // 0.0 to 0.3 range
        colorValue = (intensity * 255).toInt()
    }

    override fun render(canvas: EngineCanvas) {
        // 1. Draw the Pulsing Box
        val color = (0xFF shl 24) or (colorValue shl 16) or (colorValue)
        canvas.drawRect(
            state.viewportSafeX,
            state.viewportSafeY,
            state.viewportSafeX + state.viewportSafeWidth,
            state.viewportSafeY + state.viewportSafeHeight,
            color,
            isFilled = true
        )

        // 2. Draw a white border around the Safe Area for verification
        canvas.drawRect(
            state.viewportSafeX,
            state.viewportSafeY,
            state.viewportSafeX + state.viewportSafeWidth,
            state.viewportSafeY + state.viewportSafeHeight,
            0xFFFFFFFF.toInt(),
            isFilled = false
        )
    }
}
