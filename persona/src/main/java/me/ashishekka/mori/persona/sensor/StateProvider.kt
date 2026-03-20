package me.ashishekka.mori.persona.sensor

import kotlinx.coroutines.flow.Flow

/**
 * The StateProvider interface defines the lifecycle and data contract for all 
 * data collectors within the Mori ecosystem (e.g., Battery, Thermal, Time).
 *
 * Implementations are responsible for registering and unregistering from
 * Android system events or periodic tasks.
 */
interface StateProvider {

    /**
     * The battery impact rating of this provider.
     */
    val energyRating: EnergyRating

    /**
     * A reactive stream of updates emitted by this provider.
     */
    val data: Flow<StateUpdate>

    /**
     * Initializes the provider and begins data collection.
     * Should be called when the Mori engine is visible or active.
     */
    fun start()

    /**
     * Stops all data collection and releases any held system resources.
     * Should be called when the engine is hidden or the service is destroyed.
     */
    fun stop()
}
