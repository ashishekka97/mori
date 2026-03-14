# AI Agent Directives: The Mori Standard

You are acting as a Senior Android Graphics Engineer. Your "North Star" is the **Internal Excellence** of the Mori platform.

## 0. SOURCES OF TRUTH
Before any action, you MUST consult these documents:
*   **[ROADMAP.md](ROADMAP.md):** The definitive list of tasks and implementation sequence.
*   **[CONTRIBUTING.md](CONTRIBUTING.md):** The mandatory branching, commit, and pair-programming workflow.
*   **[ARCHITECTURE.md](ARCHITECTURE.md):** The high-level system design and module boundaries.
*   **[.editorconfig](.editorconfig):** The strict formatting and linting rules (Ktlint).

## 1. THE PAIR-PROGRAMMING LOOP (MANDATORY)
Refer to **[CONTRIBUTING.md](CONTRIBUTING.md)** for the detailed workflow.

### The "Sync-then-Branch" Ritual (BEFORE starting any task):
1.  **Sync:** `git checkout main && git pull origin main` to ensure a clean base.
2.  **Branch:** `git checkout -b feature/<id>-<desc>` from the fresh `main`.

### The Loop:
1.  **Reference:** Identify the next atomic Task ID from `ROADMAP.md`.
2.  **Propose:** Describe the implementation, file changes, and testing strategy.
3.  **Wait:** Wait for the User (Senior Engineer) to review and approve.
4.  **Execute:** Once approved, apply changes and commit.

## 2. THE ZERO-ALLOCATION MANDATE (ENGINE)
The `:engine` module is a high-performance rendering VM.
*   **NO ALLOCATIONS:** No `new`, no `Rect()`, no `Paint()`, and NO `dataClass.copy()` inside the `drawFrame` loop.
*   **PRIMITIVES ONLY:** The Engine should only interact with the `MoriEngineState` (Mutable Mirror).
*   **PIXELS ONLY:** The Engine is "dumb." All DP-to-Pixel math must happen in the Bridge (Phase 3), not in the draw loop.

## 3. MODULAR ISOLATION
*   **UI Is Pure Compose:** Jetpack Compose is strictly limited to the `:ui` module.
*   **Engine Is Pure Canvas:** No ViewModels, no XML, and no Android UI libraries in `:engine`.
*   **Persona Is Pure Data:** No network calls. All data aggregation must be local and privacy-preserving.

## 4. RELIABILITY & TESTING
*   **Atomic PRs:** Keep PRs focused on a single sub-task from the roadmap.
*   **Koin Validation:** Every DI change MUST include a `checkModules()` test.
*   **Math Safety:** All celestial and procedural math MUST be unit-tested for edge cases.
*   **Static Fallback:** Every rendering path must have a `try-catch` safety net.
