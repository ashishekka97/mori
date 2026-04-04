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
*   **Decisions**:
    *   **Clipping Masks (`maskId`)**: Implemented `PATH`-based clipping to allow complex shaders and gradients to be constrained to specific geometry without bleeding.
    *   **The 1000x1000 Artboard**: Standardized the "Center-Crop" responsive strategy. Background elements now bleed to **2000 units** vertically to ensure edge-to-edge coverage on all modern aspect ratios.
    *   **OKLab Standard**: Mandated OKLab space for all `mix_oklab` operations to ensure perceptually uniform and vibrant atmospheric transitions.
    *   **Signal Propagation**: Validated the 8-slot `signal[n]` buffer for inter-layer communication, enabling complex shared logic (like Thermal Stress melting snow) with zero redundant calculations.

*   **Architectural Shifts**:
    *   Integrated AGSL shaders mapped directly from Property Buffers to GPU Uniforms. 
    *   Shifted to platform-agnostic GPU drawing paths for `PATH` layers, bypassing Android's XML overhead.
    *   Standardized the **64-layer limit** as the "Golden Rule" for balancing visual fidelity with battery life.

*   **Phase 7 Retrospective**:
    *   **The Win**: Successfully delivered a high-fidelity "Childhood Canvas" demo that utilizes the full pipeline (Rules -> Shaders -> Paths -> Masks) while maintaining **zero allocations** in the hot path. 
    *   **The Lesson**: Orientation-aware composition (Portrait vs. Landscape) revealed that strict 1:1 artboards require careful "Safe Zone" planning. This led to the formalization of the **Aspect Ratio Fact (`fact[24]`)** for responsive anchoring.
    *   **The State of the Machine**: Mori is no longer just a "Rule Engine"—it is now a **High-Performance Graphics Platform**. The connection from real-world sensors to GPU-accelerated vector art is complete, verified, and documented for artists.
