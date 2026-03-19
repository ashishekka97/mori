package me.ashishekka.mori.bridge.di

import kotlinx.coroutines.CoroutineScope
import me.ashishekka.mori.engine.core.MoriEngine
import me.ashishekka.mori.persona.state.StateManager
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.verify.verify

class BridgeModuleTest : KoinTest {

    @Test
    fun `verify bridgeModule dependency graph`() {
        bridgeModule.verify(
            extraTypes = listOf(
                CoroutineScope::class,
                MoriEngine::class,
                StateManager::class
            )
        )
    }
}
