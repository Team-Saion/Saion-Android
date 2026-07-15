package com.saion.core.model.notification

data class NotificationInboxItem(
    val id: Long,
    val type: NotificationType,
    val title: String,
    val body: String,
    val occurredAt: String,
    val readAt: String?,
    val route: NotificationRoute,
)
