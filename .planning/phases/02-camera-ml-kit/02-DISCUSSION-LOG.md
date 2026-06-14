# Phase 2: Camera & ML Kit Integration - Discussion Log

> **Audit trail only.** Do not use as input to planning, research, or execution agents.
> Decisions are captured in CONTEXT.md — this log preserves the alternatives considered.

**Date:** 2026-06-14T21:59:00Z
**Phase:** 2-Camera & ML Kit Integration
**Areas discussed:** ML Model Strategy, Camera Capture Flow, Frame Processing, Asset Download

---

## ML Model Strategy

- Option A: Custom `.tflite` only (Recommended)
- Option B: ML Kit built-in first, `.tflite` second
- Option C: ML Kit built-in only

**User's choice:** Custom `.tflite` only (Recommended).

## Result Display

- Option A: Top-3 results with confidence scores
- Option B: Top-1 result only
- Option C: Full ranked list

**User's choice:** Top-1 result only but show top 3 in details section after capture, with report option.

## Camera Capture Flow

- Option A: Manual shutter tap (Recommended)
- Option B: Auto-capture on high confidence
- Option C: Review screen before saving

**User's choice:** Option 1 (Manual shutter tap) but with previous confirmation (like Option 3).

## Frame Processing Performance

- Option A: Live continuous recognition (Recommended)
- Option B: On-demand recognition (only on tap)

**User's choice:** Live continuous recognition (Recommended).

## Asset Download & Model Management

- Option A: Pre-Trip Asset Manager UI
- Option B: Download silently on first launch
- Option C: Bundle a lightweight core model

**User's choice:** Download whole app with functions on first launch, blocking the app until installed. The on-demand downloads requirement is explicitly overridden/deleted.
