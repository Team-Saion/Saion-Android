package com.saion.core.network.datasource

import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.notification.NotificationInboxItemResponse
import com.saion.core.network.model.notification.NotificationInboxPageResponse

interface NotificationRemoteDataSource {
    suspend fun getInbox(
        cursor: Long?,
        size: Int?,
    ): ApiResponse<NotificationInboxPageResponse>

    suspend fun markRead(notificationId: Long): ApiResponse<NotificationInboxItemResponse>
}
