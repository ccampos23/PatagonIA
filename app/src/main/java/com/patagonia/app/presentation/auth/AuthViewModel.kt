package com.patagonia.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patagonia.app.domain.usecase.auth.GetSessionUseCase
import com.patagonia.app.domain.usecase.auth.LoginUseCase
import com.patagonia.app.domain.usecase.auth.LogoutUseCase
import com.patagonia.app.domain.usecase.auth.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel managing authentication UI state for Login and Register screens.
 *
 * On initialization, checks for an existing session via [GetSessionUseCase]. If a session
 * exists, auto-navigates past auth by emitting [AuthUiState.Success] (D-04: indefinite session).
 *
 * Auth operations (login, register, logout) emit [AuthUiState.Loading] before the operation
 * and then [AuthUiState.Success] or [AuthUiState.Error] depending on the result.
 * Error messages are surfaced as inline text on the auth screens (D-24).
 *
 * Introduced in Plan 04-01, Task 4.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val getSessionUseCase: GetSessionUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getSessionUseCase().collect { profile ->
                if (profile != null) {
                    _uiState.value = AuthUiState.Success(profile)
                }
            }
        }
    }

    /**
     * Authenticate with email and password.
     * Emits Loading → Success or Loading → Error.
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = loginUseCase(email, password)
            _uiState.value = result.fold(
                onSuccess = { AuthUiState.Success(it) },
                onFailure = { AuthUiState.Error(it.message ?: "Login failed") }
            )
        }
    }

    /**
     * Register a new account with email, password, and unique username.
     * Emits Loading → Success or Loading → Error.
     * Username availability is checked by RegisterUseCase (D-17).
     */
    fun register(email: String, password: String, username: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = registerUseCase(email, password, username)
            _uiState.value = result.fold(
                onSuccess = { AuthUiState.Success(it) },
                onFailure = { AuthUiState.Error(it.message ?: "Registration failed") }
            )
        }
    }

    /**
     * Sign out and reset state to Idle.
     */
    fun logout() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            logoutUseCase()
            _uiState.value = AuthUiState.Idle
        }
    }

    /**
     * Reset error state back to Idle (e.g. after user dismisses error message).
     */
    fun clearError() {
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Idle
        }
    }
}
