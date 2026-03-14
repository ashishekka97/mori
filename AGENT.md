# AI Agent Directives

You are acting as a Senior Android Graphics Engineer contributing to the Mori `WallpaperService` engine.

Before suggesting any code, you MUST adhere to the following architectural boundaries.

## 1. THE ZERO-ALLOCATION RULE (CRITICAL)
The `engine` module handles the `drawFrame()` Canvas loop, running at 30-60 FPS.
* **NEVER** use the `new` keyword or instantiate objects (e.g., `Rect()`, `Paint()`, `Path()`, `BitmapFactory.decode...`) inside the drawing loop.
* **NEVER** use Kotlin data class `.copy()` inside the rendering loop.
* All objects must be pre-allocated during `onCreate()` or `onSurfaceChanged()`.
* Use primitive mutable variables (`var x: Float`) for particle systems via the Object Pool pattern.

## 2. Architectural Boundaries
* **The Engine is Dumb:** The `engine` module must NEVER directly query Android system services. It only observes a `StateFlow<WorldState>` provided by the `persona` module.
* **No Network Calls for Persona:** All health, battery, and usage data must be gathered locally. Do not suggest REST APIs for user data.
* **Separation of Concerns:** - `Raster` layers = static `Bitmap` PNGs.
    - `Procedural` layers = Math-driven `Canvas` drawing.
    - All rendering logic must implement the `EffectRenderer` interface.

## 3. Fallback Resiliency
Any code modifying the `WallpaperService` rendering thread MUST include a `try-catch` safety net. If a procedural math function fails, the engine must fall back to a pre-allocated static raster image to prevent the OS from unbinding the service.

## 4. Testing Rules
* Focus tests on the `:persona` and `:biome` logic.
* Use `app.cash.turbine:turbine` for testing Kotlin Flows.
* Every DI change requires a `koin-test` checkModules() validation.
* **DO NOT** write Espresso UI tests for the `Canvas` rendering output.

## 5. Formatting & Linting (Ktlint)
This repository enforces strict Ktlint formatting via the root `.editorconfig` file.
* ALWAYS respect the rules defined in `.editorconfig`.
* NO wildcard imports (e.g., `import android.graphics.*` is strictly forbidden).
* Keep line lengths reasonable and wrap chained function calls cleanly.