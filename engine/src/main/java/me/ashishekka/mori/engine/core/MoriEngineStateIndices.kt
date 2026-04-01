package me.ashishekka.mori.engine.core

/**
 * Static mapping of real-world "Facts" to integer indices.
 * This is the "Agnostic Ingress" model where the Engine treats all incoming 
 * data as anonymous numerical signals.
 * 
 * DESIGN PRINCIPLE:
 * The Engine is "dumb." It doesn't know that FACT_BATTERY_LEVEL means 
 * "device battery"—it only knows that it's a value in a buffer.
 */
object MoriEngineStateIndices {
    // --- Chronos (0-4) ---
    const val FACT_TIME_SECONDS = 0
    const val FACT_SUN_ALTITUDE = 1
    const val FACT_TIME_PROGRESS = 2
    const val FACT_MOON_PHASE = 3
    const val FACT_SEASON_PROGRESS = 4
    const val FACT_IS_WEEKEND = 5

    // --- Energy (6-8) ---
    const val FACT_BATTERY_LEVEL = 6
    const val FACT_IS_CHARGING = 7
    const val FACT_THERMAL_STRESS = 8

    // --- Vitality (9-12) ---
    const val FACT_STEPS_PROGRESS = 9
    const val FACT_ACTIVITY_INTENSITY = 10
    const val FACT_SLEEP_CLARITY = 11
    const val FACT_STAND_GOAL_PROGRESS = 12

    // --- Zen (13-17) ---
    const val FACT_DIGITAL_CONGESTION = 13
    const val FACT_SOCIAL_NOISE = 14
    const val FACT_CONTEXT_SWITCHING = 15
    const val FACT_IS_DND_ACTIVE = 16
    const val FACT_LAST_INTERACTION_AGE = 17

    // --- Atmos (18-19) ---
    const val FACT_LIGHT_LEVEL = 18
    const val FACT_IS_POCKETED = 19

    // --- Legacy / Extra (20-23) ---
    const val FACT_KP_INDEX = 20
    const val FACT_MEDIA_PULSE = 21
    const val FACT_ALARM_DISTANCE = 22
    const val FACT_NOTIFICATION_COUNT = 23
    
    // --- Platform Metadata (24-26) ---
    const val FACT_ASPECT_RATIO = 24
    const val FACT_IS_LANDSCAPE = 25
    const val FACT_FIELD_RATIO = 26

    // --- Expansion Slots (27-31) ---
    const val FACT_CUSTOM_A = 27
    const val FACT_CUSTOM_B = 28
    const val FACT_CUSTOM_C = 29
    const val FACT_CUSTOM_D = 30
    const val FACT_CUSTOM_E = 31

    const val BUFFER_SIZE = 32
}
