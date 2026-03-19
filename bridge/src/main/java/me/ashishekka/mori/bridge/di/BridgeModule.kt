package me.ashishekka.mori.bridge.di

import kotlinx.coroutines.CoroutineScope
import me.ashishekka.mori.bridge.sync.StateSynchronizer
import me.ashishekka.mori.engine.core.MoriEngine
import org.koin.dsl.module

/**
 * Koin module for the :bridge layer.
 * Acts as the translator between Persona (Data) and Engine (Pixels).
 */
val bridgeModule = module {
    factory { (scope: CoroutineScope, engine: MoriEngine) ->
        StateSynchronizer(
            stateManager = get(),
            moriEngine = engine,
            scope = scope
        )
    }
}
