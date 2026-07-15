package com.saion.core.domain.usecase.auth

import com.saion.core.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * 로컬에 저장된 access token과 refresh token을 제거합니다.
 *
 * 서버와 무관하게 현재 기기의 인증 상태만 초기화할 때 사용합니다.
 */
class ClearSessionUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke() = authRepository.clearSession()
}
