package me.ashishekka.mori.engine.di

import me.ashishekka.mori.engine.core.MoriEngine
import me.ashishekka.mori.engine.core.RuleEvaluator
import me.ashishekka.mori.engine.core.interfaces.EngineTicker
import me.ashishekka.mori.engine.core.interfaces.RenderSurface
import me.ashishekka.mori.engine.renderer.LayerManager
import org.koin.dsl.module

/**
 * Koin module for the :engine layer.
 * Contains definitions for the rendering core and its managers.
 */
val engineModule = module {
    // The high-performance logic VM
    single { RuleEvaluator() }

    // Manages the Z-Order rendering stack
    factory { LayerManager() }

    // The core rendering engine
    factory { (ticker: EngineTicker, renderSurface: RenderSurface) ->
        MoriEngine(
            ticker = ticker,
            renderSurface = renderSurface,
            layerManager = get()
        )
    }
}
