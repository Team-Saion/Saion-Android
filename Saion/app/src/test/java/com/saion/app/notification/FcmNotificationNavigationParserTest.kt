package com.saion.app.notification

import com.saion.core.notification.FcmPayloadKeys
import com.saion.feature.home.api.key.HomeNavKey
import com.saion.feature.schedule.api.key.ScheduleDetailNavKey
import com.saion.feature.schedule.api.key.ScheduleNavKey
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FcmNotificationNavigationParserTest {
    @Test
    fun `써클 참여 완료 알림은 홈으로 이동한다`() {
        val actual = FcmNotificationNavigationParser.parse(
            mapOf(FcmPayloadKeys.EVENT_TYPE to "CIRCLE_JOIN_COMPLETED"),
        )

        assertEquals(HomeNavKey, actual)
    }

    @Test
    fun `일정 상세 알림은 schedule id로 상세 화면으로 이동한다`() {
        val actual = FcmNotificationNavigationParser.parse(
            mapOf(
                FcmPayloadKeys.EVENT_TYPE to "SCHEDULE_CREATED",
                FcmPayloadKeys.SCHEDULE_ID to "schedule-1",
            ),
        )

        assertEquals(ScheduleDetailNavKey(scheduleId = "schedule-1"), actual)
    }

    @Test
    fun `일정 삭제 알림은 일정 목록으로 이동한다`() {
        val actual = FcmNotificationNavigationParser.parse(
            mapOf(FcmPayloadKeys.EVENT_TYPE to "SCHEDULE_DELETED"),
        )

        assertEquals(ScheduleNavKey, actual)
    }

    @Test
    fun `schedule id가 없는 일정 알림은 일정 목록으로 fallback 한다`() {
        val actual = FcmNotificationNavigationParser.parse(
            mapOf(FcmPayloadKeys.EVENT_TYPE to "SCHEDULE_CONFIRMATION_REQUESTED"),
        )

        assertEquals(ScheduleNavKey, actual)
    }

    @Test
    fun `알 수 없는 eventType은 무시한다`() {
        val actual = FcmNotificationNavigationParser.parse(
            mapOf(FcmPayloadKeys.EVENT_TYPE to "UNKNOWN_EVENT"),
        )

        assertNull(actual)
    }
}
