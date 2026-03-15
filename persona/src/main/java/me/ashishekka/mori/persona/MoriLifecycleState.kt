package me.ashishekka.mori.persona

/**
 * Represents the high-level lifecycle state of the Mori system.
 *
 * These states allow the UI and Engine to react to the system's availability
 * (e.g., showing a loading indicator or pausing the render loop).
 */
sealed class MoriLifecycleState {

    /**
     * The system is inactive and all sensors are stopped.
     */
    object Stopped : MoriLifecycleState()

    /**
     * The system is initializing or fetching high-latency background data.
     */
    object Loading : MoriLifecycleState()

    /**
     * The system is fully initialized and sensors are emitting data.
     */
    object Ready : MoriLifecycleState()
}
