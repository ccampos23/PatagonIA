package com.patagonia.app.domain.usecase.profile

import com.patagonia.app.domain.model.UserProfile
import com.patagonia.app.domain.repository.AuthRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(bio: String, isPrivate: Boolean): Result<UserProfile> {
        return repository.updateProfile(bio, isPrivate)
    }
}
