---
phase: 04
slug: sync-engine-supabase
status: draft
nyquist_compliant: true
wave_0_complete: false
created: 2026-07-06
---

# Phase 04 — Validation Strategy

> Per-phase validation contract for feedback sampling during execution.

---

## Test Infrastructure

| Property | Value |
|----------|-------|
| **Framework** | JUnit 4 + Turbine + Mockito-Kotlin |
| **Config file** | app/build.gradle.kts |
| **Quick run command** | `./gradlew :app:testDebugUnitTest` |
| **Full suite command** | `./gradlew :app:testDebugUnitTest` |
| **Estimated runtime** | ~60 seconds |

---

## Sampling Rate

- **After every task commit:** Run `./gradlew :app:testDebugUnitTest`
- **After every plan wave:** Run `./gradlew :app:testDebugUnitTest`
- **Before `/gsd-verify-work`:** Full suite must be green
- **Max feedback latency:** 60 seconds

---

## Per-Task Verification Map

| Task ID | Plan | Wave | Requirement | Threat Ref | Secure Behavior | Test Type | Automated Command | File Exists | Status |
|---------|------|------|-------------|------------|-----------------|-----------|-------------------|-------------|--------|
| 04-01-01 | 01 | 1 | REQ-SOC-01 | T-04-02 | Setup credentials securely | build | `./gradlew :app:assembleDebug` | ❌ W0 | ⬜ pending |
| 04-01-02 | 01 | 1 | REQ-SOC-01 | T-04-01 | Encrypt auth traffic | unit | `./gradlew :app:testDebugUnitTest --tests "*SupabaseAuthDataSourceTest*"` | ❌ W0 | ⬜ pending |
| 04-01-03 | 01 | 1 | REQ-SOC-01 | T-04-04 | app_metadata authorization | unit | `./gradlew :app:testDebugUnitTest --tests "*auth*"` | ❌ W0 | ⬜ pending |
| 04-01-04 | 01 | 1 | REQ-SOC-01 | T-04-01 | Blocks when offline | unit | `./gradlew :app:testDebugUnitTest --tests "*AuthViewModelTest*"` | ❌ W0 | ⬜ pending |
| 04-02-01 | 02 | 2 | REQ-SOC-02 | T-04-05 | Room DB migration v1 to v2 | unit | `./gradlew :app:testDebugUnitTest --tests "*SyncStatus*" --tests "*CaptureDao*"` | ❌ W0 | ⬜ pending |
| 04-02-02 | 02 | 2 | REQ-SOC-02 | T-04-06 | Row-level security for own captures | unit | `./gradlew :app:testDebugUnitTest --tests "*SupabaseCaptureDataSourceTest*"` | ❌ W0 | ⬜ pending |
| 04-02-03 | 02 | 2 | REQ-SOC-02 | T-04-07 | Sharing toggle state change | unit | `./gradlew :app:testDebugUnitTest --tests "*CaptureRepository*"` | ❌ W0 | ⬜ pending |
| 04-02-04 | 02 | 2 | REQ-SOC-01 | T-04-08 | Unique username verification | unit | `./gradlew :app:testDebugUnitTest --tests "*ProfileViewModel*"` | ❌ W0 | ⬜ pending |
| 04-03-01 | 03 | 2 | REQ-SOC-02 | T-04-12 | Soft delete and visibility toggle | unit | `./gradlew :app:testDebugUnitTest --tests "*CaptureSyncEngineTest*"` | ❌ W0 | ⬜ pending |
| 04-03-02 | 03 | 2 | REQ-SOC-02 | T-04-09 | Token expiration capture quarantine | unit | `./gradlew :app:testDebugUnitTest --tests "*SyncWorkerTest*"` | ❌ W0 | ⬜ pending |
| 04-03-03 | 03 | 2 | REQ-SOC-02 | T-04-11 | Backoff constraints | unit | `./gradlew :app:testDebugUnitTest --tests "*SyncOrchestrator*"` | ❌ W0 | ⬜ pending |
| 04-03-04 | 03 | 2 | REQ-SOC-02 | T-04-12 | UI retry sync mapping | unit | `./gradlew :app:testDebugUnitTest --tests "*SettingsViewModel*"` | ❌ W0 | ⬜ pending |

*Status: ⬜ pending · ✅ green · ❌ red · ⚠️ flaky*

---

## Wave 0 Requirements

- [ ] `app/src/test/java/com/patagonia/app/data/remote/SupabaseAuthDataSourceTest.kt` — stubs for REQ-SOC-01
- [ ] `app/src/test/java/com/patagonia/app/data/sync/CaptureSyncEngineTest.kt` — stubs for REQ-SOC-02

*If none: "Existing infrastructure covers all phase requirements."*

---

## Manual-Only Verifications

| Behavior | Requirement | Why Manual | Test Instructions |
|----------|-------------|------------|-------------------|
| Offline app block | REQ-SOC-01 | Requires hardware offline control on first launch | Turn off device network, open app for first time, check if "Internet required" screen blocks navigation. |
| Background WorkManager Sync | REQ-SOC-02 | Real network changes | Add a capture offline, connect to network, check if background sync succeeds. |

---

## Validation Sign-Off

- [ ] All tasks have `<automated>` verify or Wave 0 dependencies
- [ ] Sampling continuity: no 3 consecutive tasks without automated verify
- [ ] Wave 0 covers all MISSING references
- [ ] No watch-mode flags
- [ ] Feedback latency < 60s
- [ ] `nyquist_compliant: true` set in frontmatter

**Approval:** pending
