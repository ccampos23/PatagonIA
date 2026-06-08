# Technology Stack

**Analysis Date:** 2026-06-08

## Languages

**Primary:**
- Kotlin - All application code

## Runtime

**Environment:**
- Android SDK (minSdk 23+, target latest stable)

**Package Manager:**
- Gradle (Kotlin DSL `build.gradle.kts`)

## Frameworks

**Core:**
- Jetpack Compose (UI Toolkit)

**Testing:**
- JUnit4/5 - Unit tests
- Espresso / Compose UI Test - UI tests

**Build/Dev:**
- Android Studio / Android Build Tools

## Key Dependencies

**Critical:**
- Jetpack Compose Material 3 - Modern UI components
- Hilt (Dagger) - Dependency injection
- Room - Local persistence/database
- Kotlin Coroutines - Asynchronous programming

**Infrastructure:**
- Retrofit or Ktor Client - Networking
- ML Kit Vision - On-device AI (Image labeling, object detection)

## Configuration

**Build:**
- `build.gradle.kts` at project and module levels.
- Kotlin Compiler Extension enabled for Compose.

## Platform Requirements

**Development:**
- Windows/macOS/Linux with Android Studio
- Android Emulator or physical device

**Production:**
- Native Android app target.

---

*Stack analysis: 2026-06-08*
