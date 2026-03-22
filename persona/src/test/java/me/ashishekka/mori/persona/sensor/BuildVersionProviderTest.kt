package me.ashishekka.mori.persona.sensor

import org.junit.Assert.assertTrue
import org.junit.Test

class BuildVersionProviderTest {

    @Test
    fun `DefaultBuildVersionProvider should return a valid SDK version`() {
        val provider = DefaultBuildVersionProvider()
        // SDK_INT is always > 0 on a real device or JVM with Android stubs
        assertTrue(provider.sdkInt >= 0)
    }
}
