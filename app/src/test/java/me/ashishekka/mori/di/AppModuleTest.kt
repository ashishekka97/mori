package me.ashishekka.mori.di

import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.check.checkModules

class AppModuleTest : KoinTest {

    @Test
    fun `verify entire Phase 1 dependency graph`() {
        checkModules {
            modules(appModule)
        }
    }
}
