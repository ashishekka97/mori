package me.ashishekka.mori.engine.renderer

import me.ashishekka.mori.engine.core.MoriEngineState
import me.ashishekka.mori.engine.core.interfaces.EngineCanvas
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

/**
 * The Ultimate Phase 4 Smoke Test.
 * A "Living Core" that reacts to:
 * - Battery: Pulse Speed
 * - Sun: Color (Day/Night)
 * - Social: Border Thickness
 * - Atmos: Opacity (Dim in dark)
 * - Zen: Pause Pulse (Stillness in DND)
 * - Vitality: External Progress Ring (Steps)
 * - Thermal: Jitter (Shake on stress)
 */
class DebugPulseRenderer : EffectRenderer {

    override val zOrder: Int = 0

    private var pulseTime = 0f
    private var colorValue = 0
    private lateinit var state: MoriEngineState

    override fun onSurfaceChanged(width: Int, height: Int, density: Float) {}

    override fun update(state: MoriEngineState) {
        this.state = state

        // 1. Zen (DND) stops the pulse time from advancing
        if (!state.zenIsDndActive) {
            // 2. Battery drives Speed
            val speed = 0.02f + (state.energyBatteryLevel * 0.08f)
            pulseTime += if (state.energyIsCharging) speed * 2f else speed
        }

        // 3. Atmos drives Opacity
        // Mapping 0.0-1.0 Atmos to 50-255 Alpha
        val alpha = (50 + (state.atmosLightLevel * 205)).toInt()
        
        val intensity = ((sin(pulseTime) + 1f) / 2f) * 0.5f
        val rgbValue = (intensity * 255).toInt()
        
        // Final color value with Atmos Alpha
        colorValue = (alpha shl 24) or (rgbValue shl 16) // Simplified for base
    }

    override fun render(canvas: EngineCanvas) {
        // 4. Solar Altitude drives Color Hue
        val baseColor = if (state.chronosSunAltitude > 0) {
            // Day: Amber
            (state.colorAlpha shl 24) or (state.pulseIntensity shl 16) or ((state.pulseIntensity * 0.8f).toInt() shl 8)
        } else {
            // Night: Purple
            (state.colorAlpha shl 24) or (state.pulseIntensity shl 16) or (state.pulseIntensity)
        }

        // 5. Thermal Stress drives Jitter
        var offsetX = 0f
        var offsetY = 0f
        if (state.energyThermalStress > 0.3f) {
            val shake = state.energyThermalStress * 10f
            offsetX = Random.nextFloat() * shake - (shake / 2)
            offsetY = Random.nextFloat() * shake - (shake / 2)
        }

        val centerX = state.viewportSafeX + (state.viewportSafeWidth / 2f) + offsetX
        val centerY = state.viewportSafeY + (state.viewportSafeHeight / 2f) + offsetY

        // 6. Social Noise drives Border Thickness
        val baseRadius = min(state.viewportSafeWidth, state.viewportSafeHeight) * 0.2f
        val noiseRadius = state.zenSocialNoise * 50f
        val coreRadius = baseRadius + noiseRadius
        val borderThickness = 4f + (state.zenSocialNoise * 16f)

        // Draw the "Living Core"
        canvas.drawCircle(centerX, centerY, coreRadius, baseColor, isFilled = true)
        canvas.drawCircle(centerX, centerY, coreRadius, 0xFFFFFFFF.toInt(), isFilled = false, thickness = borderThickness)

        // 7. Vitality drives the External Ring (Progress Bar)
        val vitalityRadius = coreRadius + 40f
        val vitalityColor = (0x88 shl 24) or (0x00FF00) // Translucent Green
        // We'll draw it as a thin circle representing the goal
        canvas.drawCircle(centerX, centerY, vitalityRadius, 0x44FFFFFF.toInt(), isFilled = false, thickness = 2f)
        // And a thicker one for actual progress (simplified as a full circle for now)
        if (state.vitalityStepsProgress > 0) {
            canvas.drawCircle(centerX, centerY, vitalityRadius, vitalityColor, isFilled = false, thickness = 8f * state.vitalityStepsProgress)
        }
    }

    // Helper extensions for clean code
    private val MoriEngineState.colorAlpha: Int
        get() = (50 + (this.atmosLightLevel * 205)).toInt()
    
    private val MoriEngineState.pulseIntensity: Int
        get() = colorValue and 0x00FFFFFF shr 16 // Extract the calculated pulse intensity
}
