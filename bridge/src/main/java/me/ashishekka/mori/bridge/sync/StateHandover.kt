package me.ashishekka.mori.bridge.sync

import me.ashishekka.mori.engine.core.MoriEngineState
import me.ashishekka.mori.persona.state.WorldState

/**
 * A centralized utility for the "Data Handover" process.
 * Maps the high-level, immutable [WorldState] to the performance-optimized,
 * mutable [MoriEngineState].
 */
object StateHandover {

    /**
     * Performs a zero-allocation sync from Persona to Engine.
     */
    fun sync(from: WorldState, to: MoriEngineState) {
        // === CHRONOS ===
        to.chronosTimeProgress = from.chronosTimeProgress
        to.chronosSunAltitude = from.chronosSunAltitude
        to.chronosMoonPhase = from.chronosMoonPhase
        to.chronosSeasonProgress = from.chronosSeasonProgress
        to.chronosIsWeekend = from.chronosIsWeekend

        // === VITALITY ===
        to.vitalityStepsProgress = from.vitalityStepsProgress
        to.vitalityActivityIntensity = from.vitalityActivityIntensity
        to.vitalitySleepClarity = from.vitalitySleepClarity
        to.vitalityStandGoalProgress = from.vitalityStandGoalProgress

        // === ZEN ===
        to.zenDigitalCongestion = from.zenDigitalCongestion
        to.zenSocialNoise = from.zenSocialNoise
        to.zenContextSwitching = from.zenContextSwitching
        to.zenIsDndActive = from.zenIsDndActive
        to.zenLastInteractionAge = from.zenLastInteractionAge

        // === ENERGY ===
        to.energyBatteryLevel = from.energyBatteryLevel
        to.energyIsCharging = from.energyIsCharging
        to.energyThermalStress = from.energyThermalStress

        // === ATMOS ===
        to.atmosLightLevel = from.atmosLightLevel
        to.atmosIsPocketed = from.atmosIsPocketed
    }
}
