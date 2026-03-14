package me.ashishekka.mori.persona

/**
 * A flattened snapshot of the device's current physical and digital state.
 * The Engine layer observes this, but never modifies it.
 */
data class WorldState(
    val isCharging: Boolean = false,
    val batteryLevel: Float = 1.0f,
    val timeOfDay: Float = 0.5f // 0.0 (Midnight) to 1.0 (11:59 PM)
)
