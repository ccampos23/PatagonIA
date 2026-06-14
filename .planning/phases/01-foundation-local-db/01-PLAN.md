# Phase 1: Foundation & Local DB - Execution Plan

**Goal:** Establish the single source of truth for the offline-first app.
**Context:** UUIDs (Strings) will be used for offline ID generation (D-02). A Single Module architecture organized by layer/feature will be used (D-01).

## Plan 01-01: Scaffold Android project and Clean Architecture layers with Hilt

1. **Step 1: Set up root and app build files**
   - Create `settings.gradle.kts` in the project root.
   - Create root `build.gradle.kts` and `gradle.properties`.
   - Create `app/build.gradle.kts` configuring Jetpack Compose, Kotlin, Coroutines, Room, and Hilt. Use `kotlin-dsl` style.

2. **Step 2: Initialize basic Android application structure**
   - Create `app/src/main/AndroidManifest.xml`.
   - Create the base application class `PatagoniaApplication.kt` annotated with `@HiltAndroidApp` and register it in the manifest.

3. **Step 3: Define Clean Architecture package structure**
   - In `app/src/main/java/com/patagonia/app/`, create the following packages:
     - `domain/` (Models, Repositories interfaces, UseCases)
     - `data/` (Local DB, Remote Sync, Repository Implementations, DI modules)
     - `presentation/` (Compose UI, ViewModels)

## Plan 01-02: Implement Room database, entities, and DAOs for captures

1. **Step 1: Create Data Entities**
   - Create `CaptureEntity.kt` in `data/local/entity/`. Include fields: `id` (String UUID, Primary Key), `speciesName` (String), `timestamp` (Long), `imagePath` (String), `latitude` (Double), `longitude` (Double), `isSynced` (Boolean).

2. **Step 2: Create Data Access Objects (DAOs)**
   - Create `CaptureDao.kt` in `data/local/dao/`. Add suspend functions for `insertCapture`, `updateCapture`, and a `Flow` returning function for `getAllCaptures()`.

3. **Step 3: Create Room Database Definition**
   - Create `PatagoniaDatabase.kt` in `data/local/` extending `RoomDatabase`. Register `CaptureEntity`.

4. **Step 4: Hilt Database Module**
   - Create `DatabaseModule.kt` in `data/di/`. Provide `@Singleton` instances of `PatagoniaDatabase` and `CaptureDao`.

## Plan 01-03: Create domain UseCases for managing the capture journal

1. **Step 1: Create Domain Models and Repository Interfaces**
   - Create `Capture.kt` in `domain/model/`.
   - Create `CaptureRepository.kt` interface in `domain/repository/` with functions matching the DAO but returning Domain models.

2. **Step 2: Implement Repositories**
   - Create `CaptureRepositoryImpl.kt` in `data/repository/`.
   - Inject `CaptureDao`.
   - Implement mapping between `CaptureEntity` and `Capture` domain model.

3. **Step 3: Hilt Repository Module**
   - Create `RepositoryModule.kt` in `data/di/`. Bind `CaptureRepositoryImpl` to `CaptureRepository`.

4. **Step 4: Create UseCases**
   - Create `GetCapturesUseCase.kt` and `AddCaptureUseCase.kt` in `domain/usecase/`. Inject `CaptureRepository` into them to encapsulate business logic.

## Verification
- Validate that the project builds (`./gradlew assembleDebug`).
- Ensure no dependency injection cycles or missing Hilt bindings.
- Validate that the domain module has no dependencies on the Android SDK (`android.*`).
