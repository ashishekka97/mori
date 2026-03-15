package me.ashishekka.mori.persona.di

import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.check.checkModules

class PersonaModuleTest : KoinTest {

    @Test
    fun `verify personaModule dependency graph`() {
        checkModules {
            modules(personaModule)
        }
    }
}
