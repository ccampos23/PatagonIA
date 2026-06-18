# Phase 3: Offline Maps — Technical Research

**Researched:** 2026-06-17
**Phase:** 03-offline-maps
**Requirements:** REQ-CORE-01, REQ-CORE-04

---

## 1. Mapbox Maps SDK for Android — Setup & Integration

### 1.1 SDK Version & Dependencies

**Latest Stable:** `v11.25.0`

```kotlin
// app/build.gradle.kts
dependencies {
    implementation("com.mapbox.maps:android:11.25.0")
    implementation("com.mapbox.extension:maps-compose:11.25.0")
}
```

**Maven Repository** (must be added to `settings.gradle.kts`):
```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            authentication { create<BasicAuthentication>("basic") }
            credentials {
                username = "mapbox"
                password = providers.gradleProperty("MAPBOX_DOWNLOADS_TOKEN").get()
            }
        }
    }
}
```

**Secret download token (`sk.xxx`)** goes in `~/.gradle/gradle.properties`:
```properties
MAPBOX_DOWNLOADS_TOKEN=sk.your_secret_download_token_here
```

### 1.2 Access Token via `local.properties` → `BuildConfig`

**Decision D-09 from CONTEXT.md confirmed as feasible.**

Step 1 — `local.properties` (already in `.gitignore`):
```properties
MAPBOX_ACCESS_TOKEN=pk.your_public_access_token_here
```

Step 2 — Read in `app/build.gradle.kts`:
```kotlin
import java.util.Properties

android {
    buildFeatures {
        compose = true
        buildConfig = true  // Required — not currently enabled
    }
    defaultConfig {
        val properties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            properties.load(localPropertiesFile.inputStream())
        }
        val mapboxToken = properties.getProperty("MAPBOX_ACCESS_TOKEN") ?: ""
        buildConfigField("String", "MAPBOX_ACCESS_TOKEN", "\"$mapboxToken\"")
    }
}
```

Step 3 — Initialize in `PatagoniaApplication.onCreate()`:
```kotlin
MapboxOptions.accessToken = BuildConfig.MAPBOX_ACCESS_TOKEN
```

### 1.3 Compose Integration

**Discovery:** Mapbox provides an **official Compose extension** (`maps-compose`). The CONTEXT.md decision D-08 chose `AndroidView` wrapping, but the Compose extension handles lifecycle automatically and is more idiomatic. **Recommend using `maps-compose` with `MapEffect` for raw SDK access.**

**Basic MapboxMap Composable:**
```kotlin
@Composable
fun MapScreen() {
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(Point.fromLngLat(-71.5, -46.0))
            zoom(8.0)
        }
    }
    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = mapViewportState
    )
}
```

**Raw SDK access via `MapEffect`:**
```kotlin
MapboxMap(modifier = Modifier.fillMaxSize(), mapViewportState = state) {
    MapEffect(Unit) { mapView ->
        val mapboxMap = mapView.getMapboxMap()
        // Direct SDK operations (sources, layers, listeners, etc.)
    }
}
```

### 1.4 Mapbox Outdoors v12 Style

**URI:** `mapbox://styles/mapbox/outdoors-v12`

Features: Contour lines, hillshading, hiking trails, paths, ski runs, natural land cover, national parks, water features. Optimized for outdoor/recreational use.

> **Note:** Mapbox classifies Outdoors v12 as a "Classic Style." For PataGOnIA's trekking use case, it remains the most appropriate style.

---

## 2. Offline Tile Management — TileStore Architecture

### 2.1 Core Architecture

| Component | Purpose | Contents |
|-----------|---------|----------|
| **TileStore** | Central disk storage engine | Manages downloads, cache, eviction, disk quota |
| **StylePack** | Non-tile style resources | Style JSON, sprites, fonts, icons |
| **TileRegion** | Geographic area of map data | Vector/raster tiles in "tile packs" |

**Critical:** Both a **StylePack** AND a **TileRegion** must be downloaded for full offline rendering.

