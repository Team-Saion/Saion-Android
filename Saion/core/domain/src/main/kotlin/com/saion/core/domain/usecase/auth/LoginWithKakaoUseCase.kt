package com.saion.core.domain.usecase.auth

import com.saion.core.domain.repository.AuthRepository
import com.saion.core.model.result.AppResult
import javax.inject.Inject

class LoginWithKakaoUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(idToken: String): AppResult<Unit> = authRepository.loginWithKakao(idToken)
}
