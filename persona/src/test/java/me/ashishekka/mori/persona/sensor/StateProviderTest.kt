package me.ashishekka.mori.persona.sensor

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StateProviderTest {

    /**
     * A mock implementation used to verify that the StateProviderRegistry
     * and LifecycleManager correctly orchestrate the sensor lifecycles.
     */
    class MockStateProvider : StateProvider {
        var isStarted = false
            private set
        
        override val data = kotlinx.coroutines.flow.emptyFlow<StateUpdate>()

        override fun start() {
            isStarted = true
        }

        override fun stop() {
            isStarted = false
        }
    }

    @Test
    fun `MockStateProvider should track start and stop lifecycle`() {
        val provider = MockStateProvider()
        
        assertFalse("Provider should start in a stopped state", provider.isStarted)
        
        provider.start()
        assertTrue("Provider should be started after calling start()", provider.isStarted)
        
        provider.stop()
        assertFalse("Provider should be stopped after calling stop()", provider.isStarted)
    }
}
