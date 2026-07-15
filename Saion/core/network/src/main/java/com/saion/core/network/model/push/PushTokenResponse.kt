package com.saion.core.network.model.push

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PushTokenResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("platform")
    val platform: String,
    @SerialName("osNotificationPermissionGranted")
    val osNotificationPermissionGranted: Boolean,
    @SerialName("appVersion")
    val appVersion: String? = null,
    @SerialName("active")
    val active: Boolean,
)
