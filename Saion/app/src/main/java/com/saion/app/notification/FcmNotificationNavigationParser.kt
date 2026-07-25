package com.saion.app.notification

import com.saion.core.navigation.key.AppNavKey
import com.saion.core.notification.FcmPayloadKeys
import com.saion.feature.home.api.key.HomeNavKey
import com.saion.feature.schedule.api.key.ScheduleDetailNavKey
import com.saion.feature.schedule.api.key.ScheduleNavKey

internal object FcmNotificationNavigationParser {
    fun parse(payload: Map<String, String>): AppNavKey? {
        val eventType = payload[FcmPayloadKeys.EVENT_TYPE]?.takeIf(String::isNotBlank) ?: return null
        val scheduleId = payload[FcmPayloadKeys.SCHEDULE_ID]?.takeIf(String::isNotBlank)

        return when (eventType) {
            "CIRCLE_JOIN_COMPLETED" -> HomeNavKey
            "SCHEDULE_DELETED" -> ScheduleNavKey
            "SCHEDULE_CREATED",
            "SCHEDULE_REMINDER_D7",
            "SCHEDULE_REMINDER_D1",
            "SCHEDULE_REMINDER_DDAY_ALL_DAY",
            "SCHEDULE_REMINDER_DDAY_TIMED",
            "SCHEDULE_CONFIRMED_BY_FAMILY",
            "SCHEDULE_CONFIRMATION_REQUESTED",
            "SCHEDULE_FAMILY_NOTIFICATION_REQUESTED",
            -> scheduleId?.let(::ScheduleDetailNavKey) ?: ScheduleNavKey

            else -> null
        }
    }
}
