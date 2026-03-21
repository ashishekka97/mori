package me.ashishekka.mori.persona.lifecycle

import me.ashishekka.mori.persona.sensor.StateProviderRegistry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicInteger

/**
 * An internal implementation of the [MoriLifecycleManager].
 * This manager coordinates the overall system state and orchestrates
 * the [StateProviderRegistry]'s lifecycle using reference counting
 * to handle multiple active clients (e.g., App + Wallpaper Service).
 */
internal class DefaultMoriLifecycleManager(
    private val registry: StateProviderRegistry
) : MoriLifecycleManager {

    private val _lifecycleState = MutableStateFlow<MoriLifecycleState>(MoriLifecycleState.Stopped)
    override val lifecycleState: StateFlow<MoriLifecycleState> = _lifecycleState.asStateFlow()

    private val activeClients = AtomicInteger(0)

    override fun onStart() {
        val count = activeClients.incrementAndGet()
        
        // Only start the registry if this is the first client
        if (count == 1) {
            _lifecycleState.value = MoriLifecycleState.Loading
            registry.start()
            _lifecycleState.value = MoriLifecycleState.Ready
        }
    }

    override fun onStop() {
        val count = activeClients.decrementAndGet()
        
        // Ensure we don't go below zero
        if (count < 0) {
            activeClients.set(0)
            return
        }

        // Only stop the registry if this was the last client
        if (count == 0) {
            registry.stop()
            _lifecycleState.value = MoriLifecycleState.Stopped
        }
    }
}
