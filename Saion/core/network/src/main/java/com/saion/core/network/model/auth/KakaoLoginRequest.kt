package com.saion.core.network.model.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 카카오 소셜 로그인 요청입니다.
 *
 * 카카오 SDK 또는 로그인 플로우에서 발급받은 ID Token을 서버에 전달합니다.
 *
 * @property idToken 카카오에서 발급받은 ID Token입니다.
 */
@Serializable
data class KakaoLoginRequest(
    @SerialName("idToken")
    val idToken: String,
)
