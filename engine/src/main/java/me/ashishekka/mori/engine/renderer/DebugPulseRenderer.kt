package me.ashishekka.mori.engine.renderer

import me.ashishekka.mori.engine.core.MoriEngineState
import me.ashishekka.mori.engine.core.interfaces.EngineCanvas
import kotlin.math.sin

/**
 * A debug renderer that pulses the background color.
 * Verifies that the rendering loop is active and the LayerManager is working.
 */
class DebugPulseRenderer : EffectRenderer {

    override val zOrder: Int = 0 // Background layer

    private var pulseTime = 0f

    override fun onSurfaceChanged(width: Int, height: Int, density: Float) {
        // No-op for pulse
    }

    override fun update(state: MoriEngineState) {
        // Increment pulse based on a fixed speed (independent of FPS)
        // In Phase 3, we would use state.chronosTimeProgress
        pulseTime += 0.05f
    }

    override fun render(canvas: EngineCanvas) {
        // Oscillate between deep purple and black
        val intensity = ((sin(pulseTime) + 1f) / 2f) * 0.3f // 0.0 to 0.3 range
        val colorValue = (intensity * 255).toInt()
        
        // Construct a simple ARGB color: FF [R] 00 [B] (Purple-ish pulse)
        val color = (0xFF shl 24) or (colorValue shl 16) or (colorValue)
        canvas.drawColor(color)
    }
}
