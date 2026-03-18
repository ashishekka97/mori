package me.ashishekka.mori.engine.renderer

import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyOrder
import me.ashishekka.mori.engine.core.MoriEngineState
import me.ashishekka.mori.engine.core.interfaces.EngineCanvas
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LayerManagerTest {

    @Test
    fun `addEffect should sort layers by zOrder`() {
        // Given
        val layerManager = LayerManager(maxLayers = 3)
        val background = mockk<EffectRenderer>(relaxed = true) { every { zOrder } returns 0 }
        val midground = mockk<EffectRenderer>(relaxed = true) { every { zOrder } returns 10 }
        val foreground = mockk<EffectRenderer>(relaxed = true) { every { zOrder } returns 20 }
        
        val state = MoriEngineState()
        val canvas = mockk<EngineCanvas>()

        // When (Add in mixed order)
        layerManager.addEffect(midground)
        layerManager.addEffect(foreground)
        layerManager.addEffect(background)

        layerManager.updateAndDraw(state, canvas)

        // Then (Verify draw order: background -> midground -> foreground)
        verifyOrder {
            background.updateAndDraw(state, canvas)
            midground.updateAndDraw(state, canvas)
            foreground.updateAndDraw(state, canvas)
        }
    }

    @Test
    fun `addEffect should return false when full`() {
        // Given
        val layerManager = LayerManager(maxLayers = 1)
        val effect1 = mockk<EffectRenderer>(relaxed = true) { every { zOrder } returns 1 }
        val effect2 = mockk<EffectRenderer>(relaxed = true) { every { zOrder } returns 2 }

        // When / Then
        assertTrue(layerManager.addEffect(effect1))
        assertFalse(layerManager.addEffect(effect2))
    }
}
