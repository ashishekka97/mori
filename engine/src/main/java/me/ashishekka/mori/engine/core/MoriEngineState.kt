package me.ashishekka.mori.engine.core

/**
 * A mutable, pre-allocated mirror of the Persona's WorldState.
 * This class is a "Flat Memory" object designed for zero-allocation access
 * by the rendering thread. 
 * 
 * DESIGN PRINCIPLE:
 * De-semanticized. All real-world data is stored in the [factBuffer].
 * Named properties are reserved for geometric/lifecycle state that 
 * isn't processed by the Rule Engine.
 */
class MoriEngineState {

    // === THE FACT BUFFER (Agnostic Ingress) ===
    /** Pre-allocated memory for all incoming real-world data. */
    val factBuffer = FloatArray(MoriEngineStateIndices.BUFFER_SIZE)

    /** Continuous normalized time in seconds for smooth animations (Engine-Internal). */
    var timeSeconds: Float 
        get() = factBuffer[MoriEngineStateIndices.FACT_TIME_SECONDS]
        set(value) { factBuffer[MoriEngineStateIndices.FACT_TIME_SECONDS] = value }

    // === VIEWPORT (Geometric Handover) ===
    var viewportSafeX: Float = 0f
    var viewportSafeY: Float = 0f
    var viewportSafeWidth: Float = 0f
    var viewportSafeHeight: Float = 0f
    var viewportReferenceScale: Float = 1f
    var referenceWidth: Float = 1000f
    var referenceHeight: Float = 1000f

    // === SURFACE (Geometry) ===
    var surfaceWidth: Int = 0
    var surfaceHeight: Int = 0
    var surfaceDensity: Float = 1f

    // === PALETTE (Atmospheric) ===
    var dominantFoundationColor: Int = 0xFF121212.toInt()
    var dominantAccentColor: Int = 0xFF9575CD.toInt()
    var dominantSurfaceColor: Int = 0x44000000.toInt()
    var dominantOnSurfaceColor: Int = 0xFFF5F5F5.toInt()
    var isDarkState: Boolean = true

    // === TIME (Global Sync) ===
    var currentTimeNanos: Long = 0L

    // === PERFORMANCE ===
    var shaderComplexity: Float = 1.0f

    /**
     * Helper to fetch a value by its index from the [factBuffer].
     * Used by the RuleEvaluator to maintain 100% zero-allocation access.
     */
    fun getFieldValue(index: Int): Float {
        return if (index in 0 until MoriEngineStateIndices.BUFFER_SIZE) {
            factBuffer[index]
        } else 0.0f
    }
    
    /**
     * Helper to update a value by its index in the [factBuffer].
     */
    fun setFieldValue(index: Int, value: Float) {
        if (index in 0 until MoriEngineStateIndices.BUFFER_SIZE) {
            factBuffer[index] = value
        }
    }
}
