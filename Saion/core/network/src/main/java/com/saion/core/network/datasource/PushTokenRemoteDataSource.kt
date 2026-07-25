package com.saion.core.network.datasource

import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.push.PushTokenResponse

interface PushTokenRemoteDataSource {
    suspend fun register(
        installationId: String,
        token: String,
        platform: String,
    ): ApiResponse<PushTokenResponse>

    suspend fun deactivate(tokenId: Long): ApiResponse<Unit>
}
