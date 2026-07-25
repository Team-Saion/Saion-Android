package com.saion.core.model.notification

import org.junit.Assert.assertEquals
import org.junit.Test

class NotificationTypeTest {
    @Test
    fun `최신 알림 타입을 문자열에서 파싱한다`() {
        val actual = NotificationType.from("SCHEDULE_FAMILY_NOTIFICATION_REQUESTED")

        assertEquals(NotificationType.SCHEDULE_FAMILY_NOTIFICATION_REQUESTED, actual)
    }
}
