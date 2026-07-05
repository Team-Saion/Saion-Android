package com.saion.core.network.datasource

import com.saion.core.network.api.AuthService
import com.saion.core.network.model.auth.TokenResponse
import com.saion.core.network.model.common.ApiResponse
import javax.inject.Inject

/**
 * [AuthService]를 그대로 위임하는 기본 인증 원격 데이터 소스입니다.
 */
class DefaultAuthRemoteDataSource @Inject constructor(private val service: AuthService) : AuthRemoteDataSource {
    override suspend fun loginWithKakao(idToken: String): ApiResponse<TokenResponse> = service.loginWithKakao(idToken = idToken)

    override suspend fun refreshToken(refreshToken: String): ApiResponse<TokenResponse> = service.refreshToken(refreshToken = refreshToken)
}
