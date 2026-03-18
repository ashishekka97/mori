package me.ashishekka.mori.di

import android.service.wallpaper.WallpaperService
import me.ashishekka.mori.engine.core.MoriEngine
import me.ashishekka.mori.engine.core.interfaces.EngineTicker
import me.ashishekka.mori.engine.core.interfaces.RenderSurface
import me.ashishekka.mori.engine.renderer.EffectRenderer
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
                kotlinx.coroutines.CoroutineScope::class,
                android.view.Choreographer::class
            )
        )
    }
}
