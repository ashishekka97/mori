package me.ashishekka.mori.persona.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import me.ashishekka.mori.persona.lifecycle.DefaultMoriLifecycleManager
import me.ashishekka.mori.persona.lifecycle.MoriLifecycleManager
import me.ashishekka.mori.persona.sensor.AndroidChronosProvider
import me.ashishekka.mori.persona.sensor.AndroidEnergyProvider
import me.ashishekka.mori.persona.sensor.AndroidZenProvider
import me.ashishekka.mori.persona.sensor.MoriStateProviderRegistry
import me.ashishekka.mori.persona.sensor.StateProvider
import me.ashishekka.mori.persona.sensor.StateProviderRegistry
import me.ashishekka.mori.persona.state.MoriStateManager
import me.ashishekka.mori.persona.state.MutableStateManager
import me.ashishekka.mori.persona.state.StateManager
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * The Koin module for the :persona layer.
 * Defines the dependency graph for the Brain (StateManager, Sensors, and Lifecycle).
 */
val personaModule = module {

    // === SENSORS & PROVIDERS ===
    factory<StateProvider>(named("Energy")) { AndroidEnergyProvider(get()) }
    factory<StateProvider>(named("Chronos")) { AndroidChronosProvider(get()) }
    factory<StateProvider>(named("Zen")) { AndroidZenProvider(get()) }

    single<StateProviderRegistry> {
        MoriStateProviderRegistry(
            providers = listOf(
                get(named("Energy")),
                get(named("Chronos")),
                get(named("Zen"))
            )
        )
    }

    // === STATE MANAGEMENT ===
    single(named("PersonaScope")) { CoroutineScope(Dispatchers.Default + Job()) }

    // Central state hub. We provide both StateManager (Read) and MutableStateManager (Internal Write).
    single { MoriStateManager(get(), get(named("PersonaScope"))) }
    single<StateManager> { get<MoriStateManager>() }
    single<MutableStateManager> { get<MoriStateManager>() }

    // System lifecycle orchestrator.
    single<MoriLifecycleManager> { DefaultMoriLifecycleManager(get()) }
}
