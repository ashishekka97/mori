package me.ashishekka.mori.persona.di

import android.content.Context
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.verify.verify

class PersonaModuleTest : KoinTest {

    @Test
    fun `verify personaModule dependency graph`() {
        personaModule.verify(
            extraTypes = listOf(
                Context::class
            )
        )
    }
}
