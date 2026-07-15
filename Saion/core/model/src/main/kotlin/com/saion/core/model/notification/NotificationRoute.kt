package com.saion.core.model.notification

data class NotificationRoute(
    val type: NotificationRouteType,
    val circleId: String?,
    val scheduleId: String?,
)
