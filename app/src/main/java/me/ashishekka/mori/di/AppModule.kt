package me.ashishekka.mori.di

import android.view.Choreographer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import me.ashishekka.mori.engine.ChoreographerTicker
import me.ashishekka.mori.engine.SurfaceHolderRenderSurface
import me.ashishekka.mori.engine.core.MoriEngine
import me.ashishekka.mori.engine.core.interfaces.EngineTicker
import me.ashishekka.mori.engine.core.interfaces.RenderSurface
import me.ashishekka.mori.persona.di.personaModule
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * The main Koin module for the :app layer.
 * Aggregates all sub-modules from other layers (Persona, Engine, etc.).
 */
val appModule = module {
    // Phase 1: Only includes the Persona (Brain) layer components.
    includes(personaModule)

    // Android Framework
    single { Choreographer.getInstance() }

    // Engine Core
    factory<EngineTicker> { ChoreographerTicker(get()) }
    
    // RenderSurface is special because it needs the Engine instance
    factory<RenderSurface> { (engine: android.service.wallpaper.WallpaperService.Engine) -> 
        SurfaceHolderRenderSurface(engine) 
    }

    factory { (ticker: EngineTicker, renderSurface: RenderSurface) ->
        MoriEngine(ticker, renderSurface)
    }

    // Scopes
    single(named("EngineScope")) { CoroutineScope(Dispatchers.Main + Job()) }
}
