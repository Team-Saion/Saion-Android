package com.saion.core.domain.usecase.auth

import com.saion.core.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * 로컬에 저장된 인증 세션을 정리합니다.
 */
class ClearSessionUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke() = authRepository.clearSession()
}
