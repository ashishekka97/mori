package me.ashishekka.mori.di

import android.service.wallpaper.WallpaperService
import me.ashishekka.mori.engine.core.interfaces.EngineTicker
import me.ashishekka.mori.engine.core.interfaces.RenderSurface
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
                me.ashishekka.mori.engine.renderer.EffectRenderer::class,
                kotlinx.coroutines.CoroutineScope::class,
                android.view.Choreographer::class
            )
        )
    }
}
