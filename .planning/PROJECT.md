# PataGOnIA

## What This Is

PataGOnIA is an interactive, educational Android app for nature enthusiasts (ages 16-50), biologists, and photographers to identify, record, and share Chilean species during their outdoor journeys. It combines an offline-first map and trekking guide with an ML-powered nature journal and gamified social features.

## Core Value

The absolute core value is the **personal offline nature journal** (Map + ML Recognition + Personal captures) — it must provide a 100% reliable offline map and species capture experience even when deep in the wilderness without signal.

## Requirements

### Validated

<!-- Shipped and confirmed valuable. -->

(None yet — ship to validate)

### Active

<!-- Current scope. Building toward these. -->

- [ ] Interactive map with reliable offline caching for closest routes and parks.
- [ ] ML Kit camera integration for real-time species recognition.
- [ ] Personal capture journal visualized as a digital "Sticker Book" / Field Guide.
- [ ] On-demand downloading system for species wiki and ML models to optimize app size.
- [ ] User accounts and authentication powered by Supabase.
- [ ] Gamification system: XP leveling (Level 1 Explorer to Master Tracker) and Badges/Achievements.
- [ ] Two-tier reward system: Live captures get "Verified Location" XP, post-trip uploads get unverified "Photographer" points.
- [ ] Social features: Leaderboards, user profiles, and a "Fame Museum" for top liked photos.
- [ ] Crowdsourced map pins showing where specific species are frequently spotted.

### Out of Scope

<!-- Explicit boundaries. Includes reasoning to prevent re-adding. -->

- **Custom built backend from scratch** — Using Supabase instead to accelerate development and leverage its built-in offline sync capabilities.
- **Bundling all map and wiki data** — Data will be downloaded/cached on-demand to prevent the initial app download size from becoming too massive.

## Context

- **Technical Environment:** Android Native, Kotlin, Jetpack Compose, Room, Hilt, ML Kit Vision.
- **Monetization Roadmap:** Direct merch sales (t-shirts, hoodies, trekking gear featuring species designs), kickstarter, government support, and future collaborations with outdoor brands.
- **Target Audience:** Casual hikers looking for an educational experience, serious nature photographers (who prefer post-trip uploads), and biologists who can contribute technical insights.
- **Design Aesthetic:** Primary brand color is green, evoking nature and outdoor exploration.

## Constraints

- **Connectivity:** The core capture and map features must function flawlessly offline.
- **Storage Management:** On-demand downloads for models and maps must handle failure gracefully and allow users to plan ahead.

## Key Decisions

<!-- Decisions that constrain future work. Add throughout project lifecycle. -->

| Decision | Rationale | Outcome |
|----------|-----------|---------|
| Supabase for backend | Fast setup, excellent offline sync, scalable for social features | — Pending |
| On-demand Wiki/ML models | Balances 100% offline capability with initial app download size | — Pending |
| Verified vs Unverified Points | Incentivizes both real-time hikers (live camera) and professional photographers (post-trip upload) | — Pending |

## Evolution

This document evolves at phase transitions and milestone boundaries.

**After each phase transition** (via `/gsd-transition`):
1. Requirements invalidated? → Move to Out of Scope with reason
2. Requirements validated? → Move to Validated with phase reference
3. New requirements emerged? → Add to Active
4. Decisions to log? → Add to Key Decisions
5. "What This Is" still accurate? → Update if drifted

**After each milestone** (via `/gsd-complete-milestone`):
1. Full review of all sections
2. Core Value check — still the right priority?
3. Audit Out of Scope — reasons still valid?
4. Update Context with current state

---
*Last updated: 2026-06-08 after project initialization*
