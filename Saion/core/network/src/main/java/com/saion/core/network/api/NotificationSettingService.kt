package com.saion.core.network.api

import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.common.ApiResponse.Companion.toApiResponse
import com.saion.core.network.model.notification.NotificationSettingResponse
import com.saion.core.network.model.notification.UpdateNotificationSettingRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class NotificationSettingService(private val client: HttpClient) {
    suspend fun getSetting(): ApiResponse<NotificationSettingResponse> = client
        .get("/api/v1/notification-settings")
        .toApiResponse()

    suspend fun updateSetting(
        d7Enabled: Boolean,
        d1Enabled: Boolean,
        ddayEnabled: Boolean,
        familyScheduleCheckEnabled: Boolean,
    ): ApiResponse<NotificationSettingResponse> = client
        .put("/api/v1/notification-settings") {
            setBody(
                UpdateNotificationSettingRequest(
                    d7Enabled = d7Enabled,
                    d1Enabled = d1Enabled,
                    ddayEnabled = ddayEnabled,
                    familyScheduleCheckEnabled = familyScheduleCheckEnabled,
                ),
            )
        }.toApiResponse()
}
