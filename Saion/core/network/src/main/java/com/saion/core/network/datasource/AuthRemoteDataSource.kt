package com.saion.core.network.datasource

import com.saion.core.network.model.auth.TokenResponse
import com.saion.core.network.model.common.ApiResponse

/**
 * 인증 관련 원격 데이터 소스 계약입니다.
 */
interface AuthRemoteDataSource {
    /**
     * 카카오 ID Token으로 서비스 로그인 결과를 조회합니다.
     */
    suspend fun loginWithKakao(idToken: String): ApiResponse<TokenResponse>

    /**
     * refresh token으로 인증 토큰을 재발급합니다.
     */
    suspend fun refreshToken(refreshToken: String): ApiResponse<TokenResponse>
}
