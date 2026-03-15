package me.ashishekka.mori.persona

import kotlinx.coroutines.flow.StateFlow

/**
 * The StateManager is the single source of truth for the Mori WorldState.
 * It provides a reactive, read-only stream of data to all consumers (UI, Engine Bridge).
 *
 * This interface follows the Unidirectional Data Flow (UDF) principle by only
 * exposing the current state without allowing external mutations.
 */
interface StateManager {

    /**
     * A reactive stream of the current [WorldState].
     * Observers should always receive the latest state upon collection.
     */
    val state: StateFlow<WorldState>
}

/**
 * An extension of [StateManager] that allows for atomic state mutations.
 *
 * Marked as [internal] to ensure that mutation capability is strictly
 * encapsulated within the :persona module. No external module (UI, Engine)
 * can trigger state updates.
 */
internal interface MutableStateManager : StateManager {

    /**
     * Performs an atomic update to the current state.
     *
     * @param transform A function that takes the current [WorldState] and returns the updated state.
     *                  The implementation must ensure this transformation is applied atomically.
     */
    fun update(transform: (WorldState) -> WorldState)
}
