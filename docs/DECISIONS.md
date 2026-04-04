# Architectural Decision Records (ADRs) & Retrospectives

This document tracks the evolution of the Mori platform, capturing key decisions, architectural shifts, and "Phase Retrospectives" for long-term context forensics.

---

## Phase 1: The Agnostic Platform
*   **Decisions**: Established strict UDF via `StateManager`. Decoupled rendering from Android `Canvas` via `EngineCanvas` interface.

## Phase 3: The Engine Bridge
*   **Decisions**: Implemented the "Stage vs. Actor" model. Centralized all DP-to-Pixel math in the Bridge to keep the Engine "dumb."

## Phase 4: Persona (Data Collectors)
*   **Decisions**: Implemented Grade-based energy ratings for sensors. Introduced the "Burst" sensor strategy for battery efficiency.

## Phase 5: Pulse Design System
*   **Decisions**: Unified the entire app UI under a single engine-driven `PulseTheme`. Refactored `PulseButton` to ensure 100% theme compliance.

## Phase 6: The Rule Engine (The Brain)
*   **Decisions**: Established the **Zero-Meaning Design Principle**, where the Engine and Persona modules process raw "Facts" without semantic knowledge. Transitioned the entire engine to a stack-based VM architecture.
*   **Architectural Shifts**: Shifted from hardcoded Kotlin logic to a declarative JSON-driven system. Introduced the "VRAM Model" for property handovers.
*   **Performance**: Achieved 100% zero-allocation status for the evaluation loop. All per-frame object creation has been eliminated.
*   **State of the Machine**: Mori is now a fully data-driven platform. The engine is "dumb," and all visual behavior is defined via external DSLs, making it extremely portable and efficient.


## Phase 7: The Visual Pipeline
*   **Decisions**: Implemented the `AssetRegistry` and a Bitmap Texture Atlas to enable zero-allocation asset rendering and unified GPU pipelines.
*   **Architectural Shifts**: Integrated AGSL shaders mapped directly from Property Buffers to GPU Uniforms. Shifted towards high-performance, platform-agnostic GPU drawing paths for `PATH` layers to bypass Android XML overhead. Expanded the maximum layer limit to 64 to support procedural bloom, parallax depth, and procedural shadows entirely driven by the DSL.
*   **State of the Machine**: The visual pipeline is now fully connected from rule evaluation to GPU rendering via `DslEffectRenderer`. Validated by the successful rendering of the high-fidelity "The Childhood Canvas" scene without breaking the zero-allocation mandate.
