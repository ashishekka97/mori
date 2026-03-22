package me.ashishekka.mori.engine.renderer

import io.mockk.mockk
import io.mockk.verify
import me.ashishekka.mori.engine.core.MoriEngineState
import me.ashishekka.mori.engine.core.interfaces.EngineCanvas
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class StaticFallbackRendererTest {

    private val renderer = StaticFallbackRenderer()
    private val state = MoriEngineState()
    private val mockCanvas = mockk<EngineCanvas>(relaxed = true)

    @Test
    fun `render should draw foundation color on canvas`() {
        // Given
        state.chronosSunAltitude = -1.0f // Midnight
        renderer.update(state)

        // When
        renderer.render(mockCanvas)

        // Then
        verify { mockCanvas.drawColor(0xFF0D0221.toInt()) }
    }

    @Test
    fun `update should shift colors based on sun altitude`() {
        // Midnight
        state.chronosSunAltitude = -1.0f
        renderer.update(state)
        val midnightPalette = renderer.getPaletteContribution()
        assertNotNull(midnightPalette)
        assertEquals(0xFF0D0221.toInt(), midnightPalette?.foundation)

        // Noon
        state.chronosSunAltitude = 1.0f
        renderer.update(state)
        val noonPalette = renderer.getPaletteContribution()
        assertNotNull(noonPalette)
        assertEquals(0xFF40C4FF.toInt(), noonPalette?.foundation)
    }

    @Test
    fun `palette should be cached when values do not change`() {
        state.chronosSunAltitude = 0.5f
        renderer.update(state)
        val palette1 = renderer.getPaletteContribution()

        renderer.update(state)
        val palette2 = renderer.getPaletteContribution()

        assert(palette1 === palette2) // Reference equality
    }

    @Test
    fun `palette should be invalidated when values change`() {
        state.chronosSunAltitude = 0.5f
        renderer.update(state)
        val palette1 = renderer.getPaletteContribution()

        state.chronosSunAltitude = -0.5f
        renderer.update(state)
        val palette2 = renderer.getPaletteContribution()

        assert(palette1 !== palette2) // New object created
    }
}
