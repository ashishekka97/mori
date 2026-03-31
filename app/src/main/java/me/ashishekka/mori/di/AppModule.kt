package me.ashishekka.mori.di

import android.view.Choreographer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import me.ashishekka.mori.app.WallpaperFactory
import me.ashishekka.mori.biome.di.biomeModule
import me.ashishekka.mori.bridge.di.bridgeModule
import me.ashishekka.mori.engine.ChoreographerTicker
import me.ashishekka.mori.engine.SurfaceHolderRenderSurface
import me.ashishekka.mori.engine.core.interfaces.EngineTicker
import me.ashishekka.mori.engine.core.interfaces.RenderSurface
import me.ashishekka.mori.engine.di.engineModule
import me.ashishekka.mori.persona.di.personaModule
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * The main Koin module for the :app layer.
 * Aggregates all sub-modules from other layers (Persona, Engine, etc.).
 */
val appModule = module {
    // Phase 1, 2, 3 & 6: Includes Persona, Engine, Bridge, and Biome layers.
    includes(personaModule, engineModule, bridgeModule, biomeModule)

    // Android Framework
    single { Choreographer.getInstance() }

    // Engine Core Implementation (Android Specific)
    factory<EngineTicker> { ChoreographerTicker(get()) }
    
    // RenderSurface is special because it needs the Engine instance
    factory<RenderSurface> { (engine: android.service.wallpaper.WallpaperService.Engine) -> 
        SurfaceHolderRenderSurface(engine) 
    }

    // Scopes
    single(named("EngineScope")) { CoroutineScope(Dispatchers.Default + Job()) }

    // Factories
    single { WallpaperFactory(get(), get()) }
}
