package me.ashishekka.mori.engine.core

import io.mockk.every
import io.mockk.mockk
import me.ashishekka.mori.engine.renderer.EffectRenderer
import me.ashishekka.mori.engine.renderer.RendererPalette
import org.junit.Assert.assertEquals
import org.junit.Test

class MoriWallpaperTest {

    @Test
    fun `synthesizePalette should respect granular weights`() {
        // Given
        val layer1 = mockk<EffectRenderer> {
            every { getPaletteContribution() } returns RendererPalette(
                accent = 0xFFFF0000.toInt(), // Red
                accentWeight = 0.9f,         // High accent weight
                foundation = 0xFF00FF00.toInt(), // Green
                foundationWeight = 0.1f      // Low foundation weight
            )
        }
        val layer2 = mockk<EffectRenderer> {
            every { getPaletteContribution() } returns RendererPalette(
                accent = 0xFF0000FF.toInt(), // Blue
                accentWeight = 0.1f,         // Low accent weight
                foundation = 0xFFFFFF00.toInt(), // Yellow
                foundationWeight = 0.9f      // High foundation weight
            )
        }

        val wallpaper = MoriWallpaper("test", listOf(layer1, layer2))
        val state = MoriEngineState()

        // When
        wallpaper.synthesizePalette(state)

        // Then
        assertEquals(0xFFFF0000.toInt(), state.dominantAccentColor) // Red wins (Higher accent weight)
        assertEquals(0xFFFFFF00.toInt(), state.dominantFoundationColor) // Yellow wins (Higher foundation weight)
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
