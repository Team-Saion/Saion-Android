package com.saion.core.network.model.push

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PushTokenResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("platform")
    val platform: String,
    @SerialName("active")
    val active: Boolean,
)
