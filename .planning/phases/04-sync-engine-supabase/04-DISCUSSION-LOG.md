# Phase 4: Sync Engine & Supabase - Discussion Log

> **Audit trail only.** Do not use as input to planning, research, or execution agents.
> Decisions are captured in CONTEXT.md — this log preserves the alternatives considered.

**Date:** 2026-07-06T17:48:42Z
**Phase:** 4-Sync Engine & Supabase
**Areas discussed:** Auth Method, Background Sync Timing, Sync Conflict Strategy, Data Privacy & Crowdsourcing, Profile Schemas, Error Handling

---

## Auth Method

| Option | Description | Selected |
|--------|-------------|----------|
| 1 | Email and Password | ✓ |
| 2 | Magic Links | |
| 3 | Social Logins (Google/Apple) | ✓ |

**User's choice:** 1 is primary but 3 is secondary
**Notes:**

| Option | Description | Selected |
|--------|-------------|----------|
| 1 | Allow "Guest Mode" | |
| 2 | Block Access (Require internet initially) | ✓ |

**User's choice:** Block Access

| Option | Description | Selected |
|--------|-------------|----------|
| 1 | Defer Verification | ✓ |
| 2 | Require Verification | |

**User's choice:** Defer Verification

| Option | Description | Selected |
|--------|-------------|----------|
| 1 | Indefinite Session | ✓ |
| 2 | Standard Expiry | |

**User's choice:** Indefinite Session

---

## Background Sync Timing

| Option | Description | Selected |
|--------|-------------|----------|
| 1 | Wi-Fi Only for auto-sync | |
| 2 | Any network is fine | ✓ |

**User's choice:** Any network is fine

| Option | Description | Selected |
|--------|-------------|----------|
| 1 | No charging requirement | ✓ |
| 2 | Yes (charging only) | |

**User's choice:** No charging requirement

| Option | Description | Selected |
|--------|-------------|----------|
| 1 | Immediate sync on network restore | ✓ |
| 2 | Periodic batch only | |

**User's choice:** Immediate sync on network restore

| Option | Description | Selected |
|--------|-------------|----------|
| 1 | Exponential backoff | ✓ |
| 2 | Manual retry only | |

**User's choice:** 1 but only up to 120 seconds the the system stop tying for a while and the try again

---

## Sync Conflict Strategy

| Option | Description | Selected |
|--------|-------------|----------|
| 1 | Local-wins | ✓ |
| 2 | Last-write-wins | |
| 3 | Server-wins | |

**User's choice:** Local-wins

| Option | Description | Selected |
|--------|-------------|----------|
| 1 | Fresh install adopts remote baseline | ✓ |
| 2 | Fresh install overwrites remote | |

**User's choice:** Fresh install adopts remote baseline

| Option | Description | Selected |
|--------|-------------|----------|
| 1 | sync_status flag | ✓ |
| 2 | Simple boolean | |

**User's choice:** sync_status flag

| Option | Description | Selected |
|--------|-------------|----------|
| 1 | Soft Deletes | ✓ |
| 2 | Hard Deletes | |

**User's choice:** Soft Deletes

---

## Data Privacy & Crowdsourcing

| Option | Description | Selected |
|--------|-------------|----------|
| 1 | Private by default | ✓ |

**User's choice:** Private by default

| Option | Description | Selected |
|--------|-------------|----------|
| 1 | Per-capture toggle | ✓ |

**User's choice:** Per-capture toggle

| Option | Description | Selected |
|--------|-------------|----------|
| 1 | Exact coordinates | ✓ |

**User's choice:** Exact coordinates

| Option | Description | Selected |
|--------|-------------|----------|
| 1 | Immediate Removal | ✓ |

**User's choice:** Immediate Removal

---

## Profile Schemas

| Option | Description | Selected |
|--------|-------------|----------|
| 1 | Force unique usernames | ✓ |

**User's choice:** Force unique usernames

| Option | Description | Selected |
|--------|-------------|----------|
| 1 | Basic Avatar Upload | ✓ |

**User's choice:** Basic Avatar Upload

| Option | Description | Selected |
|--------|-------------|----------|
| 1 | Add bio & level field | ✓ |

**User's choice:** Add bio & level field

| Option | Description | Selected |
|--------|-------------|----------|
| 1 | Supabase Auth UUID | ✓ |

**User's choice:** Supabase Auth UUID

---

## Error Handling

| Option | Description | Selected |
|--------|-------------|----------|
| 1 | In-app indicator | ✓ |

**User's choice:** In-app indicator

| Option | Description | Selected |
|--------|-------------|----------|
| 1 | Quarantine and Skip | ✓ |

**User's choice:** Quarantine and Skip

| Option | Description | Selected |
|--------|-------------|----------|
| 1 | Manual Retry vs Auto-delete | ✓ |

**User's choice:** a mix of both if te sync fails the try again like 3 times and if it fails the 3 times then a manual retry with the option of delete and a error message like the image ins corrupted

| Option | Description | Selected |
|--------|-------------|----------|
| 1 | Inline error messages | ✓ |

**User's choice:** Inline error messages

## Deferred Ideas
- A gallery view in the bottom bar for old captures (Digital Sticker Book / Field Guide). (Phase 5)
