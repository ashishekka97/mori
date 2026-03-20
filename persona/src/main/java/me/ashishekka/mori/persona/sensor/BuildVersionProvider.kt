package me.ashishekka.mori.persona.sensor

import android.os.Build

/**
 * Interface to provide the current Android API version.
 * Enables clean unit testing of version-gated logic.
 */
interface BuildVersionProvider {
    val sdkInt: Int
}

class DefaultBuildVersionProvider : BuildVersionProvider {
    override val sdkInt: Int = Build.VERSION.SDK_INT
}
