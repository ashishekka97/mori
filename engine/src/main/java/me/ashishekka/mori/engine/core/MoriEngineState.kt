package me.ashishekka.mori.engine.core

/**
 * Static mapping of MoriEngineState fields to integer indices.
 * Used by the RuleEvaluator to maintain 100% zero-allocation access via the ISA.
 */
object MoriEngineStateIndices {
    const val INDEX_TIME_SECONDS = 0
    const val INDEX_SUN_ALTITUDE = 1
    const val INDEX_BATTERY_LEVEL = 2
    const val INDEX_IS_CHARGING = 3
    const val INDEX_STEPS_PROGRESS = 4
    const val INDEX_THERMAL_STRESS = 5
    const val INDEX_SOCIAL_NOISE = 6
    const val INDEX_LIGHT_LEVEL = 7
}

/**
 * A mutable, pre-allocated mirror of the Persona's WorldState.
 * This class is a "Flat Memory" object designed for zero-allocation access
 * by the rendering thread. 
 */
class MoriEngineState {

    // === ENGINE TIME (Internal) ===
    /** Continuous normalized time in seconds for smooth animations. */
    var timeSeconds: Float = 0f

    // === CHRONOS (Time & Cycles) ===
    var chronosTimeProgress: Float = 0f
    var chronosSunAltitude: Float = 0f
    var chronosMoonPhase: Float = 0f
    var chronosSeasonProgress: Float = 0f
    var chronosIsWeekend: Boolean = false

    // === VITALITY (Health & Activity) ===
    var vitalityStepsProgress: Float = 0f
    var vitalityActivityIntensity: Float = 0f
    var vitalitySleepClarity: Float = 1f
    var vitalityStandGoalProgress: Float = 0f

    // === ZEN (Digital Wellbeing) ===
    var zenDigitalCongestion: Float = 0f
    var zenSocialNoise: Float = 0f
    var zenContextSwitching: Float = 0f
    var zenIsDndActive: Boolean = false
    var zenLastInteractionAge: Float = 0f

    // === ENERGY (Device Pulse) ===
    var energyBatteryLevel: Float = 1f
    var energyIsCharging: Boolean = false
    var energyThermalStress: Float = 0f

    // === ATMOS (Environment) ===
    var atmosLightLevel: Float = 1f
    var atmosIsPocketed: Boolean = false
    
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

    // === TIME (Global Sync) ===
    var currentTimeNanos: Long = 0L

    // === PALETTE (Atmospheric) ===
    var dominantFoundationColor: Int = 0xFF121212.toInt()
    var dominantAccentColor: Int = 0xFF9575CD.toInt()
    var dominantSurfaceColor: Int = 0x44000000.toInt()
    var dominantOnSurfaceColor: Int = 0xFFF5F5F5.toInt()
    var isDarkState: Boolean = true

    /**
     * Helper to fetch a value by its index.
     * Used by the RuleEvaluator to maintain 100% zero-allocation access.
     */
    fun getFieldValue(index: Int): Float {
        return when (index) {
            MoriEngineStateIndices.INDEX_TIME_SECONDS -> timeSeconds
            MoriEngineStateIndices.INDEX_SUN_ALTITUDE -> chronosSunAltitude
            MoriEngineStateIndices.INDEX_BATTERY_LEVEL -> energyBatteryLevel
            MoriEngineStateIndices.INDEX_IS_CHARGING -> if (energyIsCharging) 1.0f else 0.0f
            MoriEngineStateIndices.INDEX_STEPS_PROGRESS -> vitalityStepsProgress
            MoriEngineStateIndices.INDEX_THERMAL_STRESS -> energyThermalStress
            MoriEngineStateIndices.INDEX_SOCIAL_NOISE -> zenSocialNoise
            MoriEngineStateIndices.INDEX_LIGHT_LEVEL -> atmosLightLevel
            else -> 0.0f
        }
    }
}
