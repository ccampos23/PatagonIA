# Application Architecture

## Overview
PataGOnIA follows a strict **Offline-First Clean Architecture** based on the Android Clean Architecture standard (Domain, Data, Presentation layers). Because the core value depends on a 100% reliable wilderness experience without connectivity, the device's local database and filesystem are treated as the **Single Source of Truth**. All remote interactions (Supabase, downloading maps/models) happen asynchronously in the background.

## Component Boundaries

### 1. Presentation Layer (Jetpack Compose & ViewModels)
- **Responsibility:** UI rendering, handling user input, and managing screen state.
- **Rules:** ViewModels are completely agnostic to network connectivity. They only observe reactive streams (`Flow`) from the Domain layer. The UI never makes a direct network call to Supabase for core app data.
- **Components:** `MapScreen`, `CameraScreen`, `JournalScreen`, `ProfileScreen`.

### 2. Domain Layer (Pure Kotlin UseCases & Models)
- **Responsibility:** Business logic and coordination. Contains zero Android UI or data framework dependencies.
- **Models:** `Species`, `Capture`, `MapRegion`.
- **UseCases:** Encapsulate single actions.
  - *Core:* `SaveCaptureUseCase`, `GetCapturesUseCase`
  - *ML:* `IdentifySpeciesUseCase`, `EnsureModelDownloadedUseCase`
  - *Map:* `GetMapTilesUseCase`

### 3. Data Layer (Repositories & Data Sources)
- **Responsibility:** Data fetching, caching, and mediating between local and remote sources.
- **Repositories:** `CaptureRepository`, `MapRepository`, `MLModelRepository`.
- **Local Data Sources (The Source of Truth):**
  - **Room Database:** Stores relational data like User Profiles, Capture metadata, and Species information.
  - **Local FileSystem (Scoped Storage):** Stores heavy binaries like captured images, downloaded `.tflite` ML models, and MBTiles/vector caches for the map.
- **Remote Data Sources:** Supabase SDK (PostgreSQL for backend data sync, Storage for cloud images).

### 4. Hardware & Background Service Layer
- **Machine Learning Service:** Wraps CameraX and Google ML Kit. Instantiated as singletons (via Hilt) to avoid memory leaks. Loads custom `.tflite` models directly from the local FileSystem for on-device inference without network requests.
- **Location Service:** Wraps `FusedLocationProvider` to supply GPS coordinates to the Map and Camera.
- **Background Sync Service:** Android `WorkManager` manages background queues to sync Room to Supabase and handle asset downloads.

## Data Flow

### 1. The Offline Capture & Inference Flow (Camera to Journal)
1. **Input:** The user points the camera at a species. CameraX feeds image frames to the `MLKitVisionService` (using `STRATEGY_KEEP_ONLY_LATEST` to avoid backpressure).
2. **Local Inference:** ML Kit runs inference using a downloaded `.tflite` model, identifying the species and returning it to the UI for preview.
3. **Save Action:** User taps "Capture". The UI calls `SaveCaptureUseCase`.
4. **Local Persistence:** The Repository writes the image to the local FileSystem and inserts a `CaptureEntity` into the Room database (marked as `sync_status = PENDING`).
5. **Optimistic UI:** The Room database instantly emits a new `Flow<List<Capture>>`. The ViewModel receives it, updating the "Sticker Book" instantly—no loading spinners required.
6. **Deferred Sync:** A `WorkManager` job is enqueued. If the user is offline, it waits. Once back in civilization (network connected), the worker uploads the image to Supabase Storage and syncs the row to the remote database, updating the local row to `sync_status = SYNCED`.

### 2. On-Demand Resources Flow (Map & ML Models)
To keep the initial app download size small, heavy assets are downloaded on-demand.
1. **User Action:** The user selects a region (e.g., "Torres del Paine") to download for offline use.
2. **Download Task:** `WorkManager` initiates the download of the specific ML Model (`.tflite`) and Map data (Vector tiles/MBTiles) from Supabase Storage.
3. **Validation & Cache:** Downloads perform checksum validation upon completion before moving from a temporary cache to persistent storage.
4. **Usage:** When the user enters the region offline, the `MapScreen` reads tiles exclusively from the local cache via MapLibre, and the Camera loads the local `.tflite` model.

## Suggested Build Order

To avoid dependency deadlocks, the architecture should be built from the inside-out:

1. **Phase 1: Local Foundation & Core Domain**
   - **What:** Setup Room DB, internal FileSystem management, Domain Models, and UseCases.
   - **Why:** Establishes the single source of truth before any UI or remote code expects it.

2. **Phase 2: ML Kit & Camera Pipeline**
   - **What:** Implement CameraX, integrate ML Kit, and build the infrastructure to load custom `.tflite` models. Setup singleton detectors and frame lifecycle management.
   - **Why:** The core "wow" factor of the app. Needs to be validated early, and depends heavily on the local filesystem.

3. **Phase 3: Offline Map Engine**
   - **What:** Integrate MapLibre/osmdroid, test rendering of local MBTiles, and build the map UI.
   - **Why:** The second critical offline feature. Can be built independently but depends on Phase 1's local caching setup.

4. **Phase 4: Remote Sync & User Accounts (Supabase)**
   - **What:** Integrate Supabase Auth, PostgreSQL sync via WorkManager, and Storage for images/models.
   - **Why:** Adds the cloud layer on top of the already-functioning local app.

5. **Phase 5: Presentation & Gamification Polish**
   - **What:** Build the final Compose UI, Sticker Book, Leaderboards, and Experience logic.
   - **Why:** The UI simply consumes the data pipelines built in Phases 1-4.
