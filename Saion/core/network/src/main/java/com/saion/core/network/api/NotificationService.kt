package com.saion.core.network.api

import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.common.ApiResponse.Companion.toApiResponse
import com.saion.core.network.model.notification.NotificationInboxItemResponse
import com.saion.core.network.model.notification.NotificationInboxPageResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch

class NotificationService(private val client: HttpClient) {
    suspend fun getInbox(
        cursor: Long?,
        size: Int?,
    ): ApiResponse<NotificationInboxPageResponse> = client
        .get("/api/v1/notifications") {
            parameter("cursor", cursor)
            parameter("size", size)
        }.toApiResponse()

    suspend fun markRead(notificationId: Long): ApiResponse<NotificationInboxItemResponse> = client
        .patch("/api/v1/notifications/$notificationId/read")
        .toApiResponse()
}
