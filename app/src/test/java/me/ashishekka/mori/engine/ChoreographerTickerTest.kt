package me.ashishekka.mori.engine

import android.view.Choreographer
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class ChoreographerTickerTest {

    private lateinit var mockChoreographer: Choreographer
    private lateinit var ticker: ChoreographerTicker

    @Before
    fun setUp() {
        mockChoreographer = mockk(relaxed = true)
        ticker = ChoreographerTicker(mockChoreographer)
    }

    @Test
    fun `start should request tick and register frame callback`() {
        ticker.start()
        verify(exactly = 1) { mockChoreographer.postFrameCallback(ticker) }
    }

    @Test
    fun `stop should unregister frame callback`() {
        ticker.start()
        ticker.stop()
        verify { mockChoreographer.removeFrameCallback(ticker) }
    }

    @Test
    fun `doFrame should not re-register callback if not continuous`() {
        // Given
        ticker.start()
        ticker.setContinuous(false)

        clearMocks(mockChoreographer, answers = false)

        // When
        ticker.doFrame(1_000_000_000L)

        // Then
        verify(exactly = 0) { mockChoreographer.postFrameCallback(ticker) }
    }

    @Test
    fun `requestTick should unregister and post callback if running`() {
        // Given
        ticker.start() // Sets running = true
        clearMocks(mockChoreographer, answers = false)

        // When
        ticker.requestTick()

        // Then
        verify(exactly = 1) { mockChoreographer.removeFrameCallback(ticker) }
        verify(exactly = 1) { mockChoreographer.postFrameCallback(ticker) }
    }

    @Test
    fun `requestTick should do nothing if not running`() {
        // Given
        ticker.stop() // Sets running = false
        clearMocks(mockChoreographer, answers = false)

        // When
        ticker.requestTick()

        // Then
        verify(exactly = 0) { mockChoreographer.postFrameCallback(ticker) }
    }
}
