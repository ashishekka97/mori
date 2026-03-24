# Mori: Engineering & Product Roadmap (High-Resolution v3)

Mori is a zero-allocation, privacy-first Live Wallpaper engine. This roadmap focuses on **Atomic PRs**, **Battery Efficiency**, and **Architectural Purity**.

---

## Phase 1: The Agnostic Platform (Foundation)
*Goal: Build the "Operating System" that orchestrates data and lifecycles.*

### 1.1 WorldState & Hub
- [x] **1.1.1 WorldState Schema:** Define the flat-categorized data class with primitive types.
- [x] **1.1.2 StateManager Interface:** Define the contract for the central state hub.
- [x] **1.1.3 StateManager Implementation:** Implement the `MutableStateFlow` logic and atomic update functions.

### 1.2 Lifecycle & Registry
- [x] **1.2.1 StateProvider Contract:** Define the `StateProvider` interface (start/stop).
- [x] **1.2.2 Provider Registry:** Implement `StateProviderRegistry` to aggregate multiple sensors.
- [x] **1.2.3 Lifecycle Manager:** Implement `MoriLifecycleManager` with "Loading/Ready" state support.

### 1.3 Service Binding & DI
- [x] **1.3.1 Service Lifecycle:** Connect `WallpaperService` visibility to the `LifecycleManager`.
- [x] **1.3.2 Persona DI:** Setup the Koin module for Phase 1 components.
- [x] **1.3.3 Module Validation:** Add a `KoinTest` to ensure the Phase 1 graph is sound.

### 1.4 Phase 1 Finalization
- [x] **1.4.1 Phase 1 Finalization:** Documentation, Retrospective, and Tagging.

---

## Phase 2: The Pulse Engine (The Heart)
*Goal: Implement the zero-allocation rendering loop early for visual verification.*

### 2.1 Heartbeat
- [x] **2.1.1 Choreographer Loop:** Implement the basic `FrameCallback` loop in `:engine`.
- [x] **2.1.2 FPS Controller:** Add logic for 30/60 FPS toggling and frame skipping.
- [x] **2.1.3 Static Fallback:** Build the `StaticFallbackRenderer` safety net (renders a solid brand color).
- [x] **2.1.4 Tactical Realignment: Engine Coupling Debt:** Shift orchestrator responsibilities to the `:app` module to prevent expanding Phase 1 technical debt.
- [x] **2.1.5 Tactical Realignment: Ticker & Renderer:** Decouple `:engine` from Android `Choreographer` and `SurfaceHolder` by extracting them into platform-agnostic interfaces.
- [x] **2.1.6 Pulse Orchestrator:** Implement "Continuous vs On-Demand" ticking logic for battery efficiency, driven by the `:app` orchestrator.

### 2.2 Rendering Architecture
- [x] **2.2.1 Effect Interface:** Define the `EffectRenderer` contract.
- [x] **2.2.2 Layer Manager:** Implement the Z-Order system for rendering layers.
- [x] **2.2.3 Engine DI:** Setup the Koin module for the rendering core.

### 2.3 Phase 2 Finalization
- [x] **2.3.1 Phase 2 Finalization:** Documentation, Retrospective, and Tagging.

---

## Phase 3: The Engine Bridge (The Handover)
*Goal: Pass data and geometry from Persona to Engine without allocations.*

### 3.1 Data Handover
- [x] **3.1.1 MoriEngineState:** Create the mutable, pre-allocated mirror of `WorldState`.
- [x] **3.1.2 Bridge Module Setup:** Initialize the `:bridge` Android module and define its DI boundaries.
- [x] **3.1.3 State Synchronizer:** Implement the `Flow` collector that performs the zero-allocation handover from `WorldState` to `MoriEngineState`.

### 3.2 Geometry & Scaling (The Dumb Engine)
- [x] **3.2.1 Metric Calculator:** Implement DP-to-Pixel conversion logic in the Bridge.
- [x] **3.2.2 Visual Handover:** Pre-calculate visual offsets so the Engine remains purely pixel-based.

### 3.3 Phase 3 Finalization
- [x] **3.3.1 Phase 3 Finalization:** Documentation, Retrospective, and Tagging.

---

## Phase 4: Persona (Data Collectors)
*Goal: Atomic collectors for device metrics with Energy-Rated providers.*

### 4.1 Passive Pulse (Grade A - Passive)
- [x] **4.1.1 Energy Provider:** Battery level, Charging, and Power-Save status.
- [x] **4.1.2 Chronos Provider:** 1-minute resolution day progress and Next Alarm sync.
- [x] **4.1.3 System Zen:** DND status, Ringer mode, and Media active pulse.

### 4.2 Celestial Calculus (Grade A/B - Math-Heavy)
- [x] **4.2.1 Solar Math:** Local Sun position based on time and coarse location.
- [x] **4.2.2 Lunar Math:** Local Moon phase and illumination calculations.

### 4.3 Environmental Snapshots (Grade B - Gated)
- [x] **4.3.1 Atmos Provider:** Ambient Light "Burst" logic (Snapshot on visibility).
- [x] **4.3.2 Thermal Provider:** System thermal throttling levels.

