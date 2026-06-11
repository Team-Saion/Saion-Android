package com.saion.core.network.datasource

import com.saion.core.network.model.auth.TokenResponse
import com.saion.core.network.model.common.ApiResponse

interface AuthRemoteDataSource {
    suspend fun loginWithKakao(idToken: String): ApiResponse<TokenResponse>

    suspend fun refreshToken(refreshToken: String): ApiResponse<TokenResponse>
}
