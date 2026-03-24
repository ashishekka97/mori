# Mori (森)

[![Android CI](https://github.com/ashishekka/mori/actions/workflows/android.yml/badge.svg)](https://github.com/ashishekka/mori/actions)

Mori turns the device's home screen into a "living dashboard." Instead of static images or generic loops, it uses passive OS-level data (time, battery, health metrics) to dynamically render a time-shifting, handcrafted biome via a **declarative DSL** and a custom **[Rule Engine (SPEC_ISA.md)](SPEC_ISA.md)**.

## Core Philosophy
1. **Zero-Allocation Core:** The rendering loop (`:engine`) is built on zero-allocation principles to ensure 60FPS without GC jank.
2. **Absolute Privacy:** All data (steps, usage, health) is gathered and processed 100% on-device. No telemetry.
3. **Data as Art:** We map digital noise to atmospheric visual cues (e.g., murky water for high screen time).
4. **Pulse Design System:** An atmospheric, context-aware UI system that bridges the gap between the wallpaper and the app.

---

## Tech Stack
* **Language:** Kotlin
* **Rendering:** Native Android `Canvas` (Hardware Accelerated)
* **Intelligence:** Custom **[OpCode ISA](SPEC_ISA.md)** for data-driven visuals.
* **Concurrency:** Kotlin Coroutines & `StateFlow`
* **Dependency Injection:** Koin
* **UI:** Jetpack Compose (The Pulse Design System)

---

## The 6-Module Architecture
Mori uses a strict multi-module architecture to enforce separation of concerns and optimize build speeds:

*   **`:app`** - **The Entry Point.** Manages the `WallpaperService` and hardware bindings.
*   **`:persona`** - **The Brain.** Normalizes raw OS data into a flat, primitive `WorldState`.
*   **`:bridge`** - **The Translator.** Handles zero-allocation data handover and DP-to-Pixel scaling.
*   **`:engine`** - **The Muscle.** A platform-agnostic rendering VM. Zero-allocation Canvas loop.
*   **`:biome`** - **The Logic.** Interprets declarative configurations and compiles them to bytecode.
*   **`:ui`** - **The Face.** Built with the **Pulse Design System**. Handles dashboard and UX.

For a deeper dive, see the **[Architecture Guide](ARCHITECTURE.md)** and the **[ISA Specification](SPEC_ISA.md)**.

---

## Development & Roadmap
To maintain "Internal Excellence," this project follows a strict branching and commit strategy. 

*   **[Engineering Roadmap](ROADMAP.md):** The high-resolution plan for all tasks.
*   **[Contribution & Workflow](CONTRIBUTING.md):** The "Pair Programming" loop and Git strategy.
*   **[AI Agent Directives](AGENTS.md):** The architectural boundaries for the AI pair-programmer.

---

## License
Copyright © 2026 Ashish Ekka. All rights reserved.
