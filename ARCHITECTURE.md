# Architecture: The Agnostic Platform

Mori is built on a strict unidirectional data flow, enforced by Gradle modules. This architecture protects the rendering thread from Android framework overhead and ensures zero-allocation performance at 60 FPS.

## 1. The "Mori Machine" (Visual Flow)

Phase 6 introduces the **"Data-Driven Brain,"** a stack-based VM that interprets high-level Biome rules into real-time visual properties.

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

    subgraph Biome [Biome Layer - The Logic Engine]
        JSON[Biome DSL] -->|Parser| Bytecode[OpCode Arrays]
        G -->|Input| VM{RuleEvaluator}
        Bytecode -->|Execute| VM
        VM -->|Fill| Buffer[Property Buffers]
    end

    subgraph Engine [Engine Layer - The "Dumb" Muscle]
        Buffer -->|Read| Instruments(DslEffectRenderer)
        Instruments -->|Draw| M[Agnostic Canvas]
    end
```

---

## 2. The Rule Engine (Phase 6 Foundations)

To achieve maximum scalability without technical debt, Phase 6 replaces hardcoded Kotlin logic with a **Flyweight Data Architecture.**

### Macro-OpCode VM
Instead of interpreting strings at runtime, Mori "compiles" JSON expressions once into primitive `IntArray` bytecode. A high-performance, stack-based evaluator executes these macros (Oscillate, Remap, Step) 60 times per second using a pre-allocated `FloatArray` stack.

### Property Buffers (Flat Memory)
Each layer in a Biome owns a pre-allocated `FloatArray`. The Rule Engine writes the results of its calculations (X, Y, Rotation, Scale, Alpha, Tint) directly into this buffer. The Renderers then perform "dumb" drawing by reading from these fixed indices, ensuring **Zero-Allocation** during the entire frame cycle.

---

## 3. The Visual Pipeline (Phase 7 Foundations)

Phase 7 bridges the Rule Engine's logic to high-fidelity assets.

### Bitmap Texture Atlas
To minimize GPU state changes, multiple biome assets are packed into a single large texture. The `EngineCanvas` is expanded to support "Bit-Blit" rendering (drawing specific source regions from the atlas).

### AGSL Shader Bridge
Mori supports **Android Graphics Shading Language (AGSL)** for pixel-level effects like water reflections and volumetric fog. The Rule Engine maps dynamic state values directly to Shader Uniforms, enabling a "Living Atmosphere" that reacts to persona data at the pixel level.

---

## 4. The 5 Modules

1.  **App Layer (`:app`)**: The Orchestrator & UI Bridge.
2.  **UI Layer (`:ui`)**: The Agnostic Design System (Pulse).
3.  **Persona Layer (`:persona`)**: The Brain (Sensor collection and normalization).
4.  **Biome Layer (`:biome`)**: The Logic Engine (DSL Parsing and OpCode Evaluation).
5.  **Engine Layer (`:engine`)**: The Muscle (Platform-agnostic rendering instruments).

---

## 8. Phase 5 Retrospective: The Unified Design System
**Status:** Completed (March 2026)
[... Existing Phase 5 retro content ...]
