# Roadmap: PataGOnIA

## Overview

PataGOnIA is an interactive offline-first nature journal Android application. The journey to v1 involves building out a true local-first architecture (Room + UseCases), integrating complex CameraX and ML Kit features for on-device recognition, introducing MapLibre Compose for contextual offline maps, wiring up the background sync layer (WorkManager + Supabase), and finally polishing the UI with Gamification elements (Sticker Book, Two-Tier Rewards).

## Phases

- [x] **Phase 1: Foundation & Local DB** - Establish the single source of truth using Room and Clean Architecture.
- [ ] **Phase 2: Camera & ML Kit Integration** - Implement real-time on-device species recognition.
- [ ] **Phase 3: Offline Maps** - Integrate MapLibre Compose and regional asset downloading.
- [ ] **Phase 4: Sync Engine & Supabase** - Build bidirectional synchronization using WorkManager.
- [ ] **Phase 5: Gamification & UI Polish** - Build the Sticker Book and Two-Tier reward system.

## Phase Details

### Phase 1: Foundation & Local DB
**Goal**: Establish the single source of truth for the offline-first app.
**Depends on**: Nothing
**Requirements**: REQ-CORE-03
**Success Criteria** (what must be TRUE):
  1. Room database schemas and DAOs exist for captures, users, and locations.
  2. Repositories and UseCases expose Data via StateFlow.
  3. Hilt dependency injection is wired up for the data and domain layers.
**Plans**: 3 plans

Plans:
- [x] 01-01: Scaffold Android project and Clean Architecture layers with Hilt.
- [x] 01-02: Implement Room database, entities, and DAOs for captures.
- [x] 01-03: Create domain UseCases for managing the capture journal.

### Phase 2: Camera & ML Kit Integration
**Goal**: Integrate real-time on-device species recognition.
**Depends on**: Phase 1
**Requirements**: REQ-CORE-02, REQ-CORE-04
**Success Criteria** (what must be TRUE):
  1. CameraX preview renders on screen smoothly.
  2. ML Kit Vision processes frames and identifies objects without OutOfMemory errors.
  3. Recognized captures can be saved to the local Room database.
**Plans**: 3 plans

Plans:
- [x] 02-01: Initial First-Launch Model Download.
- [x] 02-02: CameraX Setup and Capture Flow.
- [ ] 02-03: ML Kit Custom Model Integration.

### Phase 3: Offline Maps
**Goal**: Overlay captures onto offline vector maps.
**Depends on**: Phase 2
**Requirements**: REQ-CORE-01, REQ-CORE-04
**Success Criteria** (what must be TRUE):
  1. MapLibre Compose map renders on the UI.
  2. Users can download specific regional tile and style packs for offline use.
  3. Captures are plotted as pins on the offline map.
**Plans**: 2 plans

Plans:
- [ ] 03-01: Integrate MapLibre Compose and configure local tile rendering.
- [ ] 03-02: Build UI for Asset Manager to download offline trail maps.

### Phase 4: Sync Engine & Supabase
**Goal**: Implement bidirectional synchronization of offline captures and user accounts.
**Depends on**: Phase 3
**Requirements**: REQ-SOC-01, REQ-SOC-02
**Success Criteria** (what must be TRUE):
  1. Users can register and authenticate with Supabase.
  2. Background WorkManager queues pending local captures and syncs them when online.
  3. Cloud state correctly updates local DB without data loss or conflicts.
**Plans**: 3 plans

Plans:
- [ ] 04-01: Integrate Supabase Kotlin SDK and implement Auth flows.
- [ ] 04-02: Set up Supabase Database schema for remote captures.
- [ ] 04-03: Build WorkManager bidirectional sync engine for offline-first resilience.

### Phase 5: Gamification & UI Polish
**Goal**: Build the Sticker Book and Two-Tier reward system.
**Depends on**: Phase 4
**Requirements**: REQ-GAME-01, REQ-GAME-02, REQ-GAME-03
**Success Criteria** (what must be TRUE):
  1. The Digital Sticker Book correctly reflects user's discovered species.
  2. Live captures award Verified Location XP.
  3. Post-trip photo uploads award Photographer Points.
**Plans**: 2 plans

Plans:
- [ ] 05-01: Implement Sticker Book/Field Guide UI in Compose.
- [ ] 05-02: Implement XP calculation and leveling logic based on capture type.

## Progress

**Execution Order:**
Phases execute in numeric order: 1 → 2 → 3 → 4 → 5

| Phase | Plans Complete | Status | Completed |
|-------|----------------|--------|-----------|
| 1. Foundation & Local DB | 3/3 | ✅ Complete | 2026-06-14 |
| 2. Camera & ML Kit | 2/3 | In progress | - |
| 3. Offline Maps | 0/2 | Not started | - |
| 4. Sync Engine & Supabase | 0/3 | Not started | - |
| 5. Gamification & UI Polish | 0/2 | Not started | - |
