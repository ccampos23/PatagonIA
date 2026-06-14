# Phase 1: Foundation & Local DB - Context

**Gathered:** 2026-06-14T15:58:00-04:00
**Status:** Ready for planning

<domain>
## Phase Boundary

Establish the single source of truth for the offline-first app by setting up Room database schemas/DAOs for captures, configuring Clean Architecture domains with UseCases, and wiring up Hilt dependency injection.

</domain>

<decisions>
## Implementation Decisions

### Module Structure
- **D-01:** Single Module (packaged by layer/feature) — Fastest to set up, easier to navigate initially.

### Offline ID Generation
- **D-02:** UUIDs (Strings) — Generates unique IDs offline, preventing collisions during Supabase sync later.

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

### Established Patterns
- Clean Architecture + MVVM: Presentation -> Domain <- Data
- Dependency Injection: Hilt exclusively
- Concurrency: Coroutines and Flows exclusively for reactive programming

</code_context>

<specifics>
## Specific Ideas

No specific UI requirements — open to standard Jetpack Compose approaches as long as they follow Clean Architecture bounds.

</specifics>

<deferred>
## Deferred Ideas

- **ML Kit Vision and .tflite integration:** The user explicitly mentioned "ML detection, we are gonna use the google ML Kit Vision + iNaturalist databse via '.tflite' file, + a .txt file with the scientific names of the species." This belongs in Phase 2 (Camera & ML Kit Integration).

</deferred>

---

*Phase: 1-Foundation & Local DB*
*Context gathered: 2026-06-14T15:58:00-04:00*
