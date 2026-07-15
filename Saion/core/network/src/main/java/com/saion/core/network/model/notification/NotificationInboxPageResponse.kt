package com.saion.core.network.model.notification

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationInboxPageResponse(
    @SerialName("items")
    val items: List<NotificationInboxItemResponse>,
    @SerialName("nextCursor")
    val nextCursor: Long? = null,
)
