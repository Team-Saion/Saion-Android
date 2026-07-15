package com.saion.core.network.model.notification

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationRouteResponse(
    @SerialName("type")
    val type: String,
    @SerialName("circleId")
    val circleId: String? = null,
    @SerialName("scheduleId")
    val scheduleId: String? = null,
)
