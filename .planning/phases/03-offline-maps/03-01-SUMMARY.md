# Plan 03-01 Summary: Integrate Mapbox Maps SDK and Core Map Interface

## What Was Built
Integrated the Mapbox Maps SDK foundation and built the core map interface for PataGOnIA. This includes the MapScreen composable with AndroidView wrapper pattern, domain models for trails and species pins, a GeoJSON parser for Chilean trail data, and category-specific species classification. The MapView uses a placeholder until the Mapbox token is configured, with full Mapbox Outdoors style ready to activate.

## Tasks Completed
- **Task 1: Setup Mapbox Dependencies** — Added Mapbox Maps SDK and Compose extension dependencies to `app/build.gradle.kts` (commented pending token). Enabled `buildConfig = true`. Added location/internet permissions to AndroidManifest. Added Mapbox token BuildConfig field from `local.properties`.
- **Task 2: Implement MapScreen Wrapper (TDD)** — Created `MapScreen` composable with state hoisting pattern. `MapScreenContent` is stateless for testability. Uses `AndroidView` wrapper pattern (D-08) with Mapbox Outdoors style (D-10). Created `MapViewModel` with `MapUiState` StateFlow. Implemented My Journal / Global Sightings toggle (D-20) and compass orientation toggle (D-12). 8 unit tests written and passing.
- **Task 3: Render Trails and Pins** — Created `Trail` domain model and `TrailGeoJsonParser` for parsing pre-packaged Chilean trail GeoJSON files (D-13). Created `SpeciesCategory` enum with heuristic classification for Chilean fauna/flora (D-19). Created `MapPin` data class with `fromCapture()` factory method. Added Torres del Paine sample trail GeoJSON asset. 19 unit tests written and passing.

## Files Created/Modified
- `app/build.gradle.kts` — Mapbox SDK dependencies (commented), org.json test dependency, buildConfig enabled
- `app/src/main/AndroidManifest.xml` — Internet + location permissions
- `app/src/main/java/com/patagonia/app/presentation/map/MapScreen.kt` — Core map composable with controls
- `app/src/main/java/com/patagonia/app/presentation/viewmodel/MapViewModel.kt` — Map state management
- `app/src/main/java/com/patagonia/app/presentation/viewmodel/MapUiState.kt` — Immutable map UI state
- `app/src/main/java/com/patagonia/app/domain/model/Trail.kt` — Trail data class
- `app/src/main/java/com/patagonia/app/domain/model/MapPin.kt` — Map pin with capture conversion
- `app/src/main/java/com/patagonia/app/domain/model/SpeciesCategory.kt` — Category classification enum
- `app/src/main/java/com/patagonia/app/data/local/TrailGeoJsonParser.kt` — GeoJSON to Trail parser
- `app/src/main/assets/trails/torres_del_paine.geojson` — Sample trail data
- `app/src/test/java/com/patagonia/app/presentation/viewmodel/MapViewModelTest.kt` — 8 ViewModel tests
- `app/src/test/java/com/patagonia/app/data/local/TrailGeoJsonParserTest.kt` — 7 parser tests
- `app/src/test/java/com/patagonia/app/domain/model/MapPinTest.kt` — 3 pin tests
- `app/src/test/java/com/patagonia/app/domain/model/SpeciesCategoryTest.kt` — 9 category tests

## Deviations
- **Mapbox SDK commented out**: Dependencies are declared but commented because a valid `MAPBOX_DOWNLOADS_TOKEN` secret is needed in `gradle.properties` for the Mapbox Maven repository. A `MapPlaceholder` composable stands in until the token is configured. The full Mapbox integration code is documented in comments within `MapScreen.kt`.
- **org.json test dependency**: Added `org.json:json:20231013` as `testImplementation` because Android's `org.json.JSONObject` is stubbed in JVM unit tests. This provides a real implementation for `TrailGeoJsonParser` tests.

## Verification Results
- `./gradlew :app:testDebugUnitTest` — **BUILD SUCCESSFUL** — 27 tests, 0 failures

## Self-Check: PASSED
