package com.patagonia.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patagonia.app.domain.model.SyncStatus
import com.patagonia.app.domain.repository.AuthRepository
import com.patagonia.app.domain.repository.CaptureRepository
import com.patagonia.app.domain.usecase.profile.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val email: String = "",
    val username: String = "",
    val bio: String = "",
    val isPrivate: Boolean = true,
    val hasSyncErrors: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val captureRepository: CaptureRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        authRepository.observeSession(),
        captureRepository.getPendingSyncCaptures()
    ) { user, pendingCaptures ->
        if (user != null) {
            SettingsUiState(
                email = user.email,
                username = user.username,
                bio = user.bio,
                isPrivate = user.isPrivate,
                hasSyncErrors = pendingCaptures.any { it.syncStatus == SyncStatus.SYNC_FAILED }
            )
        } else {
            SettingsUiState()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun togglePrivacy(isPrivate: Boolean) {
        viewModelScope.launch {
            val currentState = uiState.value
            updateProfileUseCase(currentState.bio, isPrivate)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun retrySync() {
        viewModelScope.launch {
            val pending = captureRepository.getPendingSyncCaptures().first()
            pending.forEach { capture ->
                if (capture.syncStatus == SyncStatus.SYNC_FAILED) {
                    val nextStatus = when {
                        capture.isDeleted -> SyncStatus.PENDING_DELETE
                        capture.remoteId == null -> SyncStatus.PENDING_INSERT
                        else -> SyncStatus.PENDING_UPDATE
                    }
                    captureRepository.updateCapture(capture.copy(syncStatus = nextStatus))
                }
            }
        }
    }
}
