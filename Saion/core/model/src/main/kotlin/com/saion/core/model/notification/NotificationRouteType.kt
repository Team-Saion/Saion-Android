package com.saion.core.model.notification

enum class NotificationRouteType(val value: String) {
    CIRCLE_HOME("CIRCLE_HOME"),
    SCHEDULE_DETAIL("SCHEDULE_DETAIL"),
    SCHEDULE_LIST("SCHEDULE_LIST"),
    HOME("HOME"),
    ;

    companion object {
        fun from(value: String): NotificationRouteType? = entries.firstOrNull { it.value == value }
    }
}
