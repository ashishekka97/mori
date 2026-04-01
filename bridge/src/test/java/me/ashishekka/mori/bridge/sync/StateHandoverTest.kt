package me.ashishekka.mori.bridge.sync

import me.ashishekka.mori.engine.core.MoriEngineState
import me.ashishekka.mori.engine.core.MoriEngineStateIndices
import me.ashishekka.mori.persona.state.WorldState
import org.junit.Assert.assertEquals
import org.junit.Test

class StateHandoverTest {

    @Test
    fun `sync should map semantic WorldState fields to agnostic fact slots`() {
        // Given
        val world = WorldState(
            chronosSunAltitude = 0.2f,
            vitalityStepsProgress = 0.5f,
            zenSocialNoise = 0.11f,
            energyBatteryLevel = 0.44f,
            energyIsCharging = true,
            energyThermalStress = 0.55f,
            atmosLightLevel = 0.66f
        )
        val engine = MoriEngineState()

        // When
        StateHandover.sync(world, engine)

        // Then
        assertEquals(world.chronosSunAltitude, engine.getFieldValue(MoriEngineStateIndices.FACT_SUN_ALTITUDE))
        assertEquals(world.vitalityStepsProgress, engine.getFieldValue(MoriEngineStateIndices.FACT_STEPS_PROGRESS))
        assertEquals(world.zenSocialNoise, engine.getFieldValue(MoriEngineStateIndices.FACT_SOCIAL_NOISE))
        assertEquals(world.energyBatteryLevel, engine.getFieldValue(MoriEngineStateIndices.FACT_BATTERY_LEVEL))
        assertEquals(1.0f, engine.getFieldValue(MoriEngineStateIndices.FACT_IS_CHARGING))
        assertEquals(world.energyThermalStress, engine.getFieldValue(MoriEngineStateIndices.FACT_THERMAL_STRESS))
        assertEquals(world.atmosLightLevel, engine.getFieldValue(MoriEngineStateIndices.FACT_LIGHT_LEVEL))

        // --- Platform Metadata ---
        val engineWithSurface = MoriEngineState().apply {
            surfaceWidth = 1000
            surfaceHeight = 2000
        }
        StateHandover.sync(world, engineWithSurface)
        
        assertEquals(2.0f, engineWithSurface.getFieldValue(MoriEngineStateIndices.FACT_ASPECT_RATIO)) // 2000/1000
        assertEquals(0.0f, engineWithSurface.getFieldValue(MoriEngineStateIndices.FACT_IS_LANDSCAPE)) // Portrait
        assertEquals(0.5f, engineWithSurface.getFieldValue(MoriEngineStateIndices.FACT_FIELD_RATIO)) // 1000/2000
    }
}
