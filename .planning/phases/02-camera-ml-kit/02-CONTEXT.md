# Phase 2: Camera & ML Kit Integration - Context

**Gathered:** 2026-06-14T21:59:00Z
**Status:** Ready for planning

<domain>
## Phase Boundary

Integrate real-time on-device species recognition using CameraX and ML Kit Vision. Processes frames to identify species and allows saving recognized captures to the local Room database.
</domain>

<decisions>
## Implementation Decisions

### ML Model Strategy
- **D-03:** Custom `.tflite` model only — Use ML Kit's Custom Model API to load the iNaturalist model. The `.txt` file maps output indices to scientific names. This provides specific Chilean species results instead of generic labels.

### Result Display
- **D-04:** Top-1 result prominent — Show only the single highest-confidence match prominently during live preview. After capture, the review screen still only shows the Top-1 result by default. The top 3 results and their confidence percentages are hidden behind a tiny "details/info" button in the corner. The "report wrong match" option is kept within or alongside this detailed info.

### Camera Capture Flow
- **D-05:** Manual tap to capture, then review — The user taps the shutter button when ready. This opens a review screen displaying the photo, the identified species, and the confidence score. The user must confirm before the capture is saved to the database.

### Frame Processing Performance
- **D-06:** Live continuous recognition — Run the ML model continuously on the camera preview to give real-time feedback. Implement a frame dropping strategy (e.g., processing only every N frames) to ensure the main thread isn't blocked and the device doesn't lag.

### Asset Download & Model Management
- **D-07:** First-launch blocking download — Overriding previous requirements, the app will download all necessary models (`.tflite`, `.txt`, etc.) on the first launch. The user is blocked from entering the main app until the entire download is complete to ensure full offline functionality out-of-the-box.
</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### Architecture & Stack
- `.planning/codebase/ARCHITECTURE.md` — Defines Clean Architecture and MVVM boundaries
- `.planning/codebase/CONVENTIONS.md` — Defines Kotlin and Compose conventions
- `.planning/codebase/STACK.md` — Defines Android tech stack

</canonical_refs>

<code_context>
## Existing Code Insights

### Reusable Assets
- `CaptureEntity` / `Capture` domain model: Already includes fields for `speciesName`, `scientificName`, `confidence`, and `imagePath`.
- `AddCaptureUseCase`: Ready to persist captures to the Room database.

### Established Patterns
- Clean Architecture + MVVM: Presentation -> Domain <- Data
- Dependency Injection: Hilt exclusively
</code_context>

<specifics>
## Specific Ideas

- The UI should incorporate a "details section" post-capture showing top 3 results and a report wrong match button.
</specifics>

<deferred>
## Deferred Ideas

(None)
</deferred>
