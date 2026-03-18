# Mori Contribution & Workflow Guide

To maintain "Internal Excellence," this project follows a strict branching, commit, and GitHub-centric strategy.

## 1. The "Sync-then-Branch" Ritual
Before starting any task, ensure your local environment is perfectly aligned with the remote source of truth:
1.  **Sync:** `git checkout main && git pull origin main`
2.  **Branch:** `git checkout -b feature/<task-id>-<brief-description>` (e.g., `feature/1.1.2-statemanager-interface`)

## 2. Issue-First Development (The Technical Spec)
No work begins without a GitHub Issue.
*   **The Blueprint:** Pick a task from `ROADMAP.md`.
*   **The Spec:** Create a GitHub Issue describing the **Technical Requirements**. This issue serves as the "How" for the Roadmap's "What."
*   **The Approval:** For major tasks, wait for the Senior Engineer to approve the Issue's technical spec before coding.

## 3. Commit & Pull Request Standards
We follow [Conventional Commits](https://www.conventionalcommits.org/).

*   `feat:`, `fix:`, `docs:`, `perf:`, `refactor:`, `test:`
*   **The PR Requirement:** Every Pull Request must:
    1.  Include `Closes #<Issue-Number>` in the description.
    2.  Include a change to `ROADMAP.md` ticking the implemented task (e.g., `[x] 1.1.2`).
    3.  Pass all linting (`./gradlew ktlintCheck`) and module tests.

## 4. The Phase Lifecycle
To ensure strategic alignment and "Internal Excellence," every Phase follows a strict start-and-end ritual.

### The Phase Start Ritual (Strategic Alignment)
Before touching a single line of code in a new Phase:
1.  **Re-evaluate & Re-index:** Audit the `ROADMAP.md` for the upcoming Phase and all future phases. Perform **Strategic Course Correction** by adding, removing, or re-ordering tasks to align with the current project state.
2.  **Milestone Creation:** Create a new GitHub Milestone for the Phase (e.g., "Phase 2: The Pulse Engine").
3.  **High-Level Planning:** Discuss the Phase's core goals, technical hurdles, and performance/battery strategy.
4.  **Backlog Generation:** Create **all** GitHub Issues for the Phase tasks at once, ensuring each contains a detailed technical spec derived from the planning discussion.

### The Task Pre-Flight Ritual (Realignment)
Because issues are created at the start of a phase, they can become short-sighted as the implementation evolves. Before starting implementation of any task:
1. **Realignment Review:** Review the issue's technical spec against the `ARCHITECTURE.md` and current codebase.
2. **Pre-Flight Update:** If the architectural approach has shifted or technical debt is expanding beyond budget, rewrite the issue spec *before* coding.
3. **Tactical Task Creation:** If a realignment requires significant refactoring or a shift in strategy, explicitly create a new "Tactical Realignment" task in `ROADMAP.md` right before the current task to handle the architectural correction.

### The Tactical Course Correction Protocol
If a critical gap or "unknown unknown" is discovered **during execution**:
*   **Flag & Audit:** Immediately stop and report the discovery.
*   **Roadmap Update:** Explicitly update `ROADMAP.md` with the new task.
*   **Milestone Sync:** Add the corresponding new Issue to the active Milestone.
*   **Immutable History:** Completed phases remain immutable; only current and future roadmap items are eligible for re-indexing.

### The Phase Completion Ritual (Living History)
To ensure long-term "Context Forensics":
*   **GitHub Milestones:** All issues for a Phase must be grouped into the corresponding Milestone.
*   **The Phase Finalization Task:** Every Phase ends with a mandatory "Finalization" task in `ROADMAP.md`.
    *   **Branch:** `feature/phase-<number>-finalization`
    *   **PR Title:** `feat(meta): Phase <Number> Finalization - <Phase Name>`
*   **The Retrospective:** The final PR of a Phase must update `ARCHITECTURE.md` with a **"Phase Retrospective"** (Key decisions, architectural shifts, and "State of the Machine").
*   **Tagging:** Upon merging the final PR of a Phase, the `main` branch must be tagged (e.g., `v0.1.0-phase1`).

## 5. Engineering Standards
*   **Zero-Allocation:** No `new` or `.copy()` in the `:engine` module's rendering loop.
*   **Privacy First:** No network calls in `:persona`. All data must be local.
*   **Modular Isolation:** Respect Gradle module boundaries. No UI code in `:engine` or `:persona`.