**TileStore Configuration:**
```kotlin
val customPath = File(context.filesDir, "patagonia_tiles").absolutePath
val tileStore = TileStore.create(customPath)
MapboxOptions.mapsOptions.tileStore = tileStore
MapboxOptions.mapsOptions.tileStoreUsageMode = TileStoreUsageMode.READ_AND_UPDATE
```

**`TileStoreUsageMode.READ_AND_UPDATE`** is recommended for PataGOnIA's offline-first design — it converts all network requests to tile pack requests automatically.

### 2.2 Downloading an Offline Region

```kotlin
val offlineManager = OfflineManager()

// 1. Tileset descriptor (defines map data to download)
val tilesetDescriptor = offlineManager.createTilesetDescriptor(
    TilesetDescriptorOptions.Builder()
        .styleURI("mapbox://styles/mapbox/outdoors-v12")
        .minZoom(0)
        .maxZoom(14)
        .pixelRatio(resources.displayMetrics.density)
        .build()
)

// 2. Bounding box as GeoJSON Polygon
fun createBoundingBoxPolygon(west: Double, south: Double, east: Double, north: Double): Polygon {
    return Polygon.fromLngLats(listOf(listOf(
        Point.fromLngLat(west, south),
        Point.fromLngLat(east, south),
        Point.fromLngLat(east, north),
        Point.fromLngLat(west, north),
        Point.fromLngLat(west, south) // close polygon
    )))
}

// 3. Load options
val loadOptions = TileRegionLoadOptions.Builder()
    .geometry(polygon)
    .descriptors(listOf(tilesetDescriptor))
    .metadata(Value("region-name"))
    .acceptExpired(false)
    .build()

// 4. Start download
val cancelable = tileStore.loadTileRegion(
    "region-id",
    loadOptions,
    { progress -> /* TileRegionLoadProgress */ },
    { result -> /* Expected<TileRegion, TileRegionError> */ }
)
```

### 2.3 Download Progress Tracking

**`TileRegionLoadProgress` fields:**

| Property | Type | Description |
|----------|------|-------------|
| `completedResourceCount` | `Long` | Resources fully downloaded |
| `requiredResourceCount` | `Long` | Total resources needed |
| `completedResourceSize` | `Long` | Cumulative bytes completed |
| `loadedResourceSize` | `Long` | Bytes downloaded from network |
| `erroredResourceCount` | `Long` | Resources that failed |

**Percentage calculation:**
```kotlin
val percent = if (progress.requiredResourceCount > 0) {
    (progress.completedResourceCount.toDouble() / progress.requiredResourceCount) * 100
} else 0.0
```

### 2.4 StylePack Download (Required for Offline)

```kotlin
offlineManager.loadStylePack(
    "mapbox://styles/mapbox/outdoors-v12",
    StylePackLoadOptions.Builder()
        .glyphsRasterizationMode(GlyphsRasterizationMode.IDEOGRAPHS_RASTERIZED_LOCALLY)
        .acceptExpired(false)
        .build(),
    { progress -> /* StylePackLoadProgress */ },
    { result -> /* Expected<StylePack, StylePackError> */ }
)
```

### 2.5 Resumable Downloads

Calling `loadTileRegion` again with the **same region ID** automatically resumes — it downloads only missing/expired resources. This is a native SDK feature.

---

## 3. Pre-defined Chilean Park Region Presets

**Recommended pattern: hardcoded `data class` list** — park boundaries are static geographic facts, compile-time safe, no JSON parsing.

```kotlin
data class ParkRegionPreset(
    val id: String,
    val nameEs: String,
    val nameEn: String,
    val region: String,
    val west: Double, val south: Double,
    val east: Double, val north: Double,
    val minZoom: Int = 0,
    val maxZoom: Int = 14,
    val estimatedSizeMB: Int
) {
    fun toPolygon(): Polygon = createBoundingBoxPolygon(west, south, east, north)
}
```

**Initial park presets (bounding boxes from geographic research):**

