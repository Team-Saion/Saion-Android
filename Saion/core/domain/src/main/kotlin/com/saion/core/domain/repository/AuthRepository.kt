package com.saion.core.domain.repository

import com.saion.core.model.member.MemberRole
import com.saion.core.model.result.AppResult

/**
 * 인증 도메인 경계를 정의합니다.
 */
interface AuthRepository {
    /**
     * 카카오 로그인 결과를 앱 세션으로 반영합니다.
     */
    suspend fun loginWithKakao(idToken: String): AppResult<MemberRole>

    /**
     * 현재 로컬 세션이 유효한지 확인합니다.
     */
    suspend fun isSignedIn(): Boolean
}
