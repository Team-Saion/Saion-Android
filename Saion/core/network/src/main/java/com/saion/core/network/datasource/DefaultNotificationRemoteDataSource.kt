package com.saion.core.network.datasource

import com.saion.core.network.api.NotificationService
import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.notification.NotificationInboxItemResponse
import com.saion.core.network.model.notification.NotificationInboxPageResponse
import javax.inject.Inject

class DefaultNotificationRemoteDataSource @Inject constructor(
    private val service: NotificationService,
) : NotificationRemoteDataSource {
    override suspend fun getInbox(
        cursor: Long?,
        size: Int?,
    ): ApiResponse<NotificationInboxPageResponse> = service.getInbox(cursor = cursor, size = size)

    override suspend fun markRead(notificationId: Long): ApiResponse<NotificationInboxItemResponse> =
        service.markRead(notificationId = notificationId)
}
