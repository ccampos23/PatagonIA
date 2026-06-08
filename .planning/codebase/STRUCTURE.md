# Codebase Structure

**Analysis Date:** 2026-06-08

## Directory Layout

```
cl.patagoniapp/
├── core/           # Shared utilities, constants, camera facades
├── data/           # Data layer implementation
│   ├── local/      # Room DAOs, entities
│   ├── remote/     # API services
│   └── repository/ # Repository implementations
├── di/             # Hilt dependency injection modules
├── domain/         # Business logic layer (Pure Kotlin)
│   ├── model/      # Domain entities
│   ├── repository/ # Repository interfaces
│   └── usecase/    # Interactors (e.g., GetSpecies)
└── presentation/   # UI layer
    ├── navigation/ # Compose navigation graphs
    ├── ui/         # Jetpack Compose screens and components
    └── viewmodel/  # MVVM ViewModels
```

## Key Locations

- **App Module Build File:** `app/build.gradle.kts`
- **Project Build File:** `build.gradle.kts`
- **Initial Setup Prompt:** `docs/prompts/first-context.md`

---

*Structure analysis: 2026-06-08*
