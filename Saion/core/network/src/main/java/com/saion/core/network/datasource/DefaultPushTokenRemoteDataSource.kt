package com.saion.core.network.datasource

import com.saion.core.network.api.PushTokenService
import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.push.PushTokenResponse
import javax.inject.Inject

class DefaultPushTokenRemoteDataSource @Inject constructor(
    private val service: PushTokenService,
) : PushTokenRemoteDataSource {
    override suspend fun register(
        token: String,
        platform: String,
        osNotificationPermissionGranted: Boolean,
        appVersion: String?,
    ): ApiResponse<PushTokenResponse> = service.register(
        token = token,
        platform = platform,
        osNotificationPermissionGranted = osNotificationPermissionGranted,
        appVersion = appVersion,
    )

    override suspend fun deactivate(tokenId: Long): ApiResponse<Unit> = service.deactivate(tokenId = tokenId)
}
