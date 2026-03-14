# Architecture & Data Flow

This project relies on a strict unidirectional data flow, enforced by Gradle modules, to protect the rendering thread from Android framework overhead.

## The 5 Modules

1. **App Layer (`:app`)**
   - The master builder. Contains `MoriApplication`, the master Koin graph, and the `MainActivity` host.

2. **UI Layer (`:ui`)**
   - Pure Jetpack Compose.
   - Handles progressive disclosure of permissions.
   - Communicates with the Persona layer to show current data status.

3. **Persona Layer (`:persona`)**
   - The "Brain" of the app.
   - Uses `BroadcastReceivers` for real-time data.
   - Uses `WorkManager` for periodic data.
   - Flattens all data into a single `StateFlow<WorldState>`.

4. **Biome Layer (`:biome`)**
   - Reads `config.json` files that define how the environment reacts to the `WorldState`.
   - Pre-decodes all `Bitmaps` into memory during initialization.
   - Maps JSON string triggers to specific `EffectRenderer` classes.

5. **Engine Layer (`:engine`)**
   - The "Muscle".
   - Collects the `StateFlow<WorldState>` directly (No ViewModels).
   - Iterates through the active `EffectRenderer` list and calls `.updateAndDraw(canvas)` 30 times a second.
   - *Note: Jetpack Compose and standard Android UI libraries are strictly banned in this module's `build.gradle.kts`.*

## The Handshake (Example Flow)
`BatteryReceiver` (`:persona`) triggers -> Updates `WorldStateManager` -> Emits new `WorldState(isCharging = true)` -> `MoriWallpaperService` (`:engine`) receives state -> Checks current `BiomeConfig` (`:biome`) -> Sees "charging" triggers `ProceduralAurora` -> Engine calls `aurora.updateAndDraw()` on the next frame.