### 4.4 Narrative Workers (Grade B/C - Periodic)
- [x] **4.4.1 Vitality Provider:** Sensor-based Step counting (Establishing the Health Pipe).
- [x] **4.4.2 Social Provider:** Notification frequency summaries (Zen Pulse).

### 4.5 Phase 4 Finalization
- [x] **4.5.1 Phase 4 Finalization:** Documentation, Retrospective, and Tagging.

---

## Phase 5: Pulse Design System (UI Foundations)
*Goal: Build the atmospheric, glass-first UI library in `:ui`.*

### 5.1 Foundations
- [x] **5.1.1 Typography:** Define the `PulseTypography` system (High-legibility atmospheric type).
- [x] **5.1.2 Atmospheric Color System:** Reactive palette that shifts with Sun/Battery/Biome state.
- [x] **5.1.3 PulseTheme Wrapper:** Implement `PulseTheme` with dynamic token injection.

### 5.2 Pulse Components (Glassmorphism)
- [x] **5.2.0 PulseBackdrop:** Compose-native `RenderSurface` for in-app wallpaper previews.
- [x] **5.2.1 Glassmorphic Container:** Build `PulseCard` with custom blur/AGSL shaders.
- [x] **5.2.2 Atmosphere Controls:** Glass-themed toggles (`PulseToggle`), sliders (`PulseSlider`), and buttons (`PulseButton`).
- [x] **5.2.3 Data Visualizer:** Canvas-based `PulseGraph` for dashboard trends.

### 5.3 Phase 5 Finalization
- [x] **5.3.1 Component Gallery:** Build an internal screen to verify all Pulse components.
- [x] **5.3.2 Phase 5 Finalization:** Documentation, Retrospective, and Tagging.

---

## Phase 6: The Rule Engine (The Brain)
*Goal: Implement the zero-allocation OpCode VM and Perceptual Synthesis.*

### 6.1 Perceptual Foundations
- [x] **6.1.1 Zero-Allocation Synthesis:** Replace `forEach` iterators with index-based loops in the engine core.
- [x] **6.1.2 Weighted Contributions:** Allow renderers to "vote" on theme aspects with specific weights.
- [x] **6.1.3 OKLab Blending:** Implement perceptual color interpolation for vibrant theme extraction.

### 6.2 The Macro-OpCode VM
- [x] **6.2.1 OpCode ISA:** Define the instruction set for high-value macros (Oscillate, Remap, Step).
- [ ] **6.2.2 Rule Evaluator:** Build the high-performance loop and pre-allocated stack for rule execution.
- [ ] **6.2.3 Property Buffer:** Implement the "Flat Memory" bridge between the VM and Renderers.

### 6.3 Phase 6 Finalization
- [ ] **6.3.1 Biome Decoder:** Implement the initial JSON-to-Bytecode parser.
- [ ] **6.3.2 Demo: "The Data-Driven Prism":** Validate zero-allocation execution of a JSON-driven scene.
- [ ] **6.3.3 Phase 6 Finalization:** Documentation, Retrospective, and Tagging.

---

## Phase 7: The Visual Pipeline (The Body)
*Goal: Connect the Brain to high-performance assets.*

### 7.1 Asset Management
- [ ] **7.1.1 Bitmap Texture Atlas:** Implement a system to pack multiple assets into a single GPU texture.
- [ ] **7.1.2 Asset Registry:** Manage asset lifecycles and "Ready" state handshakes.

### 7.2 The Shader Bridge
- [ ] **7.2.1 AGSL Integration:** Enable custom shaders in the platform-agnostic `EngineCanvas`.
- [ ] **7.2.2 Uniform Mapping:** Automate the handover of Property Buffer values to GPU Uniforms.

### 7.3 Phase 7 Finalization
- [ ] **7.3.1 DslEffectRenderer:** Build the single, optimized renderer that interprets the full DSL.
- [ ] **7.3.2 Demo: "The Hazy Horizon":** Validate atlas batching and dynamic shader uniforms.
- [ ] **7.3.3 Phase 7 Finalization:** Documentation, Retrospective, and Tagging.

---

## Phase 8: The Island Biome (Art Implementation)
*Goal: The first production-ready biome (Zelda-inspired).*

- [ ] **8.1 Atmosphere:** Dynamic Skybox and multi-stop Gradient layers.
- [ ] **8.2 Landscape:** Procedural swaying grass and growth-aware trees.
- [ ] **8.3 Details:** Water reflections and night-time fireflies.
- [ ] **8.4 Phase 8 Finalization:** Documentation, Retrospective, and Tagging.

---

## Phase 9: Dashboard & UX
*Goal: User-facing features and onboarding.*

- [ ] **9.1 Onboarding Flow:** Multi-step atmospheric intro and permission gateway.
- [ ] **9.2 Status Dashboard:** The summary screen decoding visuals into data.
- [ ] **9.3 Phase 9 Finalization:** Documentation, Retrospective, and Tagging.

---

## Phase 10: Engineering Excellence (Finalization)
- [ ] **10.1 Custom Linting:** Detekt rule for zero-allocation enforcement.
- [ ] **10.2 Performance Audit:** Frame-time and battery profiling.
- [ ] **10.3 Phase 10 Finalization:** Documentation, Retrospective, and Tagging.
