package me.ashishekka.mori.engine.renderer

import me.ashishekka.mori.engine.core.MoriEngineState
import me.ashishekka.mori.engine.core.interfaces.EngineCanvas

/**
 * Manages a fixed set of [EffectRenderer] layers, ordered by Z-Order.
 * 
 * Performance:
 * - Uses a fixed-size pre-allocated array (16 layers) to avoid per-frame allocations.
 * - Sorting only happens when a new layer is added (not during draw).
 */
class LayerManager(
    private val maxLayers: Int = 16
) {
    private val layers = arrayOfNulls<EffectRenderer>(maxLayers)
    private var activeLayerCount = 0

    /**
     * Adds an effect to the stack.
     * The stack is automatically re-sorted by [EffectRenderer.zOrder].
     * 
     * @return true if added, false if the stack is full.
     */
    fun addEffect(effect: EffectRenderer): Boolean {
        if (activeLayerCount >= maxLayers) return false
        
        // Add to the end
        layers[activeLayerCount] = effect
        activeLayerCount++
        
        // Sort the active portion (Simple insertion sort for small N)
        sortLayers()
        return true
    }

    /**
     * Propagates surface changes to all active layers.
     */
    fun onSurfaceChanged(width: Int, height: Int, density: Float) {
        for (i in 0 until activeLayerCount) {
            layers[i]?.onSurfaceChanged(width, height, density)
        }
    }

    /**
     * The main rendering loop. Iterates through all active layers.
     * ZERO ALLOCATION MANDATE: Do not use iterators or higher-order functions here.
     */
    fun updateAndDraw(state: MoriEngineState, canvas: EngineCanvas) {
        var i = 0
        while (i < activeLayerCount) {
            layers[i]?.updateAndDraw(state, canvas)
            i++
        }
    }

    /**
     * Simple insertion sort based on zOrder.
     */
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

    /**
     * Removes all layers.
     */
    fun clear() {
        for (i in 0 until maxLayers) {
            layers[i] = null
        }
        activeLayerCount = 0
    }
}
