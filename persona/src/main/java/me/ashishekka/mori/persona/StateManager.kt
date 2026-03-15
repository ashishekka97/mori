package me.ashishekka.mori.persona

import kotlinx.coroutines.flow.StateFlow

/**
 * The StateManager is the single source of truth for the Mori WorldState.
 * It provides a reactive, unidirectional flow of data to all consumers (UI, Engine Bridge).
 *
 * Implementations should ensure that state transitions are atomic and thread-safe.
 */
interface StateManager {

    /**
     * A reactive stream of the current [WorldState].
     * Observers should always receive the latest state upon collection.
     */
    val state: StateFlow<WorldState>

    /**
     * Performs an atomic update to the current state.
     *
     * @param transform A function that takes the current [WorldState] and returns the updated state.
     *                  The implementation must ensure this transformation is applied atomically.
     */
    fun update(transform: (WorldState) -> WorldState)
}
