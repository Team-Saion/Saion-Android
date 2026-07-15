package com.saion.core.domain.usecase.auth

import com.saion.core.domain.repository.AuthRepository
import com.saion.core.model.member.MemberRole
import com.saion.core.model.result.AppResult
import javax.inject.Inject

/**
 * 카카오 ID Token으로 로그인하고 앱 세션을 시작합니다.
 *
 * 로그인에 성공하면 서버가 발급한 토큰으로 세션을 저장하고, access token의 역할 클레임을 반환합니다.
 */
class LoginWithKakaoUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(idToken: String): AppResult<MemberRole> = authRepository.loginWithKakao(idToken)
}
