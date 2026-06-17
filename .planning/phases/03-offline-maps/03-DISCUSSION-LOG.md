# Phase 3: Offline Maps - Discussion Log

> **Audit trail only.** Do not use as input to planning, research, or execution agents.
> Decisions are captured in CONTEXT.md — this log preserves the alternatives considered.

**Date:** 2026-06-17T02:39:00Z
**Phase:** 3-Offline Maps
**Areas discussed:** Mapbox Setup & Style, Offline Tile Store & Boundaries, Pin Display & Clustering, Storage & Cache

---

## Mapbox Setup & Style

### 1. How should we store and load the Mapbox Access Token?
- **Option 1:** Store in local.properties -> BuildConfig (Keeps token out of Git, compiled securely) **[Selected]**
- **Option 2:** Store in strings.xml (Simple but risks accidental commit)
- **Option 3:** Fetch dynamically from server (Allows token rotation but requires online first-launch)

### 2. Which map styles should be available offline?
- **Option 1:** Mapbox Outdoors only (Optimized for hiking/trails, keeps offline package sizes smaller) **[Selected]**
- **Option 2:** Dual style: Outdoors + Satellite Streets (Useful for photographers, but doubles download sizes)
- **Option 3:** Custom Mapbox Studio Style (Tailored design for Chile's national parks, but requires Mapbox Studio hosting maintenance)

### 3. How should the custom trekking routes be stored and loaded?
- **Option 1:** Pre-packaged in app assets (Bundle standard Chilean trails directly in the APK as GeoJSON files, rendering them dynamically) **[Selected]**
- **Option 2:** Download via Asset Manager (Store trails on a remote server/Supabase, downloading them alongside regional map tile packs)
- **Option 3:** User-imported GPX/KML (Let users import their own trail files downloaded from apps like Suda or wikiloc, saving them to local storage)

### 4. What should the default map camera position be on launch?
- **Option 1:** Auto-locate user via GPS with fallback to Santiago/Torres del Paine (Centers on user's current location, fall back to a default coordinate if GPS/permissions are offline) **[Selected]**
- **Option 2:** Restore last-viewed position (Persists map center and zoom level in SharedPreferences and restores them on next launch)
- **Option 3:** Chile Overview zoom (Starts zoomed out to show the entirety of Chile, letting users zoom in manually)

### 5. Which integration method should we use for Compose?
- **Option 1:** Mapbox Compose Extension (com.mapbox.extension:maps-compose) (Idiomatic Compose syntax, less boilerplate)
- **Option 2:** Standard AndroidView wrapping MapView (Direct lifecycle control, highly stable, but more boilerplate code) **[Selected]**

### 6. How should map rotation and user bearing be handled?
- **Option 1:** User-controlled toggle (Toggle between 'North-up' and 'Heading-up' using device sensors for trekking direction) **[Selected]**
- **Option 2:** North-up only (Map orientation is locked to North at the top, saving battery and simplifying code)

### 7. What maximum zoom level should be supported for offline downloads?
- **Option 1:** Zoom 0 to 15 (Shows trails and topography clearly, balancing details with lightweight downloads) **[Selected]**
- **Option 2:** Zoom 0 to 17 (Shows ultra-detailed contour lines and terrain, but offline packages will be 4x larger)
- **Option 3:** Zoom 0 to 13 (Broad overview only, but trail lines might look pixelated when zoomed in)

### 8. How should custom trails be styled?
- **Option 1:** Difficulty-based coloring (Green for easy, Yellow/Orange for medium, Red for hard, helping hikers plan)
- **Option 2:** Uniform brand styling (Render all trails in the app's primary brand green color, using dashed/solid line patterns to distinguish trail hierarchy)
- **Option 3:** Simple uniform dashed lines (Minimalist look, uniform color, user taps a trail line to show name/difficulty in a popup) **[Selected]**

### 9. What should the map display if opened offline before any region is downloaded?
- **Option 1:** Placeholder redirect screen (Show a screen explaining that they are offline and need to download an offline map region first, with a button to open the Asset Manager)
- **Option 2:** Open map anyway with blank grid (Show a blank map grid or standard Mapbox offline grid, allowing custom trails to render anyway) **[Selected]**
- **Option 3:** Pre-package a low-zoom Chile outline base map (Include a minimal low-resolution vector map of Chile in app assets (~10MB) so there's always a base map, even before any region is downloaded)

**User Notes:** 
- Only Outdoors style is needed offline. Streets and city maps are completely unnecessary.
- Chile trekking routes should overlay the base map. We will use Suda app as a reference for trail lines not present on Mapbox base styles.
- Opened offline before download should show a blank grid but still render the custom trails.

---

## Offline Tile Store & Boundaries

### 1. How should users select map areas for offline download?
- **Option 1:** Pre-defined Chilean Parks & Regions (Users select from a list of predefined national parks/trekking areas, e.g. Torres del Paine, Cajón del Maipo. Easier to manage, pre-calculated sizes)
- **Option 2:** Custom bounding box selection (Users pan/zoom on the map to define a custom rectangular region to download. Highly flexible but requires internet to select the area)
- **Option 3:** Hybrid approach (Provide predefined parks as fast selections, but allow advanced users to draw custom bounding boxes) **[Selected]**

### 2. How should map download progress be managed?
- **Option 1:** Foreground Service with notifications (Ensures the download completes even if the app goes to the background, showing a progress bar in notifications and in-app UI) **[Selected]**
- **Option 2:** In-app UI progress only (Simple download logic that runs only while the app is actively open; pauses/stops if the app is closed or backgrounded)
- **Option 3:** WorkManager task (Offloads the download to Android's WorkManager, but has a 10-minute execution limit and less precise real-time UI progress feedback)

### 3. How should download interruptions and failures be handled?
- **Option 1:** Auto-retry with backoff + manual Pause/Resume (Automatically retries on transient connection losses, and lets users manually pause and resume downloads) **[Selected]**
- **Option 2:** Fail and notify immediately (Stop the download immediately on error, show a failure notification, and let the user manually tap 'Retry')
- **Option 3:** Auto-resume only when Wi-Fi is reconnected (Pauses the download on failure and waits until the system detects a Wi-Fi connection to resume automatically)

### 4. How should we handle download size and tile limits?
- **Option 1:** Warn on large regions + hard block at Mapbox limits (Warn the user if their selected custom bounding box exceeds a reasonable size, e.g. >100MB, and hard block if it exceeds Mapbox's tile limits) **[Selected]**
- **Option 2:** Restrict custom box dimensions (Lock the maximum width/height of the custom bounding box selection UI so it's physically impossible to select a region that is too large)
- **Option 3:** No limits, handle Mapbox exceptions (Let users download whatever they want, and display a standard failure message if the Mapbox SDK throws an error)

---

## Pin Display & Clustering

### 1. How should species captures be represented as map pins?
- **Option 1:** Category-specific custom icons (Paw prints for mammals, birds for avifauna, leaves for plants, mushrooms for fungi. Distinct visual categorization) **[Selected]**
- **Option 2:** Uniform pins with photo thumbnails (Display a tiny circular crop of the captured photo as the marker icon itself. Looks premium but resource intensive to scale)
- **Option 3:** Simple generic marker pins with color coding (Use the same pin shape, but color code them: green for plants, blue for birds, red for mammals)

### 2. How should high-density capture pins be clustered?
- **Option 1:** Standard clustering (Nearby markers cluster into a numbered circle. Tapping or zooming in spreads the cluster out. Performant and native to Mapbox) **[Selected]**
- **Option 2:** Spiderfication (Tapping a cluster expands the markers outwards in a spider-leg layout so they are all clickable at the current zoom level)
- **Option 3:** No clustering (Display all pins individually, letting them overlap. Simple but can get cluttered and laggy with many captures)

### 3. How should we separate or toggle personal captures vs. global crowdsourced sightings?
- **Option 1:** Unified map with toggle/layers (Allow users to toggle between 'My Journal' showing all personal pins immediately, and 'Global Sightings' applying the 10+ filtering rule) **[Selected]**
- **Option 2:** Separate screens (Have one map screen for 'My Journal' showing all personal captures, and a separate 'Global Map' for crowdsourced sightings)
- **Option 3:** Always show both combined (Display personal captures always, and overlay global sightings only if they meet the 10+ threshold, using different colors/shapes to tell them apart)

### 4. What UI element should display pin details when tapped?
- **Option 1:** Bottom Sheet (A slide-up sheet displaying the species name, photo preview, date/time, and a link to open its full wiki or journal entry)
- **Option 2:** Map popup/tooltip (A small popup balloon anchored above the pin showing basic info. Minimalist, but can be cramped) **[Selected]**
- **Option 3:** Navigate to details (Instantly transition to the full species detail page/sticker book, exiting the map view)

**User Notes:**
- Global/crowdsourced sightings should not show pins at all unless the count in a location meets a minimum threshold (10 sightings) to verify the data.
- Sighting density should have visual range groupings (e.g. 10-50, 50-300, 300-1000+, 10000+ for super common) that are easily configurable.
- When tapping a pin, a map popup with basic info shows up with a link to navigate to a detailed Wiki page with a standard reference image.

---

## Storage Management & Cache Strategy

### 1. Where should downloaded map data be stored?
- **Option 1:** Internal storage only (Store tile files in the app's internal files directory. Fast, secure, doesn't require extra runtime storage permissions, and prevents errors if the user removes an SD card) **[Selected]**
- **Option 2:** External SD card support (Allow users to choose between internal storage and SD card in settings if they have limited internal space)

### 2. Should the map use automatic ambient caching?
- **Option 1:** Hybrid: Ambient caching + explicit downloads (Browsing the map while online automatically caches the tiles you see. Users can still download explicit regions via the Asset Manager to guarantee offline availability) **[Selected]**
- **Option 2:** Strict: Explicit downloads only (Turn off Mapbox's automatic ambient cache)

### 3. How should map cache limits and deletion be managed?
- **Option 1:** Auto-eviction for ambient cache + manual deletion for downloaded regions (Set a limit (e.g. 250MB) for ambient cache that automatically clears oldest tiles first. Explicitly downloaded regions are kept safe and only deleted when the user manually taps 'Delete' in the Asset Manager) **[Selected]**
- **Option 2:** Manual cleanup of everything (Provide a single button in settings to 'Clear Map Cache' which deletes all ambient cached tiles and all downloaded offline region packs at once)
- **Option 3:** Unified hard limit (Set a total storage limit for the entire app's map cache (e.g. 1GB). If exceeded, block new ambient caching and new downloads until the user deletes an offline region manually)

---

## the agent's Discretion

None — all listed design decisions were explicitly reviewed and approved by the user.

## Deferred Ideas

None.
