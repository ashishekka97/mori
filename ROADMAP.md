# Mori: Engineering & Product Roadmap (High-Resolution v3)

Mori is a zero-allocation, privacy-first Live Wallpaper engine. This roadmap focuses on **Atomic PRs**, **Battery Efficiency**, and **Architectural Purity**.

---

## Phase 1: The Agnostic Platform (Foundation)
[... Phase 1-5 tasks marked as completed ...]

---

## Phase 6: The Rule Engine (The Brain)
*Goal: Implement the zero-allocation OpCode VM and Perceptual Synthesis.*

### 6.1 Perceptual Foundations
- [ ] **6.1.1 Zero-Allocation Synthesis:** Replace `forEach` iterators with index-based loops in the engine core.
- [ ] **6.1.2 Weighted Contributions:** Allow renderers to "vote" on theme aspects with specific weights.
- [ ] **6.1.3 OKLab Blending:** Implement perceptual color interpolation for vibrant theme extraction.

### 6.2 The Macro-OpCode VM
- [ ] **6.2.1 OpCode ISA:** Define the instruction set for high-value macros (Oscillate, Remap, Step).
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
