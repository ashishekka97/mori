# Mori Contribution & Workflow Guide

To maintain "Internal Excellence," this project follows a strict branching and commit strategy.

## 1. Branching Strategy
We use **Task-Specific Feature Branches**. 
*   **Source:** Always branch from the latest `main`.
*   **Naming:** `feature/<task-id>-<brief-description>` 
    *   Example: `feature/1.1.1-worldstate-schema`
*   **Lifecycle:** One branch per Roadmap task. Once a task is verified, it is merged into `main` and the branch is deleted.

## 2. Commit Message Convention
We follow [Conventional Commits](https://www.conventionalcommits.org/).

*   `feat:` A new feature for the user.
*   `fix:` A bug fix.
*   `docs:` Documentation changes only.
*   `perf:` A code change that improves performance (Critical for the Engine!).
*   `refactor:` A code change that neither fixes a bug nor adds a feature.
*   `test:` Adding missing tests or correcting existing tests.

**Example:** `feat(persona): define flat worldstate schema`

## 3. The Development Loop (Agent & Engineer)
1.  **Pick a Task:** Select the next unchecked item from `ROADMAP.md`.
2.  **Propose:** The Agent describes the implementation plan and code structure.
3.  **Approve:** The User (Senior Engineer) reviews and approves the plan.
4.  **Execute:** 
    *   Create a feature branch.
    *   Apply code changes.
    *   Add/Update Unit Tests.
    *   Verify with `./gradlew ktlintCheck` and module tests.
5.  **Commit:** Commit the changes using the naming convention.
6.  **Merge:** After the user reviews the commit, merge the branch into `main`.

## 4. Engineering Standards
*   **Zero-Allocation:** No `new` or `.copy()` in the `:engine` module's rendering loop.
*   **Privacy First:** No network calls in `:persona`. All data must be local.
*   **Modular Isolation:** Respect Gradle module boundaries. No UI code in `:engine` or `:persona`.
