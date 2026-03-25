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
    const val INDEX_DAILY_SCREEN_TIME = 8
    const val INDEX_REST_QUALITY = 9
    const val INDEX_KP_INDEX = 10
    const val INDEX_MEDIA_PULSE = 11
    const val INDEX_ALARM_DISTANCE = 12
    const val INDEX_CHARGING_SPEED = 13
    const val INDEX_NOTIFICATION_COUNT = 14
    const val INDEX_TEMPERATURE = 15
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
    var chronosSunAltitude: Float = 0f
    var chronosAlarmDistance: Float = 1f

    // === VITALITY (Human Pulse) ===
    var vitalityStepsProgress: Float = 0f
    var vitalityRestQuality: Float = 1f

    // === ZEN (Digital Noise & Focus) ===
    var zenDailyScreenTime: Float = 0f
    var zenSocialNoise: Float = 0f
    var zenNotificationCount: Float = 0f
    var zenIsDndActive: Boolean = false

    // === ENERGY (Device Pulse) ===
    var energyBatteryLevel: Float = 1f
    var energyIsCharging: Boolean = false
    var energyChargingSpeed: Float = 0f
    var energyThermalStress: Float = 0f

    // === ATMOS (Environment) ===
    var atmosLightLevel: Float = 1f
    var atmosKpIndex: Float = 0f
    var atmosTemperature: Float = 0f
    
    // === MEDIA (Vibe) ===
    var mediaPulse: Float = 0f

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
            MoriEngineStateIndices.INDEX_DAILY_SCREEN_TIME -> zenDailyScreenTime
            MoriEngineStateIndices.INDEX_REST_QUALITY -> vitalityRestQuality
            MoriEngineStateIndices.INDEX_KP_INDEX -> atmosKpIndex
            MoriEngineStateIndices.INDEX_MEDIA_PULSE -> mediaPulse
            MoriEngineStateIndices.INDEX_ALARM_DISTANCE -> chronosAlarmDistance
            MoriEngineStateIndices.INDEX_CHARGING_SPEED -> energyChargingSpeed
            MoriEngineStateIndices.INDEX_NOTIFICATION_COUNT -> zenNotificationCount
            MoriEngineStateIndices.INDEX_TEMPERATURE -> atmosTemperature
            else -> 0.0f
        }
    }
}
