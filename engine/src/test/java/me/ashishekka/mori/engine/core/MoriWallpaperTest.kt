package me.ashishekka.mori.engine.core

import io.mockk.every
import io.mockk.mockk
import me.ashishekka.mori.engine.renderer.EffectRenderer
import me.ashishekka.mori.engine.renderer.RendererPalette
import org.junit.Assert.assertEquals
import org.junit.Test

class MoriWallpaperTest {

    @Test
    fun `synthesizePalette should aggregate contributions from layers`() {
        // Given
        val mockLayer1 = mockk<EffectRenderer> {
            every { getPaletteContribution() } returns RendererPalette(
                accent = 0xFFFF0000.toInt(),
                foundation = 0xFF00FF00.toInt()
            )
        }
        val mockLayer2 = mockk<EffectRenderer> {
            every { getPaletteContribution() } returns RendererPalette(
                surface = 0x88FFFFFF.toInt()
            )
        }

        val wallpaper = MoriWallpaper("test", listOf(mockLayer1, mockLayer2))
        val state = MoriEngineState()

        // When
        wallpaper.synthesizePalette(state)

        // Then
        assertEquals(0xFF00FF00.toInt(), state.dominantFoundationColor) // From Layer 1
        assertEquals(0xFFFF0000.toInt(), state.dominantAccentColor)     // From Layer 1
        assertEquals(0x88FFFFFF.toInt(), state.dominantSurfaceColor)    // From Layer 2
    }

    @Test
    fun `synthesizePalette should use default fallbacks if no layers contribute`() {
        // Given
        val emptyWallpaper = MoriWallpaper("empty", emptyList())
        val state = MoriEngineState().apply {
            chronosSunAltitude = -1.0f // Midnight
        }

        // When
        emptyWallpaper.synthesizePalette(state)

        // Then
        assertEquals(0xFF121212.toInt(), state.dominantFoundationColor)
        assertEquals(0xFF9575CD.toInt(), state.dominantAccentColor)
        // Derive surface for dark state (0x4D000000)
        assertEquals(0x4D121212.toInt(), state.dominantSurfaceColor)
        assertEquals(0xFFFFFFFF.toInt(), state.dominantOnSurfaceColor)
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
