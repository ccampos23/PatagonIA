package com.patagonia.app.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patagonia.app.domain.model.SyncStatus
import com.patagonia.app.domain.repository.AuthRepository
import com.patagonia.app.domain.repository.CaptureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ProfileUiState(
    val username: String = "",
    val email: String = "",
    val bio: String = "",
    val level: Int = 1,
    val avatarUrl: String? = null,
    val captureCount: Int = 0,
    val syncErrorCount: Int = 0,
    val showSyncWarning: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val captureRepository: CaptureRepository
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = combine(
        authRepository.observeSession(),
        captureRepository.getCaptures()
    ) { user, captures ->
        if (user != null) {
            val syncErrors = captures.count { it.syncStatus == SyncStatus.SYNC_FAILED }
            ProfileUiState(
                username = user.username,
                email = user.email,
                bio = user.bio,
                level = user.level,
                avatarUrl = user.avatarUrl,
                captureCount = captures.size,
                syncErrorCount = syncErrors,
                showSyncWarning = syncErrors > 0
            )
        } else {
            ProfileUiState()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState()
    )
}
