# Phase 3: Offline Maps - Context

**Gathered:** 2026-06-17T02:39:00Z
**Status:** Ready for planning

<domain>
## Phase Boundary

Overlay species captures onto offline vector maps using Mapbox Maps SDK for Android, and provide an Asset Manager UI for regional offline map downloads.

</domain>

<decisions>
## Implementation Decisions

### Map SDK & Credentials Setup
- **D-08:** Wrap standard native Mapbox `MapView` inside a Compose `AndroidView` for direct lifecycle and offline management control.
- **D-09:** Store the Mapbox Access Token in `local.properties` and compile it securely into `BuildConfig`.
- **D-10:** Use Mapbox Outdoors style only (optimized for offline trekking, city/street details omitted).
- **D-11:** If the map is launched offline before downloading any regional tiles, open it anyway with a blank grid and show the custom trails overlay.
- **D-12:** Implement a user-controlled compass toggle to switch between "North-up" and "Heading-up" map orientation.

### Custom Trekking Trails
- **D-13:** Pre-package standard Chilean trails directly in the app assets as GeoJSON files, rendering them dynamically as simple uniform dashed lines.
- **D-14:** Tapping a trail path shows its name and difficulty in a basic map popup.

### Offline Downloading (Tile Store)
- **D-15:** Implement a hybrid offline download scope: pre-defined Chilean national parks as quick-download presets, plus a custom bounding box selection tool for advanced custom areas.
- **D-16:** Run offline map downloads via an Android Foreground Service with progress notifications to prevent OS termination in the background.
- **D-17:** Implement auto-retry with backoff for network drops during tile downloads, along with manual Pause/Resume buttons.
- **D-18:** Max offline zoom level is capped at 15. Warn the user if their selected custom bounding box exceeds 100MB, and hard block if it exceeds Mapbox's tile store limits (750k tiles).

### Pins & Sightings
- **D-19:** Use category-specific custom icons (e.g. paw prints for mammals, leaves for plants, birds for avifauna, mushrooms for fungi) to represent species captures on the map.
- **D-20:** Implement a unified map screen with a toggle or layer selector to switch between **My Journal** (personal captures) and **Global Sightings** (crowdsourced database).
- **D-21:** Filter crowdsourced sightings to only show pins when the sighting count in a given grid/location is at least 10 (configurable threshold to prevent noise).
- **D-22:** Visual ranges for crowdsourced sighting density clusters must be configured using easily changeable range limits (e.g., 10-50, 50-300, 300-1000+).
- **D-23:** Tapping a map pin displays an anchored map popup tooltip with basic info (species name, thumbnail). Tapping this popup navigates the user to a detailed Species Wiki page.

### Storage & Caching
- **D-24:** Store all downloaded map data in internal storage (`context.filesDir`) only.
- **D-25:** Use a hybrid caching model: ambient caching of viewed tiles when online (250MB limit, oldest-first auto-eviction) + explicit downloads (protected from auto-eviction).

### the agent's Discretion
- Standard Mapbox Outdoors-v12 style configuration details.
- Exact styling parameters for dashed trail lines (width, color, dash pattern).
- Foreground service channel details and notification styling.

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### Architecture & Stack
- `.planning/codebase/ARCHITECTURE.md` — Defines Clean Architecture and MVVM boundaries
- `.planning/codebase/CONVENTIONS.md` — Defines Kotlin and Compose conventions
- `.planning/codebase/STACK.md` — Defines Android tech stack
- `.planning/codebase/INTEGRATIONS.md` — Defines External Dependencies and Integrations

</canonical_refs>

<code_context>
## Existing Code Insights

### Reusable Assets
- `Capture` domain model (`app/src/main/java/com/patagonia/app/domain/model/Capture.kt`): Includes fields for `speciesName`, `scientificName`, `confidence`, and `imagePath` which will be mapped to pins.
- `GetCapturesUseCase` (`app/src/main/java/com/patagonia/app/domain/usecase/GetCapturesUseCase.kt`): Ready to fetch all captured species to display on the map.

### Established Patterns
- Clean Architecture + MVVM: Presentation -> Domain <- Data
- Dependency Injection: Hilt exclusively

### Integration Points
- Navigation: Add map screen and asset manager screen routes to navigation graph.
- MainActivity: Needs to handle map navigation and location permissions checks.

</code_context>

<specifics>
## Specific Ideas

- **Suda App Style Trails:** Use Suda (Chilean trekking app) trail file layouts as a reference to compile the local GeoJSON files representing trails in Chile.
- **Wiki Page Details:** The detailed Species Wiki page must contain comprehensive species info, including a standard/high-quality reference image.

</specifics>

<deferred>
## Deferred Ideas

None — discussion stayed within phase scope.

</deferred>

---

*Phase: 3-Offline Maps*
*Context gathered: 2026-06-17T02:39:00Z*
