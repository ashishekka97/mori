package me.ashishekka.mori.persona.di

import me.ashishekka.mori.persona.lifecycle.DefaultMoriLifecycleManager
import me.ashishekka.mori.persona.lifecycle.MoriLifecycleManager
import me.ashishekka.mori.persona.sensor.MoriStateProviderRegistry
import me.ashishekka.mori.persona.sensor.StateProviderRegistry
import me.ashishekka.mori.persona.state.MoriStateManager
import me.ashishekka.mori.persona.state.MutableStateManager
import me.ashishekka.mori.persona.state.StateManager
import org.koin.dsl.module

/**
 * The Koin module for the :persona layer.
 * Defines the dependency graph for the Brain (StateManager, Sensors, and Lifecycle).
 */
val personaModule = module {

    // Central state hub. We provide both StateManager (Read) and MutableStateManager (Internal Write).
    single<MoriStateManager> { MoriStateManager() }
    single<StateManager> { get<MoriStateManager>() }
    single<MutableStateManager> { get<MoriStateManager>() }

    // Sensor aggregator. Initialized with an empty list for Phase 1.
    single<StateProviderRegistry> { MoriStateProviderRegistry(emptyList()) }

    // System lifecycle orchestrator.
    single<MoriLifecycleManager> { DefaultMoriLifecycleManager(get()) }
}
