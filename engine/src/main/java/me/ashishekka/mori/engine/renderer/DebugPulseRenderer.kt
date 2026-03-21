package me.ashishekka.mori.engine.renderer

import me.ashishekka.mori.engine.core.MoriEngineState
import me.ashishekka.mori.engine.core.interfaces.EngineCanvas
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

/**
 * The Ultimate Phase 4 Smoke Test.
 * A "Living Core" that reacts to atmospheric signals.
 */
class DebugPulseRenderer : EffectRenderer {

    override val zOrder: Int = 0

    private var pulseIntensityValue = 0
    private lateinit var state: MoriEngineState

    override fun onSurfaceChanged(width: Int, height: Int, density: Float) {}

    override fun update(state: MoriEngineState) {
        this.state = state

        // 1. Calculate Pulse Phase based on GLOBAL TIME
        val timeSeconds = state.currentTimeNanos / 1_000_000_000.0
        val frequency = 0.5 + (state.energyBatteryLevel * 2.0)
        val multiplier = if (state.energyIsCharging) 2.0 else 1.0
        
        val intensity = if (state.zenIsDndActive) {
            0.5f 
        } else {
            ((sin(timeSeconds * frequency * multiplier * Math.PI) + 1.0) / 2.0).toFloat()
        }

        // 2. Atmospheric Data Handover (Living Palette)
        // We determine the "Dominant Color" based on the Sun's altitude
        state.dominantAccentColor = if (state.chronosSunAltitude > 0) {
            0xFFFFB74D.toInt() // Day Accent (Amber)
        } else {
            0xFF9575CD.toInt() // Night Accent (Purple)
        }

        val alpha = (50 + (state.atmosLightLevel * 205)).toInt()
        val rgbValue = (intensity * 255).toInt()
        
        pulseIntensityValue = (alpha shl 24) or (rgbValue shl 16)
    }

    override fun render(canvas: EngineCanvas) {
        // 3. Solar Altitude drives Color Hue
        val baseColor = if (state.chronosSunAltitude > 0) {
            // Day: Amber
            (colorAlpha shl 24) or (pulseIntensity shl 16) or ((pulseIntensity * 0.8f).toInt() shl 8)
        } else {
            // Night: Purple
            (colorAlpha shl 24) or (pulseIntensity shl 16) or (pulseIntensity)
        }

        // 4. Thermal Stress drives Jitter
        var offsetX = 0f
        var offsetY = 0f
        if (state.energyThermalStress > 0.3f) {
            val shake = state.energyThermalStress * 10f
            offsetX = Random.nextFloat() * shake - (shake / 2)
            offsetY = Random.nextFloat() * shake - (shake / 2)
        }

        val centerX = state.viewportSafeX + (state.viewportSafeWidth / 2f) + offsetX
        val centerY = state.viewportSafeY + (state.viewportSafeHeight / 2f) + offsetY

        val baseRadius = min(state.viewportSafeWidth, state.viewportSafeHeight) * 0.2f
        val noiseRadius = state.zenSocialNoise * 50f
        val coreRadius = baseRadius + noiseRadius
        val borderThickness = 4f + (state.zenSocialNoise * 16f)

        // Draw the "Living Core"
        canvas.drawCircle(centerX, centerY, coreRadius, baseColor, isFilled = true)
        canvas.drawCircle(centerX, centerY, coreRadius, 0xFFFFFFFF.toInt(), isFilled = false, thickness = borderThickness)

        // 5. Vitality drives the External Ring
        val vitalityRadius = coreRadius + 40f
        val vitalityColor = (0x88 shl 24) or (0x00FF00)
        canvas.drawCircle(centerX, centerY, vitalityRadius, 0x44FFFFFF.toInt(), isFilled = false, thickness = 2f)
        if (state.vitalityStepsProgress > 0) {
            canvas.drawCircle(centerX, centerY, vitalityRadius, vitalityColor, isFilled = false, thickness = 8f * state.vitalityStepsProgress)
        }
    }

    private val colorAlpha: Int get() = pulseIntensityValue ushr 24
    private val pulseIntensity: Int get() = (pulseIntensityValue shr 16) and 0xFF
}
