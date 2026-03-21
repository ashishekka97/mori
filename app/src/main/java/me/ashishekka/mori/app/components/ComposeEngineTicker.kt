package me.ashishekka.mori.app.components

import me.ashishekka.mori.engine.core.interfaces.EngineTicker

/**
 * A simple ticker implementation for Compose.
 * It doesn't drive the loop itself (the Backdrop's LaunchedEffect does),
 * but it holds the callback and handles the continuous/on-demand state logic.
 */
class ComposeEngineTicker : EngineTicker {
    private var onTick: ((Long) -> Unit)? = null
    private var isContinuous = true

    override fun setOnTickCallback(callback: (frameTimeNanos: Long) -> Unit) {
        this.onTick = callback
    }

    override fun start() {}
    override fun stop() {}

    override fun setContinuous(enabled: Boolean) {
        this.isContinuous = enabled
    }

    override fun requestTick() {
        // In the app, we usually stay continuous for the preview,
        // but we'll support manual requests for future efficiency.
    }

    fun tick(frameTimeNanos: Long) {
        if (isContinuous) {
            onTick?.invoke(frameTimeNanos)
        }
    }
}
