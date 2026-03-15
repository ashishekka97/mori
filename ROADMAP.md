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
- [ ] **1.3.1 Service Lifecycle:** Connect `WallpaperService` visibility to the `LifecycleManager`.
- [ ] **1.3.2 Persona DI:** Setup the Koin module for Phase 1 components.
- [ ] **1.3.3 Module Validation:** Add a `KoinTest` to ensure the Phase 1 graph is sound.

### 1.4 Phase 1 Finalization
- [ ] **1.4.1 Phase 1 Finalization:** Documentation, Retrospective, and Tagging.

---

## Phase 2: The Pulse Engine (The Heart)
*Goal: Implement the zero-allocation rendering loop early for visual verification.*

### 2.1 Heartbeat
- [ ] **2.1.1 Choreographer Loop:** Implement the basic `FrameCallback` loop in `:engine`.
- [ ] **2.1.2 FPS Controller:** Add logic for 30/60 FPS toggling and frame skipping.
- [ ] **2.1.3 Static Fallback:** Build the `StaticFallbackRenderer` safety net (renders a solid brand color).

### 2.2 Rendering Architecture
- [ ] **2.2.1 Effect Interface:** Define the `EffectRenderer` contract.
- [ ] **2.2.2 Layer Manager:** Implement the Z-Order system for rendering layers.
- [ ] **2.2.3 Engine DI:** Setup the Koin module for the rendering core.

### 2.3 Phase 2 Finalization
- [ ] **2.3.1 Phase 2 Finalization:** Documentation, Retrospective, and Tagging.

---

## Phase 3: The Engine Bridge (The Handover)
*Goal: Pass data and geometry from Persona to Engine without allocations.*

### 3.1 Memory Mirroring
- [ ] **3.1.1 MoriEngineState:** Create the mutable, pre-allocated mirror of `WorldState`.
- [ ] **3.1.2 State Synchronizer:** Implement the background `Flow` collector for state handover.

### 3.2 Geometry & Scaling (Smart Bridge)
- [ ] **3.2.1 Surface Metrics:** Track width/height and pre-calculate DP-to-Pixel offsets.
- [ ] **3.2.2 Handover Logic:** Ensure the Engine only receives raw Pixel coordinates, keeping it "dumb" and fast.

### 3.3 Phase 3 Finalization
- [ ] **3.3.1 Phase 3 Finalization:** Documentation, Retrospective, and Tagging.

---

## Phase 4: Persona (Data Collectors)
*Goal: Atomic collectors for device metrics with battery-aware ticking.*

### 4.1 Energy & Environment (Real-time)
- [ ] **4.1.1 Battery Provider:** Implement the `BroadcastReceiver` for level/charging status.
- [ ] **4.1.2 Thermal Provider:** Implement the `PowerManager` thermal status listener.
- [ ] **4.1.3 Atmos Provider:** Implement Light/Proximity sensor fusion.

### 4.2 Chronos & Celestial (Low-Frequency)
- [ ] **4.2.1 Time Ticker:** Implement the 1-minute system clock provider.
- [ ] **4.2.2 Solar Math:** Implement low-frequency sun position updates (1-5 min).
- [ ] **4.2.3 Lunar Math:** Implement low-frequency moon phase updates.
- [ ] **4.2.4 Season Progress:** Implement day-of-year seasonal calculator.

### 4.3 Zen & Vitality (Optional/Special Permissions)
- [ ] **4.3.1 DND Provider:** Implement the Do Not Disturb status listener.
- [ ] **4.3.2 Usage Provider (Optional):** Implement `UsageStatsManager` with graceful fallback if permission is missing.
- [ ] **4.3.3 Health Skeleton:** Create the `HealthConnect` bridge.

### 4.4 Phase 4 Finalization
- [ ] **4.4.1 Phase 4 Finalization:** Documentation, Retrospective, and Tagging.

---

## Phase 5: Pulse Design System (UI Foundations)
*Goal: Build the atmospheric UI library in `:ui`.*

### 5.1 Foundations
- [ ] **5.1.1 Typography:** Define the `PulseTypography` system.
- [ ] **5.1.2 Color Palette:** Define the atmospheric palette and dynamic gradients.
- [ ] **5.1.3 Theme Wrapper:** Implement `MoriTheme` for token injection.

### 5.2 Atomic Components
- [ ] **5.2.1 Glassmorphic Container:** Build `MoriCard` with custom shaders.
- [ ] **5.2.2 Pulse Controls:** Build atmospheric switches, sliders, and buttons.
- [ ] **5.2.3 Metric Visualizer:** A Canvas-based graph component for the dashboard.

### 5.3 Phase 5 Finalization
- [ ] **5.3.1 Phase 5 Finalization:** Documentation, Retrospective, and Tagging.

---

## Phase 6: Biome DSL & Rule Engine (The Orchestrator)
*Goal: Decouple visuals from code via a declarative DSL.*

### 6.1 Asset Pipeline
- [ ] **6.1.1 Texture Atlas:** Implement `BitmapTextureAtlas` for GPU efficiency.
- [ ] **6.1.2 Asset Registry:** Implement pre-decoding and "Ready" handshake logic.

### 6.2 DSL & Rule Engine
- [ ] **6.2.1 Trigger & Mapper:** Define how JSON maps and scales `WorldState` values.
- [ ] **6.2.2 Rule Evaluator:** Build the logic engine that calculates per-frame effect properties.
- [ ] **6.2.3 Parser:** Implement the JSON-to-Domain-Model deserializer.

### 6.3 Phase 6 Finalization
- [ ] **6.3.1 Phase 6 Finalization:** Documentation, Retrospective, and Tagging.

---

## Phase 7: The Island Biome (Art Implementation)
*Goal: The first production-ready biome.*

- [ ] **7.1 Atmosphere:** Dynamic Skybox and Celestial layers.
- [ ] **7.2 Landscape:** Procedural swaying grass and growth-aware trees.
- [ ] **7.3 Details:** Water reflections and night-time fireflies.
- [ ] **7.4 Phase 7 Finalization:** Documentation, Retrospective, and Tagging.

---

## Phase 8: Dashboard & UX
*Goal: User-facing features and onboarding.*

- [ ] **8.1 Onboarding Flow:** Multi-step atmospheric intro and permission gateway.
- [ ] **8.2 Status Dashboard:** The summary screen decoding visuals into data.
- [ ] **8.3 Preview & Simulation:** Compose surface for Engine preview and state scrubbing.
- [ ] **8.4 Phase 8 Finalization:** Documentation, Retrospective, and Tagging.

---

## Phase 9: Engineering Excellence (Finalization)
- [ ] **9.1 Custom Linting:** Detekt rule for zero-allocation enforcement.
- [ ] **9.2 Snapshot Testing:** Visual regression suite.
- [ ] **9.3 Performance Audit:** Frame-time and battery profiling.
- [ ] **9.4 Phase 9 Finalization:** Documentation, Retrospective, and Tagging.
