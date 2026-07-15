package com.saion.core.network.model.notification

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationInboxItemResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("type")
    val type: String,
    @SerialName("title")
    val title: String,
    @SerialName("body")
    val body: String,
    @SerialName("occurredAt")
    val occurredAt: String,
    @SerialName("readAt")
    val readAt: String? = null,
    @SerialName("route")
    val route: NotificationRouteResponse,
)
