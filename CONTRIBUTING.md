# Mori Contribution & Workflow Guide

To maintain "Internal Excellence," this project follows a strict branching, commit, and GitHub-centric strategy.

## 1. The "Sync-then-Branch" Ritual
Before starting any task, ensure your local environment is perfectly aligned with the remote source of truth:
1.  **Sync:** `git checkout main && git pull origin main`
2.  **Branch:** `git checkout -b feature/<task-id>-<brief-description>` (e.g., `feature/1.1.2-statemanager-interface`)

## 2. Issue-First Development (The Technical Spec)
No work begins without a GitHub Issue.
*   **The Blueprint:** Pick a task from `ROADMAP.md`.
*   **The Spec:** Create a GitHub Issue describing the **Technical Requirements** (e.g., "Must be zero-allocation," "Primitive types only"). This issue serves as the "How" for the Roadmap's "What."
*   **The Approval:** For major tasks, wait for the Senior Engineer to approve the Issue's technical spec before coding.

## 3. Commit & Pull Request Standards
We follow [Conventional Commits](https://www.conventionalcommits.org/).

*   `feat:`, `fix:`, `docs:`, `perf:`, `refactor:`, `test:`
*   **The PR Requirement:** Every Pull Request must:
    1.  Include `Closes #<Issue-Number>` in the description.
    2.  Include a change to `ROADMAP.md` ticking the implemented task (e.g., `[x] 1.1.2`).
    3.  Pass all linting (`./gradlew ktlintCheck`) and module tests.

## 4. Phase Completion & "Living History"
Mori is built in Phases (Milestones). To ensure long-term "Context Forensics":
*   **GitHub Milestones:** All issues for a Phase must be grouped into a corresponding GitHub Milestone.
*   **The Phase Finalization Task:** Every Phase ends with a mandatory "Finalization" task in `ROADMAP.md`.
    *   **Branch:** `feature/phase-<number>-finalization`
    *   **PR Title:** `feat(meta): Phase <Number> Finalization - <Phase Name>`
*   **The Retrospective:** The final PR of a Phase must update `ARCHITECTURE.md` with a **"Phase Retrospective"** (Key decisions, architectural shifts, and "State of the Machine").
*   **Tagging:** Upon merging the final PR of a Phase, the `main` branch must be tagged (e.g., `v0.1.0-phase1`).

## 5. Engineering Standards
*   **Zero-Allocation:** No `new` or `.copy()` in the `:engine` module's rendering loop.
*   **Privacy First:** No network calls in `:persona`. All data must be local.
*   **Modular Isolation:** Respect Gradle module boundaries. No UI code in `:engine` or `:persona`.
