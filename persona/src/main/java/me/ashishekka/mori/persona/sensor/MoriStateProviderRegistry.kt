package me.ashishekka.mori.persona.sensor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge

/**
 * An internal, immutable implementation of the [StateProviderRegistry].
 * This registry holds a list of [StateProvider]s and delegates all lifecycle
 * calls to them.
 */
internal class MoriStateProviderRegistry(
    override val providers: List<StateProvider>
) : StateProviderRegistry {

    override val data: Flow<StateUpdate> = providers.map { it.data }.merge()

    override fun start() {
        providers.forEach { it.start() }
    }

    override fun stop() {
        providers.forEach { it.stop() }
    }
}
