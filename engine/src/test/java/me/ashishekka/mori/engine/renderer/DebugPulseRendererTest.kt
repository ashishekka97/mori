package me.ashishekka.mori.engine.renderer

import io.mockk.mockk
import io.mockk.verify
import me.ashishekka.mori.engine.core.MoriEngineState
import me.ashishekka.mori.engine.core.interfaces.EngineCanvas
import org.junit.Before
import org.junit.Test

class DebugPulseRendererTest {

    private val renderer = DebugPulseRenderer()
    private val state = MoriEngineState()
    private val mockCanvas = mockk<EngineCanvas>(relaxed = true)

    @Before
    fun setUp() {
        state.surfaceWidth = 1080
        state.surfaceHeight = 2400
        state.viewportSafeWidth = 1000f
        state.viewportSafeHeight = 1000f
        state.viewportSafeX = 40f
        state.viewportSafeY = 700f
        state.dominantAccentColor = 0xFFFF5252.toInt()
        renderer.onSurfaceChanged(1080, 2400, 1.0f)
    }

    @Test
    fun `render should draw core and atmospheric circles`() {
        // Given
        state.currentTimeNanos = 1_000_000_000L
        state.energyBatteryLevel = 0.5f
        renderer.update(state)

        // When
        renderer.render(mockCanvas)

        // Then
        // Verify at least the core circle is drawn at the calculated position
        val expectedCoreX = state.viewportSafeX + (state.viewportSafeWidth / 2f)
        val expectedCoreY = state.viewportSafeY + (state.viewportSafeHeight / 2f)
        verify { mockCanvas.drawCircle(expectedCoreX, expectedCoreY, any(), 0xFFFFFFFF.toInt(), true) }
    }

    @Test
    fun `update should react to thermal stress with jitter`() {
        // Given high thermal stress
        state.energyThermalStress = 1.0f
        renderer.update(state)

        // When
        renderer.render(mockCanvas)

        // Then
        // The core should NOT be at its default center due to jitter
        val defaultCoreX = state.viewportSafeX + (state.viewportSafeWidth / 2f)
        val defaultCoreY = state.viewportSafeY + (state.viewportSafeHeight / 2f)

        verify {
            mockCanvas.drawCircle(
                match { it != defaultCoreX },
                match { it != defaultCoreY },
                any(),
                0xFFFFFFFF.toInt(),
                true
            )
        }
    }

    @Test
    fun `render should draw stardust based on vitality progress`() {
        // Given some steps progress
        state.vitalityStepsProgress = 0.5f
        renderer.update(state)

        // When
        renderer.render(mockCanvas)

        // Then
        // Verify multiple circles are drawn (atmos blobs + core + stardust)
        // We expect at least (200 * 0.5) = 100 stardust particles
        verify(atLeast = 100) { mockCanvas.drawCircle(any(), any(), any(), any(), any()) }
    }
}
