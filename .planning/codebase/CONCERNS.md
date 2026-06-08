# Codebase Concerns

**Analysis Date:** 2026-06-08

## Technical Debt & Status

- **Greenfield Status:** Currently, the codebase is entirely empty aside from the specification (`docs/prompts/first-context.md`).
- **Immediate Goal:** Scaffold the basic Android Studio project, configure Gradle Kotlin DSL, setup Jetpack Compose, Hilt, Room, and the MVVM Clean Architecture folders.

## Fragile Areas

- **ML Kit Integration:** Camera permissions, lifecycle, and ML Kit models running efficiently without blocking the main thread will require careful testing and abstraction.
- **Build Configuration:** Moving straight to Kotlin DSL with modern Compose and Hilt requires strict matching of compiler extension versions.

---

*Concerns analysis: 2026-06-08*
