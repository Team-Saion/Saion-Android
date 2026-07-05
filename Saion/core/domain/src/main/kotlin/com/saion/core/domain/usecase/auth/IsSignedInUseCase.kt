package com.saion.core.domain.usecase.auth

import com.saion.core.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * 현재 로컬 세션 존재 여부를 확인합니다.
 */
class IsSignedInUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Boolean = authRepository.isSignedIn()
}
