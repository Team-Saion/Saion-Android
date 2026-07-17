package com.saion.core.network.model.member

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 회원 탈퇴 요청입니다.
 *
 * @property reason 탈퇴 사유입니다. 빈 문자열도 허용됩니다.
 */
@Serializable
data class WithdrawRequest(
    @SerialName("reason")
    val reason: String,
)
