package com.saion.core.network.model.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 인증 토큰 재발급 요청입니다.
 *
 * @property refreshToken 로그인 또는 직전 재발급에서 받은 refresh token입니다.
 */
@Serializable
data class RefreshTokenRequest(
    @SerialName("refreshToken")
    val refreshToken: String,
)
