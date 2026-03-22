package me.ashishekka.mori.bridge.sync

import me.ashishekka.mori.engine.core.MoriEngineState
import me.ashishekka.mori.persona.state.WorldState
import org.junit.Assert.assertEquals
import org.junit.Test

class StateHandoverTest {

    @Test
    fun `sync should map all WorldState fields to MoriEngineState`() {
        // Given
        val world = WorldState(
            chronosTimeProgress = 0.1f,
            chronosSunAltitude = 0.2f,
            chronosMoonPhase = 0.3f,
            chronosSeasonProgress = 0.4f,
            chronosIsWeekend = true,
            vitalityStepsProgress = 0.5f,
            vitalityActivityIntensity = 0.6f,
            vitalitySleepClarity = 0.7f,
            vitalityStandGoalProgress = 0.8f,
            zenDigitalCongestion = 0.9f,
            zenSocialNoise = 0.11f,
            zenContextSwitching = 0.22f,
            zenIsDndActive = true,
            zenLastInteractionAge = 0.33f,
            energyBatteryLevel = 0.44f,
            energyIsCharging = true,
            energyThermalStress = 0.55f,
            atmosLightLevel = 0.66f,
            atmosIsPocketed = true
        )
        val engine = MoriEngineState()

        // When
        StateHandover.sync(world, engine)

        // Then
        assertEquals(world.chronosTimeProgress, engine.chronosTimeProgress)
        assertEquals(world.chronosSunAltitude, engine.chronosSunAltitude)
        assertEquals(world.chronosMoonPhase, engine.chronosMoonPhase)
        assertEquals(world.chronosSeasonProgress, engine.chronosSeasonProgress)
        assertEquals(world.chronosIsWeekend, engine.chronosIsWeekend)
        assertEquals(world.vitalityStepsProgress, engine.vitalityStepsProgress)
        assertEquals(world.vitalityActivityIntensity, engine.vitalityActivityIntensity)
        assertEquals(world.vitalitySleepClarity, engine.vitalitySleepClarity)
        assertEquals(world.vitalityStandGoalProgress, engine.vitalityStandGoalProgress)
        assertEquals(world.zenDigitalCongestion, engine.zenDigitalCongestion)
        assertEquals(world.zenSocialNoise, engine.zenSocialNoise)
        assertEquals(world.zenContextSwitching, engine.zenContextSwitching)
        assertEquals(world.zenIsDndActive, engine.zenIsDndActive)
        assertEquals(world.zenLastInteractionAge, engine.zenLastInteractionAge)
        assertEquals(world.energyBatteryLevel, engine.energyBatteryLevel)
        assertEquals(world.energyIsCharging, engine.energyIsCharging)
        assertEquals(world.energyThermalStress, engine.energyThermalStress)
        assertEquals(world.atmosLightLevel, engine.atmosLightLevel)
        assertEquals(world.atmosIsPocketed, engine.atmosIsPocketed)
    }
}
