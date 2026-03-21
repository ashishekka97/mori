package me.ashishekka.mori.engine.core.models

/**
 * Strategy for scaling a reference canvas to the actual device screen.
 */
enum class ScaleMode {
    /**
     * Contains the content within the screen boundaries. 
     * May result in "letterboxing" (black bars).
     */
    FIT,

    /**
     * Covers the entire screen. 
     * May result in cropping the edges of the content.
     */
    FILL
}
