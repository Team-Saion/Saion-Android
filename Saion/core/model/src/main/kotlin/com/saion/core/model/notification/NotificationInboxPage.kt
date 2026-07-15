package com.saion.core.model.notification

data class NotificationInboxPage(
    val items: List<NotificationInboxItem>,
    val nextCursor: Long?,
)
