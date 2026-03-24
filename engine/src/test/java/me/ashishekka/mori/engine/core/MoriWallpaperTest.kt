package me.ashishekka.mori.engine.core

import io.mockk.every
import io.mockk.mockk
import me.ashishekka.mori.engine.renderer.EffectRenderer
import me.ashishekka.mori.engine.renderer.RendererPalette
import org.junit.Assert.assertEquals
import org.junit.Test

class MoriWallpaperTest {

    @Test
    fun `synthesizePalette should respect weights and prioritize high-weight layers`() {
        // Given
        val lowWeightLayer = mockk<EffectRenderer> {
            every { getPaletteContribution() } returns RendererPalette(
                accent = 0xFFFF0000.toInt(), // Red
                foundation = 0xFF00FF00.toInt(), // Green
                weight = 0.1f
            )
        }
        val highWeightLayer = mockk<EffectRenderer> {
            every { getPaletteContribution() } returns RendererPalette(
                accent = 0xFF0000FF.toInt(), // Blue
                weight = 0.9f
            )
        }

        val wallpaper = MoriWallpaper("test", listOf(lowWeightLayer, highWeightLayer))
        val state = MoriEngineState()

        // When
        wallpaper.synthesizePalette(state)

        // Then
        assertEquals(0xFF0000FF.toInt(), state.dominantAccentColor) // Blue wins (Higher weight)
        assertEquals(0xFF00FF00.toInt(), state.dominantFoundationColor) // Green wins (Only one providing foundation)
    }

    @Test
    fun `synthesizePalette should use default fallbacks if no layers contribute`() {
        val emptyWallpaper = MoriWallpaper("empty", emptyList())
        val state = MoriEngineState().apply {
            chronosSunAltitude = -1.0f // Midnight
        }

        emptyWallpaper.synthesizePalette(state)

        assertEquals(0xFF121212.toInt(), state.dominantFoundationColor)
        assertEquals(0xFF9575CD.toInt(), state.dominantAccentColor)
    }

    @Test
    fun `synthesizePalette should derive correct dark and light modes`() {
        val wallpaper = MoriWallpaper("test", emptyList())
        val state = MoriEngineState()

        // Test Dark Mode
        state.chronosSunAltitude = -0.5f
        wallpaper.synthesizePalette(state)
        assertEquals(true, state.isDarkState)
        assertEquals(0xFFFFFFFF.toInt(), state.dominantOnSurfaceColor)

        // Test Light Mode
        state.chronosSunAltitude = 0.8f
        wallpaper.synthesizePalette(state)
        assertEquals(false, state.isDarkState)
        assertEquals(0xFF000000.toInt(), state.dominantOnSurfaceColor)
    }
}
