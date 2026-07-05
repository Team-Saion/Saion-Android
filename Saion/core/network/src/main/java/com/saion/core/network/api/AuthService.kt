package com.saion.core.network.api

import com.saion.core.network.model.auth.KakaoLoginRequest
import com.saion.core.network.model.auth.RefreshTokenRequest
import com.saion.core.network.model.auth.TokenResponse
import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.common.ApiResponse.Companion.toApiResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody

/**
 * Auth API 원격 호출을 구성합니다.
 */
class AuthService(private val client: HttpClient) {
    /**
     * 카카오 ID Token을 검증하고 서비스용 access token과 refresh token을 발급받습니다.
     *
     * 신규 가입 여부는 `isNewMember`로 내려오지만, 온보딩 분기는 access token의 `role` 클레임으로 판단해야 합니다.
     */
    suspend fun loginWithKakao(idToken: String): ApiResponse<TokenResponse> = client
        .post("/api/v1/auth/kakao") {
            setBody(KakaoLoginRequest(idToken = idToken))
        }.toApiResponse()

    /**
     * 유효한 refresh token으로 access token과 refresh token을 재발급합니다.
     *
     * 재발급이 완료되면 기존 refresh token은 즉시 무효화됩니다.
     */
    suspend fun refreshToken(refreshToken: String): ApiResponse<TokenResponse> = client
        .post("/api/v1/auth/refresh") {
            setBody(RefreshTokenRequest(refreshToken = refreshToken))
        }.toApiResponse()
}
