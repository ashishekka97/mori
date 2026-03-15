package me.ashishekka.mori.persona

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * An internal implementation of the [MoriLifecycleManager].
 * This manager coordinates the overall system state and orchestrates
 * the [StateProviderRegistry]'s lifecycle.
 */
internal class DefaultMoriLifecycleManager(
    private val registry: StateProviderRegistry
) : MoriLifecycleManager {

    private val _lifecycleState = MutableStateFlow<MoriLifecycleState>(MoriLifecycleState.Stopped)

    override val lifecycleState: StateFlow<MoriLifecycleState> = _lifecycleState.asStateFlow()

    override fun onStart() {
        // Transitional state for future extensions
        _lifecycleState.value = MoriLifecycleState.Loading
        
        // Start all sensors
        registry.start()
        
        // Transition to Ready
        _lifecycleState.value = MoriLifecycleState.Ready
    }

    override fun onStop() {
        // Stop all sensors
        registry.stop()
        
        // Transition to Stopped
        _lifecycleState.value = MoriLifecycleState.Stopped
    }
}
