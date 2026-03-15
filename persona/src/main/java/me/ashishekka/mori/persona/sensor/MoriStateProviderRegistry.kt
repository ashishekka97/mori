package me.ashishekka.mori.persona.sensor

/**
 * An internal, immutable implementation of the [StateProviderRegistry].
 * This registry holds a list of [StateProvider]s and delegates all lifecycle
 * calls to them.
 */
internal class MoriStateProviderRegistry(
    override val providers: List<StateProvider>
) : StateProviderRegistry {

    override fun start() {
        providers.forEach { it.start() }
    }

    override fun stop() {
        providers.forEach { it.stop() }
    }
}
