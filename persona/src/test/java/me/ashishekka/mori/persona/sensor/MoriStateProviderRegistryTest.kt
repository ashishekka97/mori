package me.ashishekka.mori.persona.sensor

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Assert.assertEquals
import org.junit.Test

class MoriStateProviderRegistryTest {

    private val mockProviderA = mockk<StateProvider>(relaxed = true) {
        every { energyRating } returns EnergyRating.GRADE_A
        every { data } returns emptyFlow()
    }
    private val mockProviderB = mockk<StateProvider>(relaxed = true) {
        every { energyRating } returns EnergyRating.GRADE_B
        every { data } returns emptyFlow()
    }

    @Test
    fun `energyRating should return the highest (worst) energy rating among providers`() {
        // Given a registry with GRADE_A and GRADE_B
        val registry = MoriStateProviderRegistry(listOf(mockProviderA, mockProviderB))

        // Then (GRADE_B is "higher" than GRADE_A in terms of energy cost)
        assertEquals(EnergyRating.GRADE_B, registry.energyRating)
    }

    @Test
    fun `energyRating should return GRADE_A for empty providers`() {
        val registry = MoriStateProviderRegistry(emptyList())
        assertEquals(EnergyRating.GRADE_A, registry.energyRating)
    }

    @Test
    fun `start should call start on all providers`() {
        val registry = MoriStateProviderRegistry(listOf(mockProviderA, mockProviderB))
        registry.start()
        verify { mockProviderA.start() }
        verify { mockProviderB.start() }
    }

    @Test
    fun `stop should call stop on all providers`() {
        val registry = MoriStateProviderRegistry(listOf(mockProviderA, mockProviderB))
        registry.stop()
        verify { mockProviderA.stop() }
        verify { mockProviderB.stop() }
    }
}
