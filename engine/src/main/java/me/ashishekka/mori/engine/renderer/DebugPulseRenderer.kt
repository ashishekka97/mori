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

    private var pulseIntensityValue = 0
    private lateinit var state: MoriEngineState

    override fun onSurfaceChanged(width: Int, height: Int, density: Float) {}

    override fun update(state: MoriEngineState) {
        this.state = state

        // 1. Calculate Pulse Phase based on GLOBAL TIME
        // 1,000,000,000L nanos = 1 second.
        // We use a double for the base time to ensure precision over long periods.
        val timeSeconds = state.currentTimeNanos / 1_000_000_000.0
        
        // 2. Battery drives Speed (Frequency)
        val frequency = 0.5 + (state.energyBatteryLevel * 2.0)
        val multiplier = if (state.energyIsCharging) 2.0 else 1.0
        
        // Final intensity (0.0 to 1.0)
        val intensity = if (state.zenIsDndActive) {
            0.5f // Stillness in DND
        } else {
            ((sin(timeSeconds * frequency * multiplier * Math.PI) + 1.0) / 2.0).toFloat()
        }

        // 3. Atmos drives Opacity
        val alpha = (50 + (state.atmosLightLevel * 205)).toInt()
        val rgbValue = (intensity * 255).toInt()
        
        pulseIntensityValue = (alpha shl 24) or (rgbValue shl 16)
    }

    override fun render(canvas: EngineCanvas) {
        // 4. Solar Altitude drives Color Hue
        val baseColor = if (state.chronosSunAltitude > 0) {
            // Day: Amber
            (colorAlpha shl 24) or (pulseIntensity shl 16) or ((pulseIntensity * 0.8f).toInt() shl 8)
        } else {
            // Night: Purple
            (colorAlpha shl 24) or (pulseIntensity shl 16) or (pulseIntensity)
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
        canvas.drawCircle(centerX, centerY, vitalityRadius, 0x44FFFFFF.toInt(), isFilled = false, thickness = 2f)
        if (state.vitalityStepsProgress > 0) {
            canvas.drawCircle(centerX, centerY, vitalityRadius, vitalityColor, isFilled = false, thickness = 8f * state.vitalityStepsProgress)
        }
    }

    private val colorAlpha: Int get() = pulseIntensityValue ushr 24
    private val pulseIntensity: Int get() = (pulseIntensityValue shr 16) and 0xFF
}
