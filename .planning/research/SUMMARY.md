# Project Research Summary

**Project:** PataGOnIA
**Domain:** Interactive nature journal and hiking guide app
**Researched:** 2026-06-08
**Confidence:** HIGH

## Executive Summary

PataGOnIA is a sophisticated offline-first nature journal app that relies on on-device ML for real-time species recognition, overlaid on crowdsourced data and offline trail maps. To achieve a magical and reliable experience deep in the wilderness, experts build such products with a strict local-first architecture. 

The recommended approach enforces Room as the single source of truth, utilizing Android WorkManager for all background data syncing and asset downloading (maps, ML models). The primary risks involve memory traps (CameraX + ML Kit causing `OutOfMemoryErrors` or thermal throttling) and flaky downloads corrupting app states over weak connections. These are mitigated by using `STRATEGY_KEEP_ONLY_LATEST` in ML Kit, avoiding real-time social tracking to save battery, and implementing checksum-verified resumable downloads.

## Key Findings

### Recommended Stack

A modern, highly efficient native Android stack.

**Core technologies:**
- **Room + PowerSync:** Single source of truth — true local-first architecture to handle offline mutations seamlessly.
- **Android WorkManager:** Background sync — essential for resumable, reliable downloads and uploads on weak connections.
- **Supabase Kotlin SDK:** Backend connection — integrates with the local-first layer for social/leaderboard features.
- **CameraX + MlKitAnalyzer:** ML Vision — automatic coordinate mapping and frame conversions directly on-device.
- **MapLibre Compose:** Maps — native Jetpack Compose integration for offline vector tiles (avoids legacy wrappers).
- **Jetpack Compose + Navigation Compose:** UI — type-safe routing and modern reactive layouts.

### Expected Features

**Must have (table stakes):**
- **Instant Offline Identification:** On-device ML inference using regional data packs; delay breaks the "magic".
- **Combined Context Map:** Overlaying crowdsourced species locations onto offline trail maps.

**Should have (competitive):**
- **Two-Tier Incentive System:** "Verified XP" for live on-trail captures, vs "Photographer Points" for post-trip DSLR uploads.

**Anti-features (deliberately avoid):**
- **Real-time buddy tracking:** Massive battery drain; unacceptable for deep wilderness excursions.

### Architecture Approach

A strict **Inside-Out** and **Local-First** approach.

**Major components:**
1. **Local Database (Room):** The definitive state; UI never connects directly to Supabase.
2. **Camera/ML Engine:** Consumes CameraX frames, executes local inference, writes to DB.
3. **Map Engine:** Renders local vector tiles and crowdsourced pins.
4. **Sync Engine (WorkManager):** Handles background bidirectional sync with Supabase when online.

### Critical Pitfalls

1. **Supabase Offline Sync limits:** The standard SDK lacks advanced offline mutation sync. **Avoid by:** Implementing a true local-first architecture (e.g., Room + PowerSync or custom WorkManager queues).
2. **Map Limitations:** Blank maps when offline due to missing style packs. **Avoid by:** Explicitly downloading and bundling fonts/sprites with tile-packs.
3. **Memory Traps:** OOMs and thermal throttling from Camera/ML. **Avoid by:** Using `STRATEGY_KEEP_ONLY_LATEST` and ensuring aggressive bitmap recycling.
4. **Flaky Downloads:** Corrupted models/maps. **Avoid by:** Strictly enforcing checksum-verified, resumable background downloads.

## Implications for Roadmap

Based on the research, the app must be built inside-out:

### Phase 1: Foundation & Local DB
**Rationale:** The entire app relies on the local Room DB as the single source of truth.
**Delivers:** Room entities, DAOs, UseCases, and baseline DI setup.
**Avoids:** UI-coupled data logic.

### Phase 2: Camera & ML Kit Integration
**Rationale:** The core differentiating feature; needs the local DB to save captures.
**Delivers:** CameraX implementation, ML Kit on-device inference, capture storage.
**Uses:** CameraX, MlKitAnalyzer.
**Avoids:** Memory traps (OOM) by setting proper frame analysis strategies.

### Phase 3: Offline Maps
**Rationale:** Contextualizes the captures; heavily dependent on offline data caching.
**Delivers:** MapLibre Compose integration, offline tile and style pack management.
**Avoids:** Blank offline maps.

### Phase 4: Sync Engine & Supabase
**Rationale:** Connects the fully functional offline app to the social backend.
**Delivers:** Supabase integration, WorkManager sync queues, authentication.
**Avoids:** Flaky data states and data loss during offline mutations.

### Phase 5: UI Polish & Gamification
**Rationale:** Social features rely on synced data.
**Delivers:** The Two-Tier incentive system, Leaderboards, Fame Museum, and user profiles.

### Phase Ordering Rationale

This structure completely isolates the hardest offline constraints into the earliest phases, ensuring the "Core Value" (the personal offline nature journal) works flawlessly before any cloud or social logic is introduced.

### Research Flags

Phases likely needing deeper research during planning:
- **Phase 4:** Custom WorkManager / PowerSync architecture for bulletproof offline mutation sync is complex and requires careful planning.
- **Phase 3:** Managing MapLibre offline style packs efficiently.

## Confidence Assessment

| Area | Confidence | Notes |
|------|------------|-------|
| Stack | HIGH | Validated against modern Android recommendations (Compose, Hilt, CameraX). |
| Features | HIGH | Clear differentiation for target audience. |
| Architecture | HIGH | Local-first is the only reliable way to build this. |
| Pitfalls | HIGH | Common issues well documented in ML/Map mobile apps. |

**Overall confidence:** HIGH

### Gaps to Address

- **MapLibre vs Mapbox pricing:** Need to validate open-source MapLibre tile hosting sources for Patagonia trails during Phase 3 planning.
- **PowerSync vs Custom Sync:** Final decision on whether to adopt PowerSync or build custom WorkManager queues for Supabase needed in Phase 4.

---
*Research completed: 2026-06-08*
*Ready for roadmap: yes*
