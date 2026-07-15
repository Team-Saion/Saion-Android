package com.saion.core.network.model.push

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterPushTokenRequest(
    @SerialName("token")
    val token: String,
    @SerialName("platform")
    val platform: String,
    @SerialName("osNotificationPermissionGranted")
    val osNotificationPermissionGranted: Boolean,
    @SerialName("appVersion")
    val appVersion: String? = null,
)
