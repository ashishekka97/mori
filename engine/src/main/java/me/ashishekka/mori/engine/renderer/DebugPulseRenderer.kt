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

        // 2. Visual Synthesis
        val alpha = (50 + (state.atmosLightLevel * 205)).toInt()
        val rgbIntensity = (intensity * 255).toInt()
        
        // We store alpha in high byte, and pulse intensity in next byte
        pulseIntensityValue = (alpha shl 24) or (rgbIntensity shl 16)
    }

    override fun render(canvas: EngineCanvas) {
        // 3. UNIFIED PULSE: Modulate the theme color by the calculated intensity
        val targetR = (state.dominantAccentColor shr 16) and 0xFF
        val targetG = (state.dominantAccentColor shr 8) and 0xFF
        val targetB = state.dominantAccentColor and 0xFF
        
        val intensityFactor = pulseIntensity / 255f
        
        // MODULATION: Scale RGB components by the pulse intensity
        val r = (targetR * intensityFactor).toInt()
        val g = (targetG * intensityFactor).toInt()
        val b = (targetB * intensityFactor).toInt()
        
        val baseColor = (colorAlpha shl 24) or (r shl 16) or (g shl 8) or b

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
