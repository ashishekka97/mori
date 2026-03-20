package me.ashishekka.mori.engine.renderer

import me.ashishekka.mori.engine.core.MoriEngineState
import me.ashishekka.mori.engine.core.interfaces.EngineCanvas
import kotlin.math.min
import kotlin.math.sin

/**
 * A debug renderer that pulses a central "Living Core" circle.
 * Verifies that multiple providers (Energy, Chronos, Zen, Atmos) are correctly
 * mapped and reactive.
 */
class DebugPulseRenderer : EffectRenderer {

    override val zOrder: Int = 0 

    private var pulseTime = 0f
    private var colorValue = 0
    private lateinit var state: MoriEngineState

    override fun onSurfaceChanged(width: Int, height: Int, density: Float) {
        // No-op
    }

    override fun update(state: MoriEngineState) {
        this.state = state
        
        // 1. Battery drives Pulse Speed
        val speed = 0.02f + (state.energyBatteryLevel * 0.08f)
        pulseTime += if (state.energyIsCharging) speed * 2f else speed
        
        val intensity = ((sin(pulseTime) + 1f) / 2f) * 0.5f
        colorValue = (intensity * 255).toInt()
    }

    override fun render(canvas: EngineCanvas) {
        // 2. Solar Altitude drives Color (Gold for Day, Purple for Night)
        val color = if (state.chronosSunAltitude > 0) {
            (0xFF shl 24) or (colorValue shl 16) or ((colorValue * 0.8f).toInt() shl 8)
        } else {
            (0xFF shl 24) or (colorValue shl 16) or (colorValue)
        }
        
        // 3. Viewport metrics drive positioning
        val centerX = state.viewportSafeX + (state.viewportSafeWidth / 2f)
        val centerY = state.viewportSafeY + (state.viewportSafeHeight / 2f)
        
        // 4. Social Noise subtly expands the radius and border
        val baseRadius = min(state.viewportSafeWidth, state.viewportSafeHeight) * 0.2f
        val noiseRadius = state.zenSocialNoise * 50f
        val finalRadius = baseRadius + noiseRadius
        
        // Social noise also drives the thickness of the "noise border"
        val borderThickness = 4f + (state.zenSocialNoise * 16f)

        // Draw the "Living Core" (Filled)
        canvas.drawCircle(centerX, centerY, finalRadius, color, isFilled = true)

        // Draw the "Noise Border" (Stroked)
        // Note: The radius remains the same, but it's drawn as an outline
        canvas.drawCircle(
            centerX,
            centerY,
            finalRadius,
            0xFFFFFFFF.toInt(),
            isFilled = false,
            thickness = borderThickness
        )
    }
}
