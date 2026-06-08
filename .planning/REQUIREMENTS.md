# Project Requirements

**Project:** PataGOnIA
**Phase:** Initial Setup

## Validated Requirements

<!-- Shipped and confirmed valuable. -->

*(None yet — ship to validate)*

## Active Requirements (v1)

### Core User Experience (MAP/ML)
- [ ] REQ-CORE-01: **Interactive Offline Map:** Integration of MapLibre Compose for rendering offline vector tiles and trails.
- [ ] REQ-CORE-02: **On-Device ML Camera:** Real-time species recognition using CameraX and ML Kit Vision with `STRATEGY_KEEP_ONLY_LATEST`.
- [ ] REQ-CORE-03: **Local-First Capture Journal:** Captures are instantly saved locally via Room database and marked as pending sync.
- [ ] REQ-CORE-04: **Asset Manager:** UI to download map regions, ML models, and the verified species wiki on-demand before trips.

### Authentication & Social (SOCIAL)
- [ ] REQ-SOC-01: **User Accounts:** Registration and login powered by the Supabase Kotlin SDK.
- [ ] REQ-SOC-02: **Background Sync:** Reliable bidirectional synchronization of local captures and crowdsourced map pins using Android WorkManager.

### Gamification (GAME)
- [ ] REQ-GAME-01: **Digital Sticker Book:** A field guide visualization that unlocks as users capture new species.
- [ ] REQ-GAME-02: **Two-Tier Reward System:** "Verified Location" XP for live captures vs "Photographer Points" for post-trip DSLR uploads.
- [ ] REQ-GAME-03: **Leveling System & Badges:** XP tracking and achievements (e.g., "Night Owl Tracker").

## Deferred Requirements (v2)

- [ ] REQ-SOC-V2-01: **Fame Museum & Leaderboards:** Ranking the top liked photos globally.
- [ ] REQ-MAP-V2-01: **Crowdsourced Map Pins:** Showing heatmaps of where specific animals are frequently found.
- [ ] REQ-COMM-V2-01: **Social Profiles:** Following other users and viewing their captures.

## Out of Scope

- **Real-Time Buddy Tracking:** Not building real-time GPS tracking of other hikers. **Reason:** Unacceptable battery drain in deep wilderness scenarios.
- **Custom Backend from Scratch:** Not building a custom API server. **Reason:** Relying on Supabase to accelerate development and ensure offline-sync reliability.
- **Bundling Assets in App:** Not bundling all heavy ML models and Map tiles natively in the APK. **Reason:** Bloats initial download size; requires on-demand Pre-Trip Asset Checker instead.

## Requirements Traceability

| Requirement | Satisfied By | Status |
|-------------|--------------|--------|
| REQ-CORE-01 | | Not Started |
| REQ-CORE-02 | | Not Started |
| REQ-CORE-03 | | Not Started |
| REQ-CORE-04 | | Not Started |
| REQ-SOC-01 | | Not Started |
| REQ-SOC-02 | | Not Started |
| REQ-GAME-01 | | Not Started |
| REQ-GAME-02 | | Not Started |
| REQ-GAME-03 | | Not Started |