| Park | Region | BBox (W,S,E,N) | Est. Size |
|------|--------|-----------------|-----------|
| Torres del Paine | Magallanes | -73.5, -51.3, -72.7, -50.7 | ~50-100 MB |
| Conguillío | La Araucanía | -71.8, -38.8, -71.5, -38.6 | ~25 MB |
| Villarrica | La Araucanía | -72.1, -39.6, -71.7, -39.25 | ~30 MB |
| Queulat | Aysén | -72.6, -44.6, -72.0, -44.1 | ~40 MB |
| Puyehue | Los Ríos | -72.3, -40.85, -71.8, -40.5 | ~30 MB |
| Nahuelbuta | La Araucanía | -73.15, -37.9, -72.8, -37.65 | ~20 MB |
| Lauca | Arica y Parinacota | -69.65, -18.45, -69.03, -18.05 | ~35 MB |

> **Sizes are estimates** — must benchmark with actual downloads during development.

---

## 4. Zoom Level Impact & Storage Budget

**Tile count grows exponentially with zoom.**

| Zoom | Tiles ~50km² park | Approx. tile size (vector) |
|------|-------------------|---------------------------|
| 0-5 | 1-2 | ~10-15 KB |
| 10 | ~10-50 | ~20-50 KB |
| 12 | ~150-600 | ~20-40 KB |
| 14 | ~2K-10K | ~15-30 KB |
| 15 | ~8K-40K | ~10-25 KB |

**Benchmark references (Mapbox vector tiles):**
- Greater London (zoom 0-15): ~120 MB
- Barcelona city: ~83 MB
- Contiguous USA (zoom 0-9): ~290 MB

**Chilean national park estimates (rural/wilderness):**
- Small park (~200 km², zoom 0-14): **15-30 MB**
- Medium park (~500 km², zoom 0-14): **30-60 MB**
- Large park (Torres del Paine ~2400 km², zoom 0-14): **50-100 MB**

**Recommendation:**
- Default zoom 0-14 (sufficient for trail-level navigation)
- Zoom 15 roughly doubles download size — omit per D-18 (max 15, with D-14 confirmed as sufficient)
- Budget **500 MB** total disk quota for all parks combined
- Warn at 100 MB per custom region (D-18)

---

## 5. GeoJSON Trail Rendering

**Decision D-13: Pre-packaged Chilean trails as GeoJSON in app assets.**

```kotlin
// Adding a GeoJSON source and dashed line layer
mapboxMap.loadStyle("mapbox://styles/mapbox/outdoors-v12") { style ->
    // 1. Load GeoJSON from assets
    val geoJsonString = context.assets.open("trails/torres_del_paine.geojson")
        .bufferedReader().readText()

    // 2. Add source
    style.addSource(geoJsonSource("trail-source") {
        data(geoJsonString)
    })

    // 3. Add dashed line layer (Decision D-13: uniform dashed lines)
    style.addLayer(lineLayer("trail-layer", "trail-source") {
        lineColor("#4CAF50")              // App brand green
        lineWidth(3.0)                    // Moderate width
        lineDasharray(listOf(2.0, 1.5))   // Dash pattern
        lineCap(LineCap.ROUND)
        lineJoin(LineJoin.ROUND)
        lineOpacity(0.85)
    })
}
```

**Trail popup on tap (Decision D-14):**
```kotlin
mapboxMap.addOnMapClickListener { point ->
    val screenPoint = mapboxMap.pixelForCoordinate(point)
    val features = mapboxMap.queryRenderedFeatures(screenPoint, RenderedQueryOptions(listOf("trail-layer"), null))
    if (features.isNotEmpty()) {
        val name = features[0].feature.getStringProperty("name")
        val difficulty = features[0].feature.getStringProperty("difficulty")
        // Show popup with name and difficulty
    }
    true
}
```

---

## 6. Custom Markers & Clustering

### 6.1 Category-Specific Custom Icons (Decision D-19)

