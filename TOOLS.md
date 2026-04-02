# Mori Build Tools
- Full Verification (Lint, Tests, Zero-Alloc): `./scripts/verify_all.sh`
- Engine Zero-Alloc Check: `./scripts/verify_engine.sh`
- Build: `./gradlew assembleDebug`
- Unit Tests: `./gradlew test`
- Layout Inspector: `adb shell am broadcast -a com.mori.DEBUG_LAYOUT`
- Update State: `adb shell am broadcast -a com.mori.UPDATE_STATE`

# GitHub CLI (Cost-Effective Alternative to MCP)
- List PRs: `gh pr list`
- Create PR: `gh pr create --title "..." --body "..."`
- List Issues: `gh issue list`
- View Issue: `gh issue view <number>`
- Add Comment: `gh issue comment <number> --body "..."`
