package com.saion.core.network.model.home

import com.saion.core.network.model.member.AvatarColorResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CircleMemberResponse(
    @SerialName("memberId")
    val memberId: String,
    @SerialName("nickname")
    val nickname: String,
    @SerialName("avatarColor")
    val avatarColor: AvatarColorResponse,
    @SerialName("me")
    val isMe: Boolean,
    @SerialName("role")
    val role: String,
)