```kotlin
// Add icons to map style
mapboxMap.getStyle { style ->
    style.addImage("marker-mammal", BitmapFactory.decodeResource(resources, R.drawable.ic_paw))
    style.addImage("marker-bird", BitmapFactory.decodeResource(resources, R.drawable.ic_bird))
    style.addImage("marker-plant", BitmapFactory.decodeResource(resources, R.drawable.ic_leaf))
    style.addImage("marker-fungi", BitmapFactory.decodeResource(resources, R.drawable.ic_mushroom))
}

// Create annotations
val pointOptions = PointAnnotationOptions()
    .withPoint(Point.fromLngLat(capture.longitude, capture.latitude))
    .withIconImage("marker-${capture.category}")
    .withIconSize(1.0)
```

### 6.2 Clustering (Decision D-22: Configurable density ranges)

```kotlin
val clusterConfig = AnnotationConfig(
    annotationSourceOptions = AnnotationSourceOptions(
        clusterOptions = ClusterOptions(
            cluster = true,
            clusterRadius = 50,
            clusterMaxZoom = 14,
            circleRadius = 18.0,
            circleColor = Color.parseColor("#4CAF50"),
            textColor = Color.WHITE,
            textSize = 12.0
        )
    )
)
```

**Performance:** `PointAnnotationManager` handles ~250-500 markers. For larger datasets (global sightings), use source-based `SymbolLayer` or `CircleLayer` for better performance with thousands of points.

---

## 7. Android Foreground Service for Tile Downloads

### 7.1 Manifest Requirements

```xml
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_DATA_SYNC" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

<service
    android:name=".data.service.TileDownloadService"
    android:foregroundServiceType="dataSync"
    android:exported="false" />
```

### 7.2 SDK Compatibility Table

| SDK Level | Requirement | Action |
|-----------|-------------|--------|
| 26+ (minSdk) | `FOREGROUND_SERVICE` permission | Manifest |
| 26+ | NotificationChannel required | `onCreate()` |
| 33+ | `POST_NOTIFICATIONS` runtime permission | Runtime request |
| 34+ (targetSdk) | `foregroundServiceType` mandatory | Manifest `dataSync` |
| 34+ | Type-specific permission | `FOREGROUND_SERVICE_DATA_SYNC` |
| 35 | **6-hour `dataSync` timeout** | Implement `onTimeout()`, save checkpoint |
| 35 | Cannot start from BOOT_COMPLETED | Only user-initiated |

### 7.3 Key Implementation Patterns

**Service architecture:**
- `@AndroidEntryPoint` for Hilt field injection
- `SupervisorJob() + Dispatchers.IO` coroutine scope
- Companion object `StateFlow` for UI observation
- Notification throttling to ~1 update/second
- `ServiceCompat.startForeground()` for backward compatibility

**Android 15 Timeout Handling (CRITICAL):**
```kotlin
override fun onTimeout(startId: Int, fgsType: Int) {
    // 6-hour limit reached — MUST stop within seconds
    downloadJob?.cancel()
    saveCheckpoint()
    ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    stopSelf(startId)
}
```

**Exponential backoff retry:**
```kotlin
suspend fun <T> retryWithBackoff(
    times: Int = 3,
    initialDelay: Long = 1_000L,
    maxDelay: Long = 30_000L,
    factor: Double = 2.0,
    block: suspend () -> T
): T
```

### 7.4 File Placement (Clean Architecture)

| File | Path |
|------|------|
| `TileDownloadService.kt` | `data/service/` |
| `TileDownloadState.kt` | `domain/model/` |
| `TileDownloadRepository.kt` (interface) | `domain/repository/` |
| `TileDownloadRepositoryImpl.kt` | `data/repository/` |
| `RetryUtil.kt` | `data/util/` |
| `DownloadModule.kt` (Hilt) | `di/` |

---

## 8. Storage & Caching Strategy

### 8.1 Ambient Caching (Decision D-25)

```kotlin
// Set 250MB ambient cache quota
tileStore.setOption(
    TileStoreOptions.DISK_QUOTA,
    Value(250L * 1024L * 1024L)
)
```

