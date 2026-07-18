package com.saion.core.network.datasource

import com.saion.core.network.model.circle.CircleSummaryResponse
import com.saion.core.network.model.common.ApiResponse

interface CircleRemoteDataSource {
    suspend fun listCircles(): ApiResponse<List<CircleSummaryResponse>>

    suspend fun createCircle(name: String): ApiResponse<CircleSummaryResponse>

    suspend fun transferInitiator(
        circleId: String,
        targetMemberId: String,
    ): ApiResponse<CircleSummaryResponse>

    suspend fun leave(circleId: String): ApiResponse<Unit>
}
