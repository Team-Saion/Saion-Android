package com.saion.core.domain.repository

import com.saion.core.model.notification.NotificationInboxItem
import com.saion.core.model.notification.NotificationInboxPage
import com.saion.core.model.notification.NotificationSetting
import com.saion.core.model.result.AppResult

interface NotificationRepository {
    suspend fun getInbox(
        cursor: Long?,
        size: Int?,
    ): AppResult<NotificationInboxPage>

    suspend fun markRead(notificationId: Long): AppResult<NotificationInboxItem>

    suspend fun getSetting(): AppResult<NotificationSetting>

    suspend fun updateSetting(setting: NotificationSetting): AppResult<NotificationSetting>
}
