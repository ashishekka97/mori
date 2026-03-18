package me.ashishekka.mori.engine.core

/**
 * A mutable, pre-allocated mirror of the Persona's WorldState.
 * This class is a "Flat Memory" object designed for zero-allocation access
 * by the rendering thread. 
 *
 * All fields are primitives.
 * Phase 3 (Bridge) will handle field-by-field updates from WorldState.
 */
class MoriEngineState {
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
    
    // === SURFACE (Geometry) ===
    var surfaceWidth: Int = 0
    var surfaceHeight: Int = 0
    var surfaceDensity: Float = 1f
}
