# Coding Conventions

**Analysis Date:** 2026-06-08

## General Guidelines

- **Language:** Kotlin idioms, taking advantage of standard functions and null-safety.
- **Asynchrony:** Use Coroutines and Flows exclusively for reactive programming and async work.
- **Build System:** Use Kotlin DSL (`build.gradle.kts`) instead of Groovy.

## Architecture Enforcement

- No Android dependencies in the `domain` module/package.
- Repositories in the `data` layer must implement interfaces defined in the `domain` layer.
- ViewModels should not have direct references to Compose UI elements.

## Jetpack Compose

- **State Hoisting:** Keep composables stateless where possible.
- **Previews:** Use `@Preview` annotations for reusable components.

---

*Conventions analysis: 2026-06-08*
