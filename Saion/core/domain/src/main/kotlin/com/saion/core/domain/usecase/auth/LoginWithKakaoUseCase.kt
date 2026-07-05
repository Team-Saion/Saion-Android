package com.saion.core.domain.usecase.auth

import com.saion.core.domain.repository.AuthRepository
import com.saion.core.model.member.MemberRole
import com.saion.core.model.result.AppResult
import javax.inject.Inject

/**
 * 카카오 ID Token으로 로그인하고 앱 세션을 시작합니다.
 */
class LoginWithKakaoUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(idToken: String): AppResult<MemberRole> = authRepository.loginWithKakao(idToken)
}
