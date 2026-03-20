package me.ashishekka.mori.persona.di

import android.content.Context
import me.ashishekka.mori.persona.sensor.LunarCalculator
import me.ashishekka.mori.persona.sensor.SolarCalculator
import me.ashishekka.mori.persona.sensor.ThermalListenerProvider
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.verify.verify

class PersonaModuleTest : KoinTest {

    @Test
    fun `verify personaModule dependency graph`() {
        personaModule.verify(
            extraTypes = listOf(
                Context::class,
                ThermalListenerProvider::class,
                SolarCalculator::class,
                LunarCalculator::class
            )
        )
    }
}
