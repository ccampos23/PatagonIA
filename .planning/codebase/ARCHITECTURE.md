# Architecture

**Analysis Date:** 2026-06-08

## Pattern

**Clean Architecture + MVVM**
The app follows standard Clean Architecture principles with MVVM at the presentation layer.

## Layers and Data Flow

1. **Presentation Layer (MVVM):**
   - Jetpack Compose UI observes state from ViewModels.
   - ViewModels execute Use Cases from the Domain layer.

2. **Domain Layer:**
   - Pure Kotlin (no Android dependencies).
   - Contains business logic (Use Cases), Models, and Repository Interfaces.

3. **Data Layer:**
   - Implements Domain Repository Interfaces.
   - Manages Local Data (Room) and Remote Data (Networking/APIs).

## Boundaries and Abstractions

- **Dependency Rule:** Presentation -> Domain <- Data.
- **Dependency Injection:** Handled exclusively via Hilt.
- **Hardware Abstraction:** Camera and ML Kit features are abstracted behind services/facades in the data or core layer to decouple ML from business logic.

---

*Architecture analysis: 2026-06-08*
