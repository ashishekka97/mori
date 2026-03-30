package me.ashishekka.mori.engine.core

import io.mockk.every
import io.mockk.mockk
import me.ashishekka.mori.engine.renderer.EffectRenderer
import me.ashishekka.mori.engine.renderer.RendererPalette
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MoriWallpaperTest {

    @Test
    fun `synthesizePalette should perform weighted blending`() {
        // Given
        val layer1 = mockk<EffectRenderer> {
            every { getPaletteContribution() } returns RendererPalette(
                accent = 0xFFFF0000.toInt(), // Red
                accentWeight = 0.9f
            )
        }
        val layer2 = mockk<EffectRenderer> {
            every { getPaletteContribution() } returns RendererPalette(
                accent = 0xFF0000FF.toInt(), // Blue
                accentWeight = 0.1f
            )
        }

        val wallpaper = MoriWallpaper("test", listOf(layer1, layer2))
        val state = MoriEngineState()

        // When
        wallpaper.synthesizePalette(state)

        // Then
        // Under OKLab weighted blending, the result should NOT be pure Red or pure Blue.
        // It should be a blended color heavily skewed toward Red (0.9 weight).
        assertNotEquals(0xFFFF0000.toInt(), state.dominantAccentColor)
        assertNotEquals(0xFF0000FF.toInt(), state.dominantAccentColor)
        
        // We verify that the "Red" component is still dominant (> 200)
        val red = (state.dominantAccentColor shr 16) and 0xFF
        assertTrue("Red component should be dominant", red > 200)
    }

    @Test
    fun `synthesizePalette should use default fallbacks if no layers contribute`() {
        val emptyWallpaper = MoriWallpaper("empty", emptyList())
        val state = MoriEngineState()
        state.setFieldValue(MoriEngineStateIndices.FACT_SUN_ALTITUDE, -1.0f)

        emptyWallpaper.synthesizePalette(state)

        assertEquals(0xFF121212.toInt(), state.dominantFoundationColor)
        assertEquals(0xFF9575CD.toInt(), state.dominantAccentColor)
    }

    @Test
    fun `synthesizePalette should derive correct dark and light modes`() {
        val wallpaper = MoriWallpaper("test", emptyList())
        val state = MoriEngineState()

        // Test Dark Mode
        state.setFieldValue(MoriEngineStateIndices.FACT_SUN_ALTITUDE, -0.5f)
        wallpaper.synthesizePalette(state)
        assertEquals(true, state.isDarkState)

        // Test Light Mode
        state.setFieldValue(MoriEngineStateIndices.FACT_SUN_ALTITUDE, 0.8f)
        wallpaper.synthesizePalette(state)
        assertEquals(false, state.isDarkState)
    }
}
