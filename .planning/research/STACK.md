# Tech Stack Recommendations

## Core Backend & Sync
- **Recommendation:** **Supabase + PowerSync + SQLite (Room)**
- **Rationale:** Supabase Kotlin clients do not have automatic offline-first mutation queuing or conflict resolution. Using PowerSync as a dedicated sync layer over SQLite provides a true offline-first experience where the app reads/writes locally, and the sync engine handles the background reconciliation when a connection is restored.

## Maps
- **Recommendation:** **Mapbox Maps SDK (or MapLibre GL Native)**
- **Rationale:** Excellent offline caching support compared to Google Maps. Mapbox allows explicit downloading of tile packs and style resources. However, it requires careful management of the 750 tile-pack limit.

## Machine Learning
- **Recommendation:** **Google ML Kit Vision (On-Device)**
- **Rationale:** Runs entirely offline and is optimized for mobile. Avoid custom TensorFlow Lite models unless ML Kit lacks the specific classification needed for Chilean species.

## Asynchronous Work
- **Recommendation:** **Android WorkManager**
- **Rationale:** Critical for resuming interrupted map and model downloads when users have spotty connection at a trailhead.
