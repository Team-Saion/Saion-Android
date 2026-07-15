package com.saion.core.domain.usecase.auth

import com.saion.core.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * 현재 기기에 유효한 세션 정보가 저장되어 있는지 확인합니다.
 */
class IsSignedInUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Boolean = authRepository.isSignedIn()
}
