package me.ashishekka.mori.bridge.sync

import me.ashishekka.mori.engine.core.MoriEngineState
import me.ashishekka.mori.engine.core.MoriEngineStateIndices
import me.ashishekka.mori.persona.state.WorldState

/**
 * A centralized utility for the "Data Handover" process.
 * Maps the high-level, immutable [WorldState] to the performance-optimized,
 * mutable [MoriEngineState].
 */
object StateHandover {

    /**
     * Performs a zero-allocation sync from Persona to Engine.
     * Maps semantic Persona fields to agnostic Engine fact slots.
     */
    fun sync(from: WorldState, to: MoriEngineState) {
        // --- Chronos ---
        to.setFieldValue(MoriEngineStateIndices.FACT_SUN_ALTITUDE, from.chronosSunAltitude)
        to.setFieldValue(MoriEngineStateIndices.FACT_TIME_PROGRESS, from.chronosTimeProgress)
        to.setFieldValue(MoriEngineStateIndices.FACT_MOON_PHASE, from.chronosMoonPhase)
        to.setFieldValue(MoriEngineStateIndices.FACT_SEASON_PROGRESS, from.chronosSeasonProgress)
        to.setFieldValue(MoriEngineStateIndices.FACT_IS_WEEKEND, if (from.chronosIsWeekend) 1.0f else 0.0f)
        
        // --- Energy ---
        to.setFieldValue(MoriEngineStateIndices.FACT_BATTERY_LEVEL, from.energyBatteryLevel)
        to.setFieldValue(MoriEngineStateIndices.FACT_IS_CHARGING, if (from.energyIsCharging) 1.0f else 0.0f)
        to.setFieldValue(MoriEngineStateIndices.FACT_THERMAL_STRESS, from.energyThermalStress)
        
        // --- Vitality ---
        to.setFieldValue(MoriEngineStateIndices.FACT_STEPS_PROGRESS, from.vitalityStepsProgress)
        to.setFieldValue(MoriEngineStateIndices.FACT_ACTIVITY_INTENSITY, from.vitalityActivityIntensity)
        to.setFieldValue(MoriEngineStateIndices.FACT_SLEEP_CLARITY, from.vitalitySleepClarity)
        to.setFieldValue(MoriEngineStateIndices.FACT_STAND_GOAL_PROGRESS, from.vitalityStandGoalProgress)
        
        // --- Zen ---
        to.setFieldValue(MoriEngineStateIndices.FACT_DIGITAL_CONGESTION, from.zenDigitalCongestion)
        to.setFieldValue(MoriEngineStateIndices.FACT_SOCIAL_NOISE, from.zenSocialNoise)
        to.setFieldValue(MoriEngineStateIndices.FACT_CONTEXT_SWITCHING, from.zenContextSwitching)
        to.setFieldValue(MoriEngineStateIndices.FACT_IS_DND_ACTIVE, if (from.zenIsDndActive) 1.0f else 0.0f)
        to.setFieldValue(MoriEngineStateIndices.FACT_LAST_INTERACTION_AGE, from.zenLastInteractionAge)
        
        // --- Atmos ---
        to.setFieldValue(MoriEngineStateIndices.FACT_LIGHT_LEVEL, from.atmosLightLevel)
        to.setFieldValue(MoriEngineStateIndices.FACT_IS_POCKETED, if (from.atmosIsPocketed) 1.0f else 0.0f)
    }
}
