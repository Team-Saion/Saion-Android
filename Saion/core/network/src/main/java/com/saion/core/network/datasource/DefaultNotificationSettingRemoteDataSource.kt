package com.saion.core.network.datasource

import com.saion.core.network.api.NotificationSettingService
import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.notification.NotificationSettingResponse
import javax.inject.Inject

class DefaultNotificationSettingRemoteDataSource @Inject constructor(
    private val service: NotificationSettingService,
) : NotificationSettingRemoteDataSource {
    override suspend fun getSetting(): ApiResponse<NotificationSettingResponse> = service.getSetting()

    override suspend fun updateSetting(
        d7Enabled: Boolean,
        d1Enabled: Boolean,
        ddayEnabled: Boolean,
        familyScheduleCheckEnabled: Boolean,
    ): ApiResponse<NotificationSettingResponse> = service.updateSetting(
        d7Enabled = d7Enabled,
        d1Enabled = d1Enabled,
        ddayEnabled = ddayEnabled,
        familyScheduleCheckEnabled = familyScheduleCheckEnabled,
    )
}
