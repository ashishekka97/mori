package me.ashishekka.mori.persona.sensor

import android.content.Context
import android.os.PowerManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class AndroidThermalProviderTest {

    private lateinit var context: Context
    private lateinit var listenerProvider: ThermalListenerProvider
    private lateinit var thermalProvider: AndroidThermalProvider
    private lateinit var powerManager: PowerManager

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        powerManager = mockk(relaxed = true)
        listenerProvider = mockk(relaxed = true)
        
        // Mock the system service retrieval to avoid ClassCastException
        every { context.getSystemService(Context.POWER_SERVICE) } returns powerManager
        
        thermalProvider = AndroidThermalProvider(context, listenerProvider)
    }

    @Test
    fun `start should register listener only once`() {
        thermalProvider.start()
        thermalProvider.start()

        verify(exactly = 1) { listenerProvider.register(any(), any()) }
    }

    @Test
    fun `stop should unregister listener only once`() {
        thermalProvider.start()
        thermalProvider.stop()
        thermalProvider.stop()

        verify(exactly = 1) { listenerProvider.unregister(any(), any()) }
    }

    @Test
    fun `stop without start should NOT unregister`() {
        thermalProvider.stop()
        verify(exactly = 0) { listenerProvider.unregister(any(), any()) }
    }
}
