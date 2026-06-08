# Pitfalls: Offline-First Maps & ML

This document outlines the critical, project-killing mistakes commonly made when building offline-first Android applications with heavy ML and Map dependencies.

## 1. The "Supabase Offline" Illusion

**Pitfall:** Assuming the Supabase Android/Kotlin SDK handles offline mutations out-of-the-box. While it caches reads reasonably well, it does *not* have a robust, built-in offline mutation queue or conflict resolution system like Firebase or Couchbase. If you just use the standard SDK, offline writes will fail.
**Warning signs:**
- Developers writing massive `try/catch` blocks around network calls to save data locally "if it fails."
- High numbers of sync conflicts or duplicate rows appearing in the database when connectivity returns.
**Prevention strategy:**
- Pivot the architecture immediately to a true local-first setup using a dedicated sync engine. Implement **PowerSync** (which integrates directly with Supabase and SQLite) or commit to building a manual Room-to-Supabase sync queue using WorkManager.
**Addressing Phase:** **Backend / Architecture Setup Phase**. Do not build UI features until the offline sync layer is proven.

## 2. Unmanaged Map Tile Limits and Missing Styles

**Pitfall:** Treating offline maps as a simple "download this area" button. Mapbox and MapLibre have strict limits (e.g., Mapbox's 750 tile-pack limit). Furthermore, developers often download the map tiles but forget to explicitly download the "Style Packs" (fonts, sprites, icons).
**Warning signs:**
- The app size balloons to gigabytes during testing.
- The map renders completely blank or crashes when the device goes offline, even though the download reported "100% success".
**Prevention strategy:**
- Cap the maximum zoom level allowed for offline downloads (e.g., zoom level 14 or 15).
- Build a dedicated `OfflineRegionManager` that explicitly downloads both `TileRegion` and `StylePack`.
- Force developers to test the app in true Airplane Mode, not just simulated network latency.
**Addressing Phase:** **Maps & Navigation Phase**.

## 3. ML Kit + CameraX OutOfMemory (OOM) Death Spiral

**Pitfall:** Running CameraX analysis and ML Kit Vision simultaneously without aggressive memory management. Instantiating new detectors in loops, or failing to recycle high-resolution Bitmaps after each frame, will quickly exhaust the Android heap.
**Warning signs:**
- The app crashes with `OutOfMemoryError` after 2-5 minutes of active camera use.
- The device experiences severe thermal throttling (gets very hot) and extreme UI lag.
**Prevention strategy:**
- Instantiate a single, Singleton instance of the ML Kit detector via Hilt.
- Set CameraX `ImageAnalysis` to `STRATEGY_KEEP_ONLY_LATEST` to drop frames if the ML pipeline is busy.
- Explicitly call `.close()` on `ImageProxy` and `.recycle()` on Bitmaps the millisecond inference is complete.
**Addressing Phase:** **ML Integration / Camera Phase**.

## 4. Trailhead Download Failures

**Pitfall:** Designing the asset download system (Models, Maps, Wiki) for high-speed Wi-Fi. Users often realize they need to download offline assets when they arrive at the trailhead, where they have 1 bar of flaky 3G. Downloads fail, models corrupt, and the app breaks.
**Warning signs:**
- Corrupted ML model errors (cannot parse model).
- App crashes due to incomplete JSON parsing of the species wiki.
**Prevention strategy:**
- Use Android `WorkManager` for all large asset downloads to allow pausing and resuming.
- Implement strict SHA checksum validation. A model/map is not moved to persistent storage until the checksum matches the server.
- Add UI prompts ("Pre-Trip Checklist") to aggressively remind users to download assets while on Wi-Fi at home.
**Addressing Phase:** **Infrastructure / Asset Management Phase**.
