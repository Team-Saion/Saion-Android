package com.saion.core.network.api

import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.common.ApiResponse.Companion.toApiResponse
import com.saion.core.network.model.push.PushTokenResponse
import com.saion.core.network.model.push.RegisterPushTokenRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class PushTokenService(private val client: HttpClient) {
    suspend fun register(
        token: String,
        platform: String,
        osNotificationPermissionGranted: Boolean,
        appVersion: String?,
    ): ApiResponse<PushTokenResponse> = client
        .post("/api/v1/push-tokens") {
            setBody(
                RegisterPushTokenRequest(
                    token = token,
                    platform = platform,
                    osNotificationPermissionGranted = osNotificationPermissionGranted,
                    appVersion = appVersion,
                ),
            )
        }.toApiResponse()

    suspend fun deactivate(tokenId: Long): ApiResponse<Unit> = client
        .delete("/api/v1/push-tokens/$tokenId")
        .toApiResponse()
}
