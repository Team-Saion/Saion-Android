package com.saion.core.network.datasource

import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.push.PushTokenResponse

interface PushTokenRemoteDataSource {
    suspend fun register(
        token: String,
        platform: String,
        osNotificationPermissionGranted: Boolean,
        appVersion: String?,
    ): ApiResponse<PushTokenResponse>

    suspend fun deactivate(tokenId: Long): ApiResponse<Unit>
}
