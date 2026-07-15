package com.saion.core.network.datasource

import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.notification.NotificationSettingResponse

interface NotificationSettingRemoteDataSource {
    suspend fun getSetting(): ApiResponse<NotificationSettingResponse>

    suspend fun updateSetting(
        d7Enabled: Boolean,
        d1Enabled: Boolean,
        ddayEnabled: Boolean,
        familyScheduleCheckEnabled: Boolean,
    ): ApiResponse<NotificationSettingResponse>
}
