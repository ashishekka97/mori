package me.ashishekka.mori.engine.renderer

import me.ashishekka.mori.engine.core.MoriEngineState
import me.ashishekka.mori.engine.core.interfaces.EngineCanvas
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

/**
 * The Ultimate Phase 4/5 Smoke Test.
 * A vibrant, fullscreen atmospheric "Aurora" that reacts to signals.
 * Pulls all color information from the centralized theme policy.
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
        
        pulseIntensityValue = (alpha shl 24) or (rgbIntensity shl 16)
    }

    override fun render(canvas: EngineCanvas) {
        // 3. PURE AGNOSTIC RENDERING
        // All colors are derived from the state.dominantPalette
        val width = state.surfaceWidth.toFloat()
        val height = state.surfaceHeight.toFloat()
        
        val themeRgb = state.dominantAccentColor and 0x00FFFFFF
        val accentColor = (colorAlpha shl 24) or themeRgb

        // 4. DRAW VIBRANT ATMOSPHERIC GLOW
        val centerX = width / 2f
        val centerY = height / 2f
        
        // The Aurora blob scales with pulse intensity
        val auroraRadius = min(width, height) * (0.4f + (pulseIntensity / 255f) * 0.2f)
        canvas.drawCircle(centerX, centerY, auroraRadius, accentColor, isFilled = true)

        // 5. THERMAL JITTER FOR THE CORE
        var offsetX = 0f
        var offsetY = 0f
        if (state.energyThermalStress > 0.3f) {
            val shake = state.energyThermalStress * 15f
            offsetX = Random.nextFloat() * shake - (shake / 2)
            offsetY = Random.nextFloat() * shake - (shake / 2)
        }

        val coreX = state.viewportSafeX + (state.viewportSafeWidth / 2f) + offsetX
        val coreY = state.viewportSafeY + (state.viewportSafeHeight / 2f) + offsetY
        val coreRadius = min(state.viewportSafeWidth, state.viewportSafeHeight) * 0.15f

        // Draw the "Sharp Core" (White with accent border)
        canvas.drawCircle(coreX, coreY, coreRadius, 0xFFFFFFFF.toInt(), isFilled = true)
        canvas.drawCircle(coreX, coreY, coreRadius + 10f, accentColor, isFilled = false, thickness = 4f)
    }

    private val colorAlpha: Int get() = pulseIntensityValue ushr 24
    private val pulseIntensity: Int get() = (pulseIntensityValue shr 16) and 0xFF
}
