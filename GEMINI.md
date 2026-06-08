<!-- GSD:project-start source:PROJECT.md -->

## Project

**PataGOnIA**

PataGOnIA is an interactive, educational Android app for nature enthusiasts (ages 16-50), biologists, and photographers to identify, record, and share Chilean species during their outdoor journeys. It combines an offline-first map and trekking guide with an ML-powered nature journal and gamified social features.

**Core Value:** The absolute core value is the **personal offline nature journal** (Map + ML Recognition + Personal captures) — it must provide a 100% reliable offline map and species capture experience even when deep in the wilderness without signal.

### Constraints

- **Connectivity:** The core capture and map features must function flawlessly offline.
- **Storage Management:** On-demand downloads for models and maps must handle failure gracefully and allow users to plan ahead.

<!-- GSD:project-end -->

<!-- GSD:stack-start source:codebase/STACK.md -->

## Technology Stack

## Languages

- Kotlin - All application code

## Runtime

- Android SDK (minSdk 23+, target latest stable)
- Gradle (Kotlin DSL `build.gradle.kts`)

## Frameworks

- Jetpack Compose (UI Toolkit)
- JUnit4/5 - Unit tests
- Espresso / Compose UI Test - UI tests
- Android Studio / Android Build Tools

## Key Dependencies

- Jetpack Compose Material 3 - Modern UI components
- Hilt (Dagger) - Dependency injection
- Room - Local persistence/database
- Kotlin Coroutines - Asynchronous programming
- Retrofit or Ktor Client - Networking
- ML Kit Vision - On-device AI (Image labeling, object detection)

## Configuration

- `build.gradle.kts` at project and module levels.
- Kotlin Compiler Extension enabled for Compose.

## Platform Requirements

- Windows/macOS/Linux with Android Studio
- Android Emulator or physical device
- Native Android app target.

<!-- GSD:stack-end -->

<!-- GSD:conventions-start source:CONVENTIONS.md -->

## Conventions

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

<!-- GSD:conventions-end -->

<!-- GSD:architecture-start source:ARCHITECTURE.md -->

## Architecture

## Pattern

## Layers and Data Flow

## Boundaries and Abstractions

- **Dependency Rule:** Presentation -> Domain <- Data.
- **Dependency Injection:** Handled exclusively via Hilt.
- **Hardware Abstraction:** Camera and ML Kit features are abstracted behind services/facades in the data or core layer to decouple ML from business logic.

<!-- GSD:architecture-end -->

<!-- GSD:skills-start source:skills/ -->

## Project Skills

| Skill | Description | Path |
|-------|-------------|------|
| android-clean-architecture | Clean Architecture patterns for Android and Kotlin Multiplatform projects — module structure, dependency rules, UseCases, Repositories, and data layer patterns. | `.agents/skills/android-clean-architecture/SKILL.md` |
| "android-di-hilt" | "Wire Android dependency injection with Hilt, scopes, testing overrides, and module ownership boundaries." | `.agents/skills/android-di-hilt/SKILL.md` |
| imagegen-frontend-mobile | Elite mobile app image-generation skill for creating premium, app-native screen concepts and flows. Designed for iOS, Android, and cross-platform mobile products. Prioritizes clean hierarchy, comfortably readable text, strong multi-screen consistency, controlled color palettes, non-generic creative direction, textured surfaces, image-led composition, tasteful custom iconography, and clean phone mockup framing. By default, screens should be shown inside a subtle premium iPhone or similar phone mockup with a visible frame, while the main focus stays on the app content itself. This skill generates images only. It does not write code. | `.agents/skills/imagegen-frontend-mobile/SKILL.md` |
| kotlin-springboot | 'Get best practices for developing applications with Spring Boot and Kotlin.' | `.agents/skills/kotlin-springboot/SKILL.md` |
| mobile-android-design | Master Material Design 3 and Jetpack Compose patterns for building native Android apps. Use when designing Android interfaces, implementing Compose UI, or following Google's Material Design guidelines. | `.agents/skills/mobile-android-design/SKILL.md` |
| supabase | "Use when doing ANY task involving Supabase. Triggers: Supabase products (Database, Auth, Edge Functions, Realtime, Storage, Vectors, Cron, Queues); client libraries and SSR integrations (supabase-js, @supabase/ssr) in Next.js, React, SvelteKit, Astro, Remix; auth issues (login, logout, sessions, JWT, cookies, getSession, getUser, getClaims, RLS); Supabase CLI or MCP server; schema changes, migrations, security audits, Postgres extensions (pg_graphql, pg_cron, pg_vector)." | `.agents/skills/supabase/SKILL.md` |
<!-- GSD:skills-end -->

<!-- GSD:workflow-start source:GSD defaults -->

## GSD Workflow Enforcement

Before using Edit, Write, or other file-changing tools, start work through a GSD command so planning artifacts and execution context stay in sync.

Use these entry points:

- `/gsd-quick` for small fixes, doc updates, and ad-hoc tasks
- `/gsd-debug` for investigation and bug fixing
- `/gsd-execute-phase` for planned phase work

Do not make direct repo edits outside a GSD workflow unless the user explicitly asks to bypass it.
<!-- GSD:workflow-end -->

<!-- GSD:profile-start -->

## Developer Profile

> Profile not yet configured. Run `/gsd-profile-user` to generate your developer profile.
> This section is managed by `generate-claude-profile` -- do not edit manually.
<!-- GSD:profile-end -->
