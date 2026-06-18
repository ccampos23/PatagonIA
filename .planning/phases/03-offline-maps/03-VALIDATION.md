---
phase: 03
slug: offline-maps
status: draft
nyquist_compliant: true
wave_0_complete: false
created: 2026-06-17
---

# Phase 03 — Validation Strategy

> Per-phase validation contract for feedback sampling during execution.

---

## Test Infrastructure

| Property | Value |
|----------|-------|
| **Framework** | JUnit 4 + Espresso |
| **Config file** | app/build.gradle.kts |
| **Quick run command** | `./gradlew :app:testDebugUnitTest` |
| **Full suite command** | `./gradlew :app:connectedDebugAndroidTest` |
| **Estimated runtime** | ~180 seconds |

---

## Sampling Rate

- **After every task commit:** Run `./gradlew :app:testDebugUnitTest`
- **After every plan wave:** Run `./gradlew :app:connectedDebugAndroidTest`
- **Before `/gsd-verify-work`:** Full suite must be green
- **Max feedback latency:** 180 seconds

---

## Per-Task Verification Map

| Task ID | Plan | Wave | Requirement | Threat Ref | Secure Behavior | Test Type | Automated Command | File Exists | Status |
|---------|------|------|-------------|------------|-----------------|-----------|-------------------|-------------|--------|
| 03-01-01 | 01 | 1 | REQ-CORE-01 | — | N/A | unit | `./gradlew :app:testDebugUnitTest` | ❌ W0 | ⬜ pending |
| 03-01-02 | 01 | 1 | REQ-CORE-04 | — | N/A | unit | `./gradlew :app:testDebugUnitTest` | ❌ W0 | ⬜ pending |

*Status: ⬜ pending · ✅ green · ❌ red · ⚠️ flaky*

---

## Wave 0 Requirements

- [ ] `app/src/test/java/com/patagonia/app/domain/OfflineMapRegionTest.kt` — stubs for REQ-CORE-04
- [ ] `app/src/test/java/com/patagonia/app/domain/SpeciesCaptureMappingTest.kt` — stubs for REQ-CORE-01

*If none: "Existing infrastructure covers all phase requirements."*

---

## Manual-Only Verifications

| Behavior | Requirement | Why Manual | Test Instructions |
|----------|-------------|------------|-------------------|
| Foreground Service Termination | REQ-CORE-04 | Difficult to mock OS lifecycle termination | Download a large region, switch apps, wait 5 mins, check if download completed. |
| Map Rendering Offline | REQ-CORE-01 | Requires turning off device network | Turn on Airplane mode, open map, verify tiles render correctly up to zoom 15. |

---

## Validation Sign-Off

- [ ] All tasks have `<automated>` verify or Wave 0 dependencies
- [ ] Sampling continuity: no 3 consecutive tasks without automated verify
- [ ] Wave 0 covers all MISSING references
- [ ] No watch-mode flags
- [ ] Feedback latency < 180s
- [ ] `nyquist_compliant: true` set in frontmatter

**Approval:** pending
