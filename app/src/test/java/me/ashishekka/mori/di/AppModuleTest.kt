package me.ashishekka.mori.di

import android.service.wallpaper.WallpaperService
import android.view.Choreographer
import kotlinx.coroutines.CoroutineScope
import me.ashishekka.mori.engine.core.MoriEngine
import me.ashishekka.mori.engine.core.MoriEngineState
import me.ashishekka.mori.engine.core.interfaces.EngineTicker
import me.ashishekka.mori.engine.core.interfaces.RenderSurface
import me.ashishekka.mori.engine.renderer.EffectRenderer
import me.ashishekka.mori.persona.sensor.LunarCalculator
import me.ashishekka.mori.persona.sensor.SolarCalculator
import me.ashishekka.mori.persona.sensor.ThermalListenerProvider
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.verify.verify

class AppModuleTest : KoinTest {

    @Test
    fun `verify entire dependency graph`() {
        appModule.verify(
            extraTypes = listOf(
                WallpaperService.Engine::class,
                EngineTicker::class,
                RenderSurface::class,
                EffectRenderer::class,
                MoriEngineState::class,
                ThermalListenerProvider::class,
                SolarCalculator::class,
                LunarCalculator::class,
                CoroutineScope::class,
                Choreographer::class
            )
        )
    }
}
