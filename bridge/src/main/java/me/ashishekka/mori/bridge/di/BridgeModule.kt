package me.ashishekka.mori.bridge.di

import kotlinx.coroutines.CoroutineScope
import me.ashishekka.mori.bridge.metrics.MetricCalculator
import me.ashishekka.mori.bridge.sync.AssetRegistryImpl
import me.ashishekka.mori.bridge.sync.StateSynchronizer
import me.ashishekka.mori.engine.core.MoriEngine
import me.ashishekka.mori.engine.core.interfaces.AssetRegistry
import org.koin.dsl.module

/**
 * Koin module for the :bridge layer.
 * Acts as the translator between Persona (Data) and Engine (Pixels).
 */
val bridgeModule = module {
    single { MetricCalculator() }
    single<AssetRegistry> { AssetRegistryImpl() }

    factory { (scope: CoroutineScope, engine: MoriEngine) ->
        StateSynchronizer(
            stateManager = get(),
            engine = engine,
            scope = scope
        )
    }
}
