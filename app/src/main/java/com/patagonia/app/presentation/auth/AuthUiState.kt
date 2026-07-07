package com.patagonia.app.presentation.auth

import com.patagonia.app.domain.model.UserProfile

/**
 * Sealed class representing the authentication UI state.
 *
 * The ViewModel emits these states to drive the auth screens:
 * - [Idle] — no auth operation in progress (initial state, or after logout)
 * - [Loading] — auth operation in progress (login/register/logout)
 * - [Success] — authenticated; [profile] contains the logged-in user's data
 * - [Error] — auth operation failed; [message] contains the inline error text (D-24)
 *
 * Introduced in Plan 04-01, Task 4.
 */
sealed class AuthUiState {
    data object Idle : AuthUiState()
    data object Loading : AuthUiState()
    data class Success(val profile: UserProfile) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}
