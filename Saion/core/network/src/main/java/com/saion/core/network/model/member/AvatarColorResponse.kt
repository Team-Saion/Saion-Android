package com.saion.core.network.model.member

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AvatarColorResponse(
    @SerialName("code")
    val code: String,
    @SerialName("hex")
    val hex: String,
)
