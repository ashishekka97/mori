package me.ashishekka.mori.engine.renderer

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import me.ashishekka.mori.engine.core.MoriEngineState
import me.ashishekka.mori.engine.core.interfaces.EngineCanvas
import org.junit.Test

class EffectRendererTest {

    @Test
    fun `updateAndDraw should call update then render`() {
        // Given
        val mockRenderer = mockk<EffectRenderer>(relaxed = true)
        val state = MoriEngineState()
        val canvas = mockk<EngineCanvas>()

        // Ensure the default implementation is called
        every { mockRenderer.updateAndDraw(any(), any()) } answers { callOriginal() }

        // When
        mockRenderer.updateAndDraw(state, canvas)

        // Then
        verify { mockRenderer.update(state) }
        verify { mockRenderer.render(canvas) }
    }
}
