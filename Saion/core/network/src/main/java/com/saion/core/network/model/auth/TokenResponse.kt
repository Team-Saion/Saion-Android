package com.saion.core.network.model.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 인증 토큰 응답입니다.
 *
 * 카카오 로그인 응답에서는 신규 가입 여부가 함께 내려올 수 있습니다.
 *
 * @property accessToken 서비스 API 인증에 사용하는 access token입니다.
 * @property refreshToken access token 재발급에 사용하는 refresh token입니다.
 * @property isNewMember 카카오 로그인 시 최초 가입 계정이면 `true`입니다.
 */
@Serializable
data class TokenResponse(
    @SerialName("accessToken")
    val accessToken: String,
    @SerialName("refreshToken")
    val refreshToken: String,
    @SerialName("isNewMember")
    val isNewMember: Boolean? = null,
)
