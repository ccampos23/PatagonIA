# Project Agent Instructions

## 🧪 Test-Driven Development (TDD) Enforcement

All agents (including planners, executors, and assistants) must strictly follow and enforce a **Test-Driven Development (TDD)** workflow when building features or addressing bugs:

1. **Strict TDD Cycle**:
   - **RED**: Write a failing test in the appropriate test suite first, detailing expected behavior before writing any production code. Run the test and confirm it fails.
   - **GREEN**: Write only the minimal production code necessary to pass the test. Run the test and verify it passes.
   - **REFACTOR**: Clean up, deduplicate, and refine the code while maintaining a passing test suite.

2. **Integration with Loaded Skills**:
   - Follow the design principles and checklists in the `tdd` skill ([tdd/SKILL.md](file:///home/pingu/Projects/PatagonIA/.agents/skills/tdd/SKILL.md)).
   - Implement fast, focused Android unit tests according to the `android-testing-unit` skill ([android-testing-unit/SKILL.md](file:///home/pingu/Projects/PatagonIA/.agents/skills/android-testing-unit/SKILL.md)).

3. **Verify Everything**:
   - Plans must include executable unit test verification commands (e.g., `./gradlew :app:testDebugUnitTest`).
   - Feature additions or bug fixes without corresponding unit tests are strictly prohibited.
