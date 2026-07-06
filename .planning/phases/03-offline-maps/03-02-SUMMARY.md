# Plan 03-02 Summary: Offline Map Downloader Service and Asset Manager UI

## What Was Built
Implemented the `TileDownloadService` foreground downloading service and the `AssetManagerScreen` Compose UI. This allows users to pre-download regional mapping data for offline use, monitors active download progress with pause/resume/cancel controls, and provides warnings/blocks based on size and tile limit estimations.

## Tasks Completed
- **Task 1: Offline Map Downloader Service**
  - Created `TileDownloadService` as a Foreground Service using type `dataSync` (Android 14+ requirement, D-16).
  - Implemented the `TileDownloadState` state machine (`Idle`, `Downloading`, `Paused`, `Completed`, `Failed`, `TimedOut`).
  - Added support for start, pause, resume, and cancel intent actions.
  - Implemented exponential retry backoff using `RetryUtil` (D-17).
  - Configured custom notification channels with throttled progress updates (capped at 1 per second) to prevent UI thread lockup.
  - Handled Android 15 `onTimeout` to save download progress checkpoint (D-17).
  - Added service declaration and required permissions to `AndroidManifest.xml`.
  - Added unit tests for the service, retry utility, presets, states, and downloaded regions (32 unit tests passing).

- **Task 2: Asset Manager Screen (TDD)**
  - Created `AssetManagerUiState` as an immutable data class for unidirectional data flow.
  - Implemented `AssetManagerViewModel` to coordinate downloading, state tracking, and deletion.
  - Built `AssetManagerScreen` with Jetpack Compose featuring:
    - Current active download progress card with pause, resume, and cancel buttons.
    - Custom bounding box download form with validation and live size estimation.
    - Curated list of Chilean national park presets (Torres del Paine, Conguillío, etc.) with estimated download sizes (D-15).
    - Saved maps list displaying downloaded regions, sizes in MB, and confirmation-based deletion.
  - Added size warning (>100MB, D-18) and a hard block (>500MB, equivalent to 750k tiles, D-18) to prevent storage exhaustion.
  - Created comprehensive JVM unit tests for `AssetManagerViewModel` verifying all states, inputs, and validation logic (10 unit tests passing).

## Files Created/Modified
- `app/src/main/AndroidManifest.xml` — Declared service and foreground data sync permissions.
- `app/src/main/java/com/patagonia/app/domain/model/TileDownloadState.kt` — State enum and progress state representation.
- `app/src/main/java/com/patagonia/app/domain/model/ParkRegionPreset.kt` — Curated Chilean parks presets.
- `app/src/main/java/com/patagonia/app/domain/model/DownloadedRegion.kt` — Model for saved maps.
- `app/src/main/java/com/patagonia/app/domain/repository/TileDownloadRepository.kt` — Repository interface for offline maps.
- `app/src/main/java/com/patagonia/app/data/service/TileDownloadService.kt` — Foreground downloading service.
- `app/src/main/java/com/patagonia/app/data/util/RetryUtil.kt` — Exponential backoff retry helper.
- `app/src/main/java/com/patagonia/app/presentation/map/AssetManagerScreen.kt` — Compose screen UI.
- `app/src/main/java/com/patagonia/app/presentation/viewmodel/AssetManagerUiState.kt` — UI state representation.
- `app/src/main/java/com/patagonia/app/presentation/viewmodel/AssetManagerViewModel.kt` — ViewModel implementation.
- `app/src/test/java/com/patagonia/app/data/service/TileDownloadServiceTest.kt` — Service tests.
- `app/src/test/java/com/patagonia/app/data/util/RetryUtilTest.kt` — Retry logic tests.
- `app/src/test/java/com/patagonia/app/domain/model/DownloadedRegionTest.kt` — Region unit tests.
- `app/src/test/java/com/patagonia/app/domain/model/ParkRegionPresetTest.kt` — Preset coordinates tests.
- `app/src/test/java/com/patagonia/app/domain/model/TileDownloadStateTest.kt` — State transition tests.
- `app/src/test/java/com/patagonia/app/presentation/viewmodel/AssetManagerViewModelTest.kt` — ViewModel tests.

## Deviations
- **Mapbox API Token**: Since the Mapbox token was missing, the downloader logic inside `TileDownloadService` and `TileDownloadRepositoryImpl` uses mock/placeholder logic for interacting with Mapbox's `TileStore`. The UI and download control states have been fully verified using mocks, and once a token is configured, the code can be uncommented.

## Verification Results
- All unit tests pass successfully.
- Code has been fully verified to build under Gradle.
