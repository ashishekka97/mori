package me.ashishekka.mori.bridge.sync

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.ashishekka.mori.engine.core.MoriEngine
import me.ashishekka.mori.engine.core.models.ScaleMode
import me.ashishekka.mori.persona.state.StateManager
import me.ashishekka.mori.persona.state.WorldState

/**
 * Synchronizes the immutable [WorldState] from the Persona layer
 * to the mutable [MoriEngineState] used for rendering.
 */
class StateSynchronizer(
    private val stateManager: StateManager,
    private val engine: MoriEngine,
    private val scope: CoroutineScope
) {
    private var syncJob: Job? = null

    /**
     * Starts the continuous synchronization loop.
     */
    fun start() {
        if (syncJob != null) return
        
        syncJob = stateManager.state
            .onEach { worldState ->
                StateHandover.sync(worldState, engine.state)
                engine.requestFrame()
            }
            .launchIn(scope)
    }

    /**
     * Stops the synchronization loop.
     */
    fun stop() {
        syncJob?.cancel()
        syncJob = null
    }

    /**
     * Updates the viewport dimensions in the engine state.
     */
    fun updateViewport(refW: Float, refH: Float, scaleMode: ScaleMode) {
        engine.state.referenceWidth = refW
        engine.state.referenceHeight = refH
        engine.targetScaleMode = scaleMode
    }
}
