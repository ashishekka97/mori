package me.ashishekka.mori.persona

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * The internal implementation of the Mori State Hub.
 * Uses [MutableStateFlow] to provide thread-safe, atomic updates and reactive observation.
 */
internal class MoriStateManager : MutableStateManager {

    private val _state = MutableStateFlow(WorldState())

    override val state: StateFlow<WorldState> = _state.asStateFlow()

    override fun update(transform: (WorldState) -> WorldState) {
        _state.update(transform)
    }
}
