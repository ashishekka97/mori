package me.ashishekka.mori.persona.sensor

/**
 * A sealed hierarchy representing all possible data updates from [StateProvider]s.
 * This allows the StateManager to handle updates in a type-safe, unified way.
 */
sealed class StateUpdate {
    data class Energy(
        val batteryLevel: Float,
        val isCharging: Boolean,
        val isPowerSaveMode: Boolean
    ) : StateUpdate()

    // Future: data class Chronos(...), data class Zen(...)
}
