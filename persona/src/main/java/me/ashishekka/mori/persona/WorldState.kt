package me.ashishekka.mori.persona

/**
 * A flattened, performance-optimized snapshot of the device's current state.
 * All fields are primitives to ensure zero-allocation updates via .copy().
 * Most values are normalized (0.0 to 1.0) to simplify rendering math in the Engine.
 */
data class WorldState(
    // === CHRONOS (Time & Cycles) ===
    val chronosTimeProgress: Float = 0f,      // 0.0 (Midnight) -> 1.0 (11:59 PM)
    val chronosSunAltitude: Float = 0f,       // -1.0 (Midnight) -> 0.0 (Horizon) -> 1.0 (Noon)
    val chronosMoonPhase: Float = 0f,         // 0.0 (New Moon) -> 0.5 (Full) -> 1.0 (New)
    val chronosSeasonProgress: Float = 0f,    // 0.0 (Spring) -> 1.0 (Summer) -> 2.0 (Autumn) -> 3.0 (Winter)
    val chronosIsWeekend: Boolean = false,

    // === VITALITY (Health & Activity) ===
    val vitalityStepsProgress: Float = 0f,    // 0.0 (0 steps) -> 1.0 (Goal met)
    val vitalityActivityIntensity: Float = 0f,// 0.0 (Still) -> 1.0 (Highly active)
    val vitalitySleepClarity: Float = 1f,     // 1.0 (Great sleep) -> 0.0 (Restless/Foggy)
    val vitalityStandGoalProgress: Float = 0f,// 0.0 (0 hours) -> 1.0 (Goal met)

    // === ZEN (Digital Wellbeing) ===
    val zenDigitalCongestion: Float = 0f,    // 0.0 (Quiet day) -> 1.0 (High screen time/Chaos)
    val zenSocialNoise: Float = 0f,           // 0.0 (Low social usage) -> 1.0 (High social usage)
    val zenContextSwitching: Float = 0f,      // 0.0 (Focused) -> 1.0 (Fragmented attention)
    val zenIsDndActive: Boolean = false,
    val zenLastInteractionAge: Float = 0f,    // 0.0 (Just used) -> 1.0 (4+ hours idle)

    // === ENERGY (Device Pulse) ===
    val energyBatteryLevel: Float = 1f,       // 0.0 (Empty) -> 1.0 (Full)
    val energyIsCharging: Boolean = false,
    val energyThermalStress: Float = 0f,      // 0.0 (Cool) -> 1.0 (Throttling)

    // === ATMOS (Environment) ===
    val atmosLightLevel: Float = 1f,          // 0.0 (Dark room) -> 1.0 (Direct sunlight)
    val atmosIsPocketed: Boolean = false      // Proximity/Light sensor combined
)
