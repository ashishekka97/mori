package me.ashishekka.mori.persona

/**
 * The StateProviderRegistry is a central hub that manages a collection of [StateProvider]s.
 * It provides a unified way to start and stop all registered data collectors at once.
 *
 * This interface is [public] to allow for orchestration from higher-level modules
 * (e.g., the :app or LifecycleManager), but its concrete implementation should remain [internal].
 */
interface StateProviderRegistry : StateProvider {
    /**
     * A read-only collection of all providers currently held by the registry.
     */
    val providers: List<StateProvider>
}
