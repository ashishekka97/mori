# Architecture: The Agnostic Platform

Mori is built on a strict unidirectional data flow, enforced by Gradle modules. This architecture protects the rendering thread from Android framework overhead and ensures zero-allocation performance at 60 FPS.

## 1. The "Mori Machine" (Visual Flow)

```mermaid
graph TD
    subgraph OS [Android OS Layer]
        A[Sensor Events / Broadcasts] -->|Real-time| B
        C[Health Connect / UsageStats] -->|Periodic| D
    end

    subgraph Persona [Persona Layer - The Brain]
        B(Pulse Collectors) -->|Update| E
        D(Narrative WorkManager) -->|Update| E
        E{WorldStateManager} -->|Emit Immutable| F[WorldState Flow]
    end

    subgraph Bridge [Bridge Layer - The Handover]
        F --> G(Synchronizer)
        G -->|Atomic Copy| H[MoriEngineState - Mutable Mirror]
    end

    subgraph Engine [Engine Layer - The Muscle]
        H --> I(RenderLoop)
        I --> J{EffectRegistry}
        J --> K[EffectRenderer 1: Sky]
        J --> L[EffectRenderer 2: Flora]
        K & L -->|Zero-Allocation Draw| M[Agnostic Canvas]
    end
```

---

## 2. The 5 Modules

1.  **App Layer (`:app`)**
    *   **Role:** The Orchestrator.
    *   **Responsibilities:** Manages the `WallpaperService` lifecycle, initializes the Koin dependency graph, and binds the Persona's data lifecycle to the Engine's visibility. Implements Android-specific components like `ChoreographerTicker` and `SurfaceHolderRenderSurface`.

2.  **UI Layer (`:ui`)**
    *   **Role:** The Face (Pulse Design System).
    *   **Responsibilities:** Atmospheric onboarding, progressive permission disclosure, and the Data-as-Art dashboard. Built with Jetpack Compose.

3.  **Persona Layer (`:persona`)**
    *   **Role:** The Brain.
    *   **Responsibilities:** 
        *   **Pulse:** `BroadcastReceivers` for real-time events (Battery, DND, Time).
        *   **Narrative:** `WorkManager` for high-latency background data (Health, Usage Summaries).
        *   **Normalization:** Flattens all data into a single, primitive-only `WorldState`.

4.  **Biome Layer (`:biome`)**
    *   **Role:** The DSL & Assets.
    *   **Responsibilities:** Interprets declarative JSON configurations (The Rule Engine). Manages the `BitmapTextureAtlas` and maps triggers to visual properties.

5.  **Engine Layer (`:engine`)**
    *   **Role:** The Muscle (Rendering VM).
    *   **Responsibilities:** A platform-agnostic rendering core. Interacts with the platform via `EngineTicker` and `RenderSurface` interfaces. Consumes raw pixel data from the `MoriEngineState` mirror. Strictly isolated from the Android Framework.

---

## 3. The Smart Handover (Sync Strategy)

To achieve **Zero-Allocation** in the rendering loop, we use a **Mirror Sync** protocol managed by the **`:bridge`** module:

1.  **The Snapshot:** The Persona layer emits an immutable `WorldState` data class (The Brain).
2.  **The Collection:** The **StateSynchronizer** (in the Bridge) collects this snapshot on a dedicated background thread.
3.  **The Atomic Copy:** Values are copied field-by-field into a pre-allocated **MoriEngineState** (The Mirror).
4.  **The Geometry Translation:** The Bridge applies screen density corrections to any DP-based values, converting them into raw Pixels.
5.  **The Draw:** The **Engine** reads directly from the mirror's primitive fields during its 16ms draw window.

This ensures that the rendering thread never touches the Android Framework, never allocates memory, and never encounters a `ConcurrentModificationException`.

---

## 4. Phase 1 Retrospective: The Agnostic Platform

**Status:** Completed (March 2026)

### Summary of Decisions
1.  **Strict UDF (StateManager):** We split the state hub into read-only (`StateManager`) and internal-only (`MutableStateManager`) interfaces to physically prevent state mutation from outside the `:persona` module.
2.  **Service-as-Orchestrator:** Moved `MoriWallpaperService` to the `:app` module. This ensures the `:engine` module remains a pure drawing library while `:app` manages the complex binding between the "Brain" (Persona) and the "Muscle" (Engine).
3.  **Feature-Based Hierarchy:** Reorganized modules into `.state`, `.lifecycle`, `.sensor`, and `.core` packages to support long-term scalability and internal encapsulation.
4.  **Decoupled Rendering:** Extracted `EngineTicker` and `RenderSurface` interfaces to isolate the `:engine` module from the Android Framework (`Canvas`, `Choreographer`, `SurfaceHolder`). This enables platform-agnostic testing and future-proofs the rendering logic.

---

## 5. Phase 2 Retrospective: The Pulse Engine

**Status:** Completed (March 2026)

### Summary of Decisions
1.  **Zero-Allocation Core:** Implemented `LayerManager` using a fixed-size pre-allocated array (16 layers) and manual indexing. This ensures that the rendering loop never triggers the Garbage Collector, even with multiple active effects.
2.  **Agnostic Timing:** Decoupled the rendering heartbeat from the Android `Choreographer`. The engine now consumes a generic `onTick(nanos)` signal, allowing for deterministic unit testing of frame-skipping and FPS capping logic.
3.  **Flat-Memory Mirror (`MoriEngineState`):** Created a mutable primitive-only mirror of the `WorldState`. This allows the bridge to copy data once per frame, while renderers read from a stable, thread-safe memory block without allocations.
4.  **Constructor-Injected Rendering:** Refactored `MoriEngine` to accept all its managers (`Ticker`, `Surface`, `LayerManager`) via constructor injection. This enabled 100% test coverage of the engine's core orchestration logic.

### State of the Machine
*   **The Heartbeat (:engine):** Robust. Capable of 30/60 FPS toggling, Z-Order layer management, and automatic fallback to a safe static state on failure. Includes `DebugPulseRenderer` for visual validation.
*   **The Plumbing (:app):** Ready. The `ChoreographerTicker` and `SurfaceHolderRenderSurface` provide the high-performance Android bindings needed for the engine to breathe.

### Visual Validation (Smoke Test)
We implemented a `DebugPulseRenderer` that oscillates the background color between deep purple and black. This proves that the multi-module orchestration is working:
1.  **Ticker:** Driving the loop via Android Choreographer.
2.  **Manager:** Correctfully sorting and iterating layers.
3.  **Canvas:** Succesfully drawing platform-agnostic pixels to the Android surface.
