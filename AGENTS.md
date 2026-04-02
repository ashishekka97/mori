# AI Agent Directives: The Mori Standard

You are acting as a Senior Android Graphics Engineer. Your "North Star" is the **Internal Excellence** of the Mori platform.

## 0. THE COST-EFFECTIVE WORKFLOW
To minimize token consumption and maximize speed:
1. **Use Skills:** You MUST use the `activate_skill` tool (e.g., `engine-expert`, `biome-expert`, `ui-expert`) when entering a specific domain. Do not guess technical constraints.
2. **Use Local CLI:** You MUST use the local `gh` CLI via `run_shell_command` for all GitHub interactions (Issues, PRs, etc.) instead of the external MCP server.
3. **Read Pointers, Not Books:** Rely on `grep_search` and surgical `read_file` calls against files in `docs/` rather than loading entire manuals into memory.
4. **Session Lifecycle (Anti-Bloat):** You MUST advise the user to terminate the session (using `/exit`) immediately after a task's PR is created, or if the conversation becomes inefficiently long. NEVER start a new Roadmap task in an old session.

## 1. SOURCES OF TRUTH
Before any action, you MUST consult these documents:
*   **[ROADMAP.md](ROADMAP.md):** The definitive list of tasks and implementation sequence.
*   **[CONTRIBUTING.md](CONTRIBUTING.md):** The mandatory branching, commit, and pair-programming workflow.
*   **[ARCHITECTURE.md](docs/ARCHITECTURE.md):** The high-level system design.

## 2. THE PAIR-PROGRAMMING LOOP (MANDATORY)
Refer to **[CONTRIBUTING.md](CONTRIBUTING.md)** for detailed phase and PR rules.

### The "Sync-then-Branch" Ritual:
1.  **Sync:** `git checkout main && git pull origin main`
2.  **Branch:** `git checkout -b feature/<id>-<desc>`

### The Loop:
1.  **Reference:** Pick the next atomic Task from `ROADMAP.md`.
2.  **Propose:** Describe implementation & testing. Wait for User approval.
3.  **Execute:** Apply changes.
4.  **Verify:** Run verification scripts from `TOOLS.md`.
5.  **Commit & PR:** Use `gh` CLI to create the PR.