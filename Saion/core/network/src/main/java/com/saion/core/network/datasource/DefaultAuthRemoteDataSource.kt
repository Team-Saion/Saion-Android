package com.saion.core.network.datasource

import com.saion.core.network.api.AuthService
import com.saion.core.network.model.auth.TokenResponse
import com.saion.core.network.model.common.ApiResponse
import javax.inject.Inject

class DefaultAuthRemoteDataSource @Inject constructor(private val service: AuthService) : AuthRemoteDataSource {
    override suspend fun loginWithKakao(idToken: String): ApiResponse<TokenResponse> = service.loginWithKakao(idToken = idToken)

    override suspend fun refreshToken(refreshToken: String): ApiResponse<TokenResponse> = service.refreshToken(refreshToken = refreshToken)
}
