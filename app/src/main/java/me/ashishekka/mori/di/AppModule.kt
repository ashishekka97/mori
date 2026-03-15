package me.ashishekka.mori.di

import me.ashishekka.mori.persona.di.personaModule
import org.koin.dsl.module

/**
 * The main Koin module for the :app layer.
 * Aggregates all sub-modules from other layers (Persona, Engine, etc.).
 */
val appModule = module {
    // Phase 1: Only includes the Persona (Brain) layer components.
    includes(personaModule)
}
