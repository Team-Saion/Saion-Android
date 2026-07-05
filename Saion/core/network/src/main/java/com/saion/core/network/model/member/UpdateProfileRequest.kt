package com.saion.core.network.model.member

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 프로필 변경 요청입니다.
 *
 * @property nickname 변경할 닉네임입니다. 2자 이상 10자 이하이며 한글, 영문, 숫자만 허용합니다.
 */
@Serializable
data class UpdateProfileRequest(
    @SerialName("nickname")
    val nickname: String,
)
