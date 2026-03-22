package me.ashishekka.mori.app.components

import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class ComposeEngineTickerTest {

    private val ticker = ComposeEngineTicker()

    @Test
    fun `tick should invoke callback when continuous is enabled`() {
        val mockCallback = mockk<(Long) -> Unit>(relaxed = true)
        ticker.setOnTickCallback(mockCallback)
        ticker.setContinuous(true)

        ticker.tick(12345L)

        verify { mockCallback(12345L) }
    }

    @Test
    fun `tick should not invoke callback when continuous is disabled`() {
        val mockCallback = mockk<(Long) -> Unit>(relaxed = true)
        ticker.setOnTickCallback(mockCallback)
        ticker.setContinuous(false)

        ticker.tick(12345L)

        verify(exactly = 0) { mockCallback(any()) }
    }
}
