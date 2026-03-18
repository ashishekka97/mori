package me.ashishekka.mori.engine

import android.view.Choreographer
import me.ashishekka.mori.engine.core.interfaces.EngineTicker

/**
 * Android implementation of [EngineTicker] using the system [Choreographer].
 */
class ChoreographerTicker(
    private val choreographer: Choreographer = Choreographer.getInstance()
) : EngineTicker, Choreographer.FrameCallback {

    private var isRunning = false
    private var isContinuous = true
    private var tickCallback: ((Long) -> Unit)? = null

    override fun start() {
        if (!isRunning) {
            isRunning = true
            requestTick()
        }
    }

    override fun stop() {
        isRunning = false
        choreographer.removeFrameCallback(this)
    }

    override fun setContinuous(enabled: Boolean) {
        isContinuous = enabled
        if (enabled && isRunning) {
            requestTick()
        }
    }

    override fun requestTick() {
        if (isRunning) {
            choreographer.removeFrameCallback(this)
            choreographer.postFrameCallback(this)
        }
    }

    override fun setOnTickCallback(callback: (frameTimeNanos: Long) -> Unit) {
        this.tickCallback = callback
    }

    override fun doFrame(frameTimeNanos: Long) {
        if (!isRunning) return

        tickCallback?.invoke(frameTimeNanos)

        if (isContinuous) {
            choreographer.postFrameCallback(this)
        }
    }
}
