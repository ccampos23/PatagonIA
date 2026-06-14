# Phase 1: Foundation & Local DB - Discussion Log

> **Audit trail only.** Do not use as input to planning, research, or execution agents.
> Decisions are captured in CONTEXT.md — this log preserves the alternatives considered.

**Date:** 2026-06-14T15:58:00-04:00
**Phase:** 1-Foundation & Local DB
**Areas discussed:** Module Structure, Offline ID Generation

---

## Module Structure

| Option | Description | Selected |
|--------|-------------|----------|
| Single Module | Fastest to set up, easier to navigate initially. We can split it later if build times become an issue. | ✓ |
| Multi-Module by Layer | Enforces clean architecture strictly, prevents accidental UI imports in the domain. | |
| Multi-Module by Feature | Best for very large teams and apps, though overkill for v1. | |

**User's choice:** (Recommended) Single Module (packaged by layer/feature): Fastest to set up, easier to navigate initially. We can split it later if build times become an issue.
**Notes:** 

---

## Offline ID Generation

| Option | Description | Selected |
|--------|-------------|----------|
| UUIDs (Strings) | Generates unique IDs offline. Crucial for syncing with Supabase later without ID collisions or complex ID-mapping. | ✓ |
| Auto-increment Integers | Simpler locally and slightly faster query performance, but will require mapping local IDs to remote IDs during the Supabase sync phase. | |

**User's choice:** (Recommended) UUIDs (Strings): Generates unique IDs offline. Crucial for syncing with Supabase later without ID collisions or complex ID-mapping.
**Notes:** 

---

## the agent's Discretion

None explicitly stated, but the agent was given discretion on UseCase Return Types by virtue of the conversation focusing tightly on the two selected areas.

## Deferred Ideas

- ML Kit Vision and .tflite integration, plus iNaturalist database via .tflite and .txt file (Moved to Phase 2).
