package me.ashishekka.mori.engine.core.models

/**
 * Defines the geometric shape of a rendering layer.
 * Using an enum ensures zero-allocation, high-speed ordinal comparisons in the rendering hot path.
 */
enum class LayerType(val type: String) {
    RECT("RECT"),
    CIRCLE("CIRCLE"),
    TRIANGLE("TRIANGLE"),
    SHADER("SHADER"),
    PATH("PATH"),
    UNKNOWN("UNKNOWN");

    companion object {
        fun fromString(value: String): LayerType {
            return entries.find { it.type.equals(value, ignoreCase = true) } ?: UNKNOWN
        }
    }
}
