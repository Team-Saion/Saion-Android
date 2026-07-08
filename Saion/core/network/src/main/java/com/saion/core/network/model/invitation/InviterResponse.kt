package com.saion.core.network.model.invitation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InviterResponse(
    @SerialName("nickname")
    val nickname: String,
    @SerialName("avatarColor")
    val avatarColor: String,
)
