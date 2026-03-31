package me.ashishekka.mori.biome.di

import me.ashishekka.mori.biome.provider.AssetBiomeProvider
import org.koin.dsl.module

/**
 * Koin module for the :biome layer.
 */
val biomeModule = module {
    single { AssetBiomeProvider(get()) }
}
