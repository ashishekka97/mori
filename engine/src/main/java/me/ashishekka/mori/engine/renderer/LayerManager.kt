package me.ashishekka.mori.engine.renderer

import me.ashishekka.mori.engine.core.MoriEngineState
import me.ashishekka.mori.engine.core.interfaces.EngineCanvas

/**
 * Manages a fixed set of [EffectRenderer] layers, ordered by Z-Order.
 * ZERO-ALLOCATION: Uses manual indexing for all iterations in the hot path.
 */
class LayerManager(
    private val maxLayers: Int = 32
) {
    private val layers = arrayOfNulls<EffectRenderer>(maxLayers)
    private var activeLayerCount = 0

    fun addEffect(effect: EffectRenderer): Boolean {
        if (activeLayerCount >= maxLayers) return false
        layers[activeLayerCount] = effect
        activeLayerCount++
        sortLayers()
        return true
    }

    fun removeEffect(effect: EffectRenderer): Boolean {
        var foundIndex = -1
        var i = 0
        while (i < activeLayerCount) {
            if (layers[i] == effect) {
                foundIndex = i
                break
            }
            i++
        }

        if (foundIndex != -1) {
            i = foundIndex
            while (i < activeLayerCount - 1) {
                layers[i] = layers[i + 1]
                i++
            }
            layers[activeLayerCount - 1] = null
            activeLayerCount--
            return true
        }
        return false
    }

    fun onSurfaceChanged(width: Int, height: Int, density: Float) {
        var i = 0
        while (i < activeLayerCount) {
            layers[i]?.onSurfaceChanged(width, height, density)
            i++
        }
    }

    /**
     * Updates all active layers with the current engine state.
     */
    fun update(state: MoriEngineState) {
        var i = 0
        while (i < activeLayerCount) {
            layers[i]?.update(state)
            i++
        }
    }

    /**
     * Renders all active layers onto the canvas.
     */
    fun draw(canvas: EngineCanvas) {
        var i = 0
        while (i < activeLayerCount) {
            layers[i]?.render(canvas)
            i++
        }
    }
    
    private fun sortLayers() {
        for (i in 1 until activeLayerCount) {
            val key = layers[i] ?: continue
            var j = i - 1
            
            while (j >= 0 && (layers[j]?.zOrder ?: 0) > key.zOrder) {
                layers[j + 1] = layers[j]
                j--
            }
            layers[j + 1] = key
        }
    }

    fun clear() {
        for (i in 0 until maxLayers) {
            layers[i] = null
        }
        activeLayerCount = 0
    }
}