**Behavior:** Oldest-first auto-eviction when approaching quota. Explicitly downloaded regions are **protected** from eviction.

### 8.2 Storage Management APIs

```kotlin
// List all regions
tileStore.getAllTileRegions { /* List<TileRegion> */ }

// Delete specific region
tileStore.removeTileRegion("region-id")

// Query total storage
tileStore.getAllTileRegions { result ->
    val totalBytes = result.value?.sumOf { it.completedResourceSize } ?: 0L
}
```

### 8.3 Internal Storage Only (Decision D-24)

Store at `context.filesDir/patagonia_tiles/`. No SD card support, no runtime storage permissions needed.

---

## 9. Error Handling & Resilience

| Error Type | Fatal? | Description | User Action |
|-----------|--------|-------------|-------------|
| `CONNECTION` | No | Network issue | Auto-retry with backoff (D-17) |
| `DISK_FULL` | Yes | Device storage exhausted | Delete regions or free storage |
| `TILE_COUNT_LIMIT_EXCEEDED` | Yes | ~750 tile pack limit | Delete old regions |
| `CANCELED` | — | Duplicate region download | Skip |

**Pre-download validation:**
- Check available device storage (`StatFs`)
- Check existing region count (< 700 to leave margin)
- Estimate download size and warn if > 100MB (D-18)

---

## 10. Project-Specific Integration Notes

### 10.1 Current Build Configuration Gaps

1. **`buildConfig = true`** must be added to `buildFeatures` in `app/build.gradle.kts`
2. **Mapbox Maven repository** must be added to `settings.gradle.kts`
3. **`local.properties`** already exists and is in `.gitignore` ✓
4. **`minSdk = 26`** satisfies Mapbox minimum of API 21 ✓
5. **Java 17** exceeds Mapbox minimum of Java 8 ✓

### 10.2 Existing Reusable Code

- `Capture` domain model has `latitude`, `longitude` fields → direct mapping to map pins
- `GetCapturesUseCase` → ready to fetch captures for map overlay
- `PatagoniaApplication` has `@HiltAndroidApp` → ready for Hilt service injection
- No navigation graph exists yet → will need to be created for map/asset manager screens
- No existing Mapbox code → greenfield implementation

### 10.3 New Dependencies Required

```kotlin
// Mapbox
implementation("com.mapbox.maps:android:11.25.0")
implementation("com.mapbox.extension:maps-compose:11.25.0")

// Location Services (for auto-locate user camera)
implementation("com.google.android.gms:play-services-location:21.2.0")

// Navigation Compose (for screen routing)
implementation("androidx.navigation:navigation-compose:2.7.7")
```

---

## Validation Architecture

### Dimension 1: Functional Correctness
- Map renders correctly with Outdoors v12 style
- GeoJSON trails overlay renders as dashed lines
- Offline regions download and render without network

### Dimension 2: Performance
- Map scroll/zoom is smooth at 60fps
- Download progress updates without UI lag (throttled to 1/sec)
- Marker clustering handles 500+ pins without jank

### Dimension 3: Reliability
- Downloads resume after app backgrounding/foregrounding
- `onTimeout()` gracefully saves checkpoint on Android 15
- Network retry with backoff recovers from transient failures

### Dimension 4: Architecture
- Clean Architecture boundaries respected (Presentation → Domain ← Data)
- Hilt injection for all new components
- Repository interfaces in domain layer

### Dimension 5: Security
- Access token compiled from `local.properties`, never committed to Git
- Download token in `~/.gradle/gradle.properties`, never in project

### Dimension 6: Edge Cases
- Map opened offline without downloaded regions → blank grid + trail overlay (D-11)
- Custom bounding box > 100MB → warning dialog (D-18)
- Custom bounding box > 750k tiles → hard block (D-18)
- Device storage full → clear error message with cleanup guidance

---

*Research completed: 2026-06-17*
*Phase: 03-offline-maps*
