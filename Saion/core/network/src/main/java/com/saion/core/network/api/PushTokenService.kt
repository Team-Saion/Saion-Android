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
        installationId: String,
        token: String,
        platform: String,
    ): ApiResponse<PushTokenResponse> = client
        .post("/api/v1/push-tokens") {
            setBody(
                RegisterPushTokenRequest(
                    installationId = installationId,
                    token = token,
                    platform = platform,
                ),
            )
        }.toApiResponse()

    suspend fun deactivate(tokenId: Long): ApiResponse<Unit> = client
        .delete("/api/v1/push-tokens/$tokenId")
        .toApiResponse()
}
