package me.ashishekka.mori.bridge.sync

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.ashishekka.mori.bridge.metrics.MetricCalculator
import me.ashishekka.mori.bridge.metrics.ScaleMode
import me.ashishekka.mori.engine.core.MoriEngine
import me.ashishekka.mori.engine.core.MoriEngineState
import me.ashishekka.mori.persona.state.StateManager
import me.ashishekka.mori.persona.state.WorldState

/**
 * Synchronizes the immutable [WorldState] from the Persona layer
 * to the mutable [MoriEngineState] mirror in the Engine layer.
 * 
 * Performs manual field-by-field copying to satisfy the zero-allocation mandate.
 */
class StateSynchronizer(
    private val stateManager: StateManager,
    private val moriEngine: MoriEngine,
    private val metricCalculator: MetricCalculator,
    private val scope: CoroutineScope
) {
    private var job: Job? = null

    /**
     * Starts background synchronization.
     */
    fun start() {
        if (job?.isActive == true) return
        
        job = stateManager.state
            .onEach { worldState ->
                sync(worldState, moriEngine.state)
                moriEngine.requestFrame()
            }
            .launchIn(scope)
    }

    /**
     * Stops background synchronization.
     */
    fun stop() {
        job?.cancel()
        job = null
    }

    /**
     * Recalculates the viewport stage based on reference dimensions.
     */
    fun updateViewport(refW: Float, refH: Float, mode: ScaleMode = ScaleMode.FILL) {
        val engineState = moriEngine.state
        val scale = metricCalculator.calculateScaleFactor(refW, refH, mode)
        
        engineState.viewportReferenceScale = scale
        engineState.viewportSafeX = metricCalculator.getCenterXOffset(refW, scale)
        engineState.viewportSafeY = metricCalculator.getCenterYOffset(refH, scale)
        engineState.viewportSafeWidth = refW * scale
        engineState.viewportSafeHeight = refH * scale
    }

    /**
     * Manual primitive-to-primitive mapping.
     * CRITICAL: No allocations (.copy, new, etc.) allowed here.
     */
    private fun sync(from: WorldState, to: MoriEngineState) {
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
