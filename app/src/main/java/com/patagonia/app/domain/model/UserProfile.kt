package com.patagonia.app.domain.model

/**
 * Domain representation of the authenticated Supabase user.
 *
 * Task 2 (Plan 04-01) introduces this model with the minimum fields needed by the auth
 * data layer (SupabaseAuthDataSource) and auth use cases. Task 3 extends it with profile
 * metadata (bio, level, isPrivate) used by the Profile screen. Field semantics are
 * anchored to the Phase 04 context decisions:
 *
 * - [id]   : Supabase Auth UUID (D-20)
 * - [username]: unique username (D-17)
 * - [avatarUrl]: Supabase Storage URL for the avatar (D-18)
 * - [bio]  : user bio (D-19)
 * - [level]: gamification level (D-19, defaults to 1 — wired in Phase 5)
 * - [isPrivate]: profile-level privacy (D-14, defaults to true per Discussion Log)
 */
data class UserProfile(
    val id: String,
    val email: String,
    val username: String,
    val avatarUrl: String? = null,
    val bio: String = "",
    val level: Int = 1,
    val isPrivate: Boolean = true
)
