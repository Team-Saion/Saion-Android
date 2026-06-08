package com.saion.core.network.api

import com.saion.core.network.model.auth.KakaoLoginRequest
import com.saion.core.network.model.auth.RefreshTokenRequest
import com.saion.core.network.model.auth.TokenResponse
import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.common.ApiResponse.Companion.toApiResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AuthService(private val client: HttpClient) {
    suspend fun loginWithKakao(idToken: String): ApiResponse<TokenResponse> = client
        .post("/api/v1/auth/kakao") {
            setBody(KakaoLoginRequest(idToken = idToken))
        }.toApiResponse()

    suspend fun refreshToken(refreshToken: String): ApiResponse<TokenResponse> = client
        .post("/api/v1/auth/refresh") {
            setBody(RefreshTokenRequest(refreshToken = refreshToken))
        }.toApiResponse()
}
