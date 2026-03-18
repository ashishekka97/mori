package me.ashishekka.mori.engine.core.interfaces

/**
 * Platform-agnostic interface for driving the rendering loop.
 */
interface EngineTicker {
    /**
     * Starts continuous ticking.
     */
    fun start()

    /**
     * Stops continuous ticking.
     */
    fun stop()

    /**
     * Toggles whether the ticker fires continuously or only on demand.
     */
    fun setContinuous(enabled: Boolean)

    /**
     * Requests a single tick.
     */
    fun requestTick()

    /**
     * Sets the callback to be executed on every tick.
     * The callback provides the frame time in nanoseconds.
     */
    fun setOnTickCallback(callback: (frameTimeNanos: Long) -> Unit)
}
