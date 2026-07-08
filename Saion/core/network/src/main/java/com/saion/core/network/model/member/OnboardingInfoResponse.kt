package com.saion.core.network.model.member

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OnboardingInfoResponse(
    @SerialName("socialNickname")
    val socialNickname: String? = null,
    @SerialName("socialProfileImageUrl")
    val socialProfileImageUrl: String? = null,
    @SerialName("avatarColor")
    val avatarColor: AvatarColorResponse,
)
