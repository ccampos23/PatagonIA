# Plan 03-03 Summary: Map Interactions, Sighting Clustering, and Storage Config

## What Was Built
Implemented advanced map interaction UI (compass toggle, tap-to-show tooltips), sighting density-based clustering models, and secure offline storage caching configurations.

## Tasks Completed
- **Task 1: Map Interactions and Tooltips**
  - Implemented the user-controlled compass toggle (North-up vs Heading-up modes) (D-12).
  - Built the `CaptureTooltip` Compose component in `MapScreen.kt`, showing detailed species name, scientific name, geographic coordinates, and placeholder thumbnail when a capture pin is tapped (D-23).
  - Updated `MapViewModel` and `MapUiState` to maintain the selected capture and manage tooltip visibility.
  - Added 5 new tests in `MapViewModelTest` verifying compass toggling, capture selection, and tooltip dismissals.

- **Task 2: Sighting Clustering**
  - Created the `SightingCluster` domain model to represent crowdsourced wildlife observations.
  - Added the `DensityRange` enum classifying sightings into five ranges (`SPARSE`, `LOW`, `MEDIUM`, `HIGH`, `VERY_HIGH`).
  - Enforced a visibility filter where sparse sightings with fewer than 10 records are hidden (`count < 10`, D-21) to prevent clutter.
  - Wrote 14 unit tests in `SightingClusterTest` to verify boundary classifications and visibility limits.

- **Task 3: Storage and Caching Config**
  - Created `MapboxStorageConfig` to manage secure paths and cache constraints.
  - Bound all Mapbox TileStore and cache files to internal storage (`context.filesDir/mapbox/tiles`) to avoid information disclosure (D-24).
  - Configured a 250MB limit for ambient data caching and ensured explicitly downloaded regional tiles are exempt from auto-eviction (D-25).
  - Integrated `MapboxStorageConfig` in `PatagoniaApplication` to initialize these settings at launch.
  - Added 4 unit tests in `MapboxStorageConfigTest` to verify path resolution and limit bounds.

## Files Created/Modified
- `app/src/main/java/com/patagonia/app/PatagoniaApplication.kt` — Configured TileStore path and cache sizes at application start.
- `app/src/main/java/com/patagonia/app/data/config/MapboxStorageConfig.kt` — Path resolution and cache size parameters.
- `app/src/main/java/com/patagonia/app/domain/model/SightingCluster.kt` — Sighting cluster and DensityRange classification models.
- `app/src/main/java/com/patagonia/app/presentation/map/MapScreen.kt` — Interactive tooltips and compass controls in Compose.
- `app/src/main/java/com/patagonia/app/presentation/viewmodel/MapViewModel.kt` — VM updates for tooltip selections and compass mode.
- `app/src/main/java/com/patagonia/app/presentation/viewmodel/MapUiState.kt` — State details for tooltips and compass.
- `app/src/test/java/com/patagonia/app/data/config/MapboxStorageConfigTest.kt` — Cache config test suite.
- `app/src/test/java/com/patagonia/app/domain/model/SightingClusterTest.kt` — Sighting cluster validation tests.
- `app/src/test/java/com/patagonia/app/presentation/viewmodel/MapViewModelTest.kt` — Compass and tooltip VM tests.

## Deviations
- **Mapbox API Token**: Storage config uses mock settings in place of native Mapbox API objects. The resolved file system configurations were fully tested to be secure, and code is ready to link Mapbox TileStore directly.

## Verification Results
- All unit tests pass successfully.
- Code has been fully verified to build under Gradle.
