package com.saion.core.network.model.push

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterPushTokenRequest(
    @SerialName("installationId")
    val installationId: String,
    @SerialName("token")
    val token: String,
    @SerialName("platform")
    val platform: String,
)
