package com.saion.core.domain.usecase.auth

import com.saion.core.domain.repository.AuthRepository
import javax.inject.Inject

class IsSignedInUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(): Boolean = authRepository.isSignedIn()
}
