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

    data class Chronos(
        val dayProgress: Float,
        val nextAlarmTime: Long
    ) : StateUpdate()

    data class Zen(
        val isDndActive: Boolean,
        val isMusicActive: Boolean,
        val ringerMode: Int
    ) : StateUpdate()

    data class Solar(
        val altitude: Float
    ) : StateUpdate()

    data class Lunar(
        val phase: Float
    ) : StateUpdate()

    data class Atmos(
        val lightLevel: Float
    ) : StateUpdate()

    data class Thermal(
        val stressLevel: Float
    ) : StateUpdate()

    // Future: data class Vitality(...)

}
