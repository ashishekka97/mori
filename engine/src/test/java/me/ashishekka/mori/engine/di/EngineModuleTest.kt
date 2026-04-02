package me.ashishekka.mori.engine.di

import me.ashishekka.mori.engine.core.interfaces.AssetRegistry
import me.ashishekka.mori.engine.core.interfaces.EngineTicker
import me.ashishekka.mori.engine.core.interfaces.RenderSurface
import me.ashishekka.mori.engine.renderer.EffectRenderer
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.verify.verify

class EngineModuleTest : KoinTest {

    @Test
    fun `verify engineModule dependency graph`() {
        engineModule.verify(
            extraTypes = listOf(
                EngineTicker::class,
                RenderSurface::class,
                AssetRegistry::class,
                EffectRenderer::class
            )
        )
    }
}

