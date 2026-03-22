package me.ashishekka.mori.engine.renderer

import me.ashishekka.mori.engine.core.MoriEngineState
import me.ashishekka.mori.engine.core.interfaces.EngineCanvas
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

/**
 * A vibrant, fullscreen atmospheric "Aurora" that reacts to signals,
 * now including a "Stardust" visualization for step goal progress.
 */
class DebugPulseRenderer : EffectRenderer {

    override val zOrder: Int = 1

    private var pulseIntensityValue = 0
    private lateinit var state: MoriEngineState
    
    // STARDUST
    private val maxParticles = 200
    private val particleX = FloatArray(maxParticles)
    private val particleY = FloatArray(maxParticles)
    private val particlePhase = FloatArray(maxParticles)

    // JITTER (ZERO-ALLOCATION)
    private var jitterX = 0f
    private var jitterY = 0f

    // PALETTE CACHE
    private var cachedPalette: RendererPalette? = null
    private var lastAccentColor: Int = 0

    override fun onSurfaceChanged(width: Int, height: Int, density: Float) {
        for (i in 0 until maxParticles) {
            particleX[i] = Random.nextFloat() * width
            particleY[i] = Random.nextFloat() * height
            particlePhase[i] = Random.nextFloat() * 2f * Math.PI.toFloat()
        }
    }

    override fun update(state: MoriEngineState) {
        this.state = state

        val timeSeconds = state.currentTimeNanos / 1_000_000_000.0
        val frequency = 0.5 + (state.energyBatteryLevel * 2.0)
        val multiplier = if (state.energyIsCharging) 2.0 else 1.0
        val intensity = if (state.zenIsDndActive) 0.5f else {
            ((sin(timeSeconds * frequency * multiplier * Math.PI) + 1.0) / 2.0).toFloat()
        }

        val alpha = (100 + (state.atmosLightLevel * 155)).toInt()
        val rgbIntensity = (intensity * 255).toInt()
        pulseIntensityValue = (alpha shl 24) or (rgbIntensity shl 16)
        
        val currentAccent = state.dominantAccentColor
        if (currentAccent != lastAccentColor) {
            cachedPalette = null
            lastAccentColor = currentAccent
        }

        // Pre-calculate jitter in the update phase
        if (state.energyThermalStress > 0.3f) {
            val shake = state.energyThermalStress * 20f
            jitterX = Random.nextFloat() * shake - (shake / 2)
            jitterY = Random.nextFloat() * shake - (shake / 2)
        } else {
            jitterX = 0f
            jitterY = 0f
        }
    }

    override fun getPaletteContribution(): RendererPalette? {
        if (cachedPalette == null) {
            val themeRgb = state.dominantAccentColor and 0x00FFFFFF
            val accentColor = (colorAlpha shl 24) or themeRgb
            cachedPalette = RendererPalette(accent = accentColor)
        }
        return cachedPalette
    }

    override fun render(canvas: EngineCanvas) {
        val width = state.surfaceWidth.toFloat()
        val height = state.surfaceHeight.toFloat()
        val timeSeconds = state.currentTimeNanos / 1_000_000_000.0
        
        val themeRgb = state.dominantAccentColor and 0x00FFFFFF
        val pulseFactor = pulseIntensity / 255f

        // ... (Atmospheric Blobs rendering)

        val numActiveParticles = (state.vitalityStepsProgress * maxParticles).toInt()
        val stardustColor = 0xFFFFFFFF.toInt()
        for (i in 0 until numActiveParticles) {
            val particleAlpha = (128 + sin(timeSeconds * 2.0 + particlePhase[i]) * 127).toInt()
            val finalColor = (particleAlpha shl 24) or (stardustColor and 0x00FFFFFF)
            canvas.drawCircle(particleX[i], particleY[i], 3f, finalColor, true)
        }

        // Use pre-calculated jitter values for a pure render method
        val coreX = state.viewportSafeX + (state.viewportSafeWidth / 2f) + jitterX
        val coreY = state.viewportSafeY + (state.viewportSafeHeight / 2f) + jitterY
        val coreRadius = min(state.viewportSafeWidth, state.viewportSafeHeight) * 0.12f
        canvas.drawCircle(coreX, coreY, coreRadius, 0xFFFFFFFF.toInt(), true)
        canvas.drawCircle(coreX, coreY, coreRadius + 15f, (colorAlpha shl 24) or themeRgb, false, 6f)
    }

    private val colorAlpha: Int get() = pulseIntensityValue ushr 24
    private val pulseIntensity: Int get() = (pulseIntensityValue shr 16) and 0xFF
}
