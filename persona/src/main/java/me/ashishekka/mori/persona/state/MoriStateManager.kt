package me.ashishekka.mori.persona.state

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import me.ashishekka.mori.persona.sensor.StateProviderRegistry
import me.ashishekka.mori.persona.sensor.StateUpdate

/**
 * The internal implementation of the Mori State Hub.
 * Uses [MutableStateFlow] to provide thread-safe, atomic updates and reactive observation.
 * 
 * Orchestrates multiple [StateProvider]s to populate the [WorldState].
 */
internal class MoriStateManager(
    private val registry: StateProviderRegistry,
    private val scope: CoroutineScope
) : MutableStateManager {

    private val _state = MutableStateFlow(WorldState())

    override val state: StateFlow<WorldState> = _state.asStateFlow()

    init {
        // Collect from all providers in the registry
        registry.data
            .onEach { update ->
                handleUpdate(update)
            }
            .launchIn(scope)
    }

    override fun update(transform: (WorldState) -> WorldState) {
        _state.update(transform)
    }

    private fun handleUpdate(update: StateUpdate) {
        when (update) {
            is StateUpdate.Energy -> {
                update { current ->
                    current.copy(
                        energyBatteryLevel = update.batteryLevel,
                        energyIsCharging = update.isCharging,
                        energyThermalStress = if (update.isPowerSaveMode) 1f else 0f
                    )
                }
            }
            is StateUpdate.Chronos -> {
                update { current ->
                    current.copy(
                        chronosTimeProgress = update.dayProgress
                    )
                }
            }
            is StateUpdate.Zen -> {
                update { current ->
                    current.copy(
                        zenIsDndActive = update.isDndActive,
                        // Media activity adds a subtle "vibrancy" to the activity intensity
                        vitalityActivityIntensity = if (update.isMusicActive) 0.2f else 0f
                    )
                }
            }
            is StateUpdate.Solar -> {
                update { current ->
                    current.copy(
                        chronosSunAltitude = update.altitude
                    )
                }
            }
        }
    }
}
