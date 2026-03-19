package me.ashishekka.mori.bridge.di

import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.verify.verify

class BridgeModuleTest : KoinTest {

    @Test
    fun `verify bridgeModule dependency graph`() {
        bridgeModule.verify()
    }
}
