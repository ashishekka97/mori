package me.ashishekka.mori.persona.lifecycle

import kotlinx.coroutines.flow.StateFlow

/**
 * The MoriLifecycleManager is the central orchestrator that manages
 * the transition of the system's overall [MoriLifecycleState].
 *
 * This interface is [public] to allow for orchestration from the :app module
 * and reactive observation by the UI and Engine.
 */
interface MoriLifecycleManager {

    /**
     * A reactive stream of the current [MoriLifecycleState].
     */
    val lifecycleState: StateFlow<MoriLifecycleState>

    /**
     * Transitions the system to the [MoriLifecycleState.Ready] state
     * and triggers the [StateProviderRegistry.start] sequence.
     */
    fun onStart()

    /**
     * Transitions the system to the [MoriLifecycleState.Stopped] state
     * and triggers the [StateProviderRegistry.stop] sequence.
     */
    fun onStop()
}
