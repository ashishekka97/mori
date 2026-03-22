# Architecture: The Agnostic Platform

Mori is built on a strict unidirectional data flow, enforced by Gradle modules. This architecture protects the rendering thread from Android framework overhead and ensures zero-allocation performance at 60 FPS.

## 1. The "Mori Machine" (Visual Flow)

The Phase 5 architecture introduces a sophisticated, renderer-driven theming pipeline.

```mermaid
graph TD
    subgraph OS [Android OS Layer]
        A[Sensor Events] -->|Real-time| B(Pulse Collectors)
    end

    subgraph Persona [Persona Layer - The Brain]
        B -->|StateUpdate| E{WorldStateManager}
        E -->|Immutable WorldState| F[StateSynchronizer]
    end

    subgraph Bridge [Bridge Layer - The Handover]
        F -->|Atomic Copy| G[MoriEngineState]
    end

    subgraph Engine [Engine Layer - The "Dumb" Muscle]
        G -->|Update| H(LayerManager)
        H -->|Update| I(EffectRenderer 1)
        H -->|Update| J(EffectRenderer 2)
        
        subgraph ThemeSynthesis [Wallpaper-Owned Theme]
            I -->|getPaletteContribution()| K{MoriWallpaper}
            J -->|getPaletteContribution()| K
            K -- Synthesize & Apply --> G
        end

        G -->|Draw| L(LayerManager)
        L -->|Draw| M[Agnostic Canvas]
    end
```

---

## 2. The 5 Modules

1.  **App Layer (`:app`)**
    *   **Role:** The Orchestrator & UI Bridge.
    *   **Responsibilities:** Manages the `WallpaperService` lifecycle. Hosts app-level Composables like `PulseBackdrop` that bridge the `:engine`'s state into the `:ui`'s `PulseTheme`.

2.  **UI Layer (`:ui`)**
    *   **Role:** The Agnostic Design System (Pulse).
    *   **Responsibilities:** Provides a library of pure, stateless Jetpack Compose components (`PulseButton`, `PulseCard`, etc.) and the `PulseTheme` wrapper. Has **zero knowledge** of the Mori engine.

3.  **Persona Layer (`:persona`)**
    *   **Role:** The Brain.
    *   **Responsibilities:** Collects real-world data from device sensors and broadcasts and normalizes it into the immutable `WorldState`.

4.  **Biome Layer (`:biome`)**
    *   **Role:** (Phase 6) The DSL & Rule Engine.
    *   **Responsibilities:** Will interpret declarative configurations to construct `MoriWallpaper` objects with specific `EffectRenderer` layers and palette rules.

5.  **Engine Layer (`:engine`)**
    *   **Role:** The "Dumb" Muscle (Rendering VM).
    *   **Responsibilities:** A platform-agnostic rendering core. `MoriEngine` orchestrates the rendering loop but delegates all visual and theme decisions to the active `MoriWallpaper`.

---

## 3. The "Update-First" Rendering Cycle (Phase 5)

To fix critical race conditions and ensure data integrity, the engine now follows a strict three-phase rendering cycle on every frame:

1.  **UPDATE**: `MoriEngine` calls `layerManager.update(state)`. Every active `EffectRenderer` updates its internal logic (e.g., calculates colors, positions, physics) based on the latest `MoriEngineState`.
2.  **SYNTHESIZE**: `MoriEngine` calls `currentWallpaper.synthesizePalette(state)`. The wallpaper iterates through its renderers, calls `getPaletteContribution()` on each, and aggregates the results to determine the final `dominant` theme colors for the frame.
3.  **DRAW**: `MoriEngine` calls `layerManager.draw(canvas)`. Every active `EffectRenderer` now performs its "dumb" drawing operations using the now-consistent state.

This guarantees that palette synthesis always happens *after* all layers have had a chance to update their state for the current frame.

---

## 4. The Zero-Allocation Mandate (Phase 5 Refinements)

### Renderer-Owned Palettes
Instead of a central `AtmosphericThemeMapper`, each `EffectRenderer` can now report its own theme colors via the `getPaletteContribution()` method. This returns a `RendererPalette` data class.

### Zero-Allocation Caching
To prevent `RendererPalette` from being allocated on every frame, all renderers now implement a **caching strategy**. The `getPaletteContribution` method only creates a new palette object if its underlying color values have actually changed during the `update` phase. Otherwise, it returns a cached instance, ensuring zero per-frame allocations.

---

## 8. Phase 5 Retrospective: The Unified Design System

**Status:** Completed (March 2026)

### Summary of Decisions
1.  **Renderer-Driven Theming:** The responsibility for color policy was moved from a central utility into the renderers themselves. `MoriWallpaper` now acts as a synthesizer, aggregating color contributions from its layers. This is the foundational pattern for the Phase 6 Biome DSL.
2.  **Update-First Architecture:** The core engine loop was refactored to enforce a strict `update() -> synthesize() -> draw()` cycle, eliminating a critical `UninitializedPropertyAccessException` race condition.
3.  **Zero-Allocation Hardening:** Implemented a caching pattern for `RendererPalette` contributions, ensuring the entire rendering and theming pipeline is allocation-free during steady-state operation.
4.  **Unified UI Theme:** Refactored `MainActivity` and `PulseBackdrop` to ensure a single, engine-driven `PulseTheme` is provided to all Composables on the screen, fixing a "stale read" bug where some components were not updating.
5.  **Pulse Design System Finalized:** Completed the full component suite (`PulseButton`, `PulseCard`, etc.), integrated the premium Satoshi font family, and created the `PulseGallery` for robust visual verification.

### State of the Machine
*   **The Brain (:persona):** Alive and well.
*   **The Muscle (:engine):** Architecturally pure. A "dumb" but powerful orchestrator.
*   **The Face (:ui):** A beautiful and fully dynamic, data-driven design system.
*   **The Bridge (:bridge):** Stable and performant.

The Mori platform is now a world-class example of a high-performance, data-reactive graphics and UI system on Android.
