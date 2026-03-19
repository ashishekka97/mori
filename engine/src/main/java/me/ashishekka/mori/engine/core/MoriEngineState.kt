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
    
    // === VIEWPORT (Geometric Handover) ===
    // These values translate the Artist's "Reference Canvas" (e.g. 1000x1000 units)
    // into the physical pixel space of the device. This ensures the Engine stays
    // "dumb" and only performs minimal pixel math.
    
    /** The X pixel offset to the start of the safe design area (top-left). */
    var viewportSafeX: Float = 0f
    /** The Y pixel offset to the start of the safe design area (top-left). */
    var viewportSafeY: Float = 0f
    /** The actual pixel width of the scaled reference canvas. */
    var viewportSafeWidth: Float = 0f
    /** The actual pixel height of the scaled reference canvas. */
    var viewportSafeHeight: Float = 0f
    /** The multiplier used to convert design units to physical pixels. */
    var viewportReferenceScale: Float = 1f

    // === SURFACE (Geometry) ===
    var surfaceWidth: Int = 0
    var surfaceHeight: Int = 0
    var surfaceDensity: Float = 1f
}
