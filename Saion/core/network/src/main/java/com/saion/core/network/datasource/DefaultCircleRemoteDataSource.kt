package com.saion.core.network.datasource

import com.saion.core.network.api.CircleService
import com.saion.core.network.model.circle.CircleSummaryResponse
import com.saion.core.network.model.common.ApiResponse
import javax.inject.Inject

class DefaultCircleRemoteDataSource @Inject constructor(
    private val service: CircleService,
) : CircleRemoteDataSource {
    override suspend fun listCircles(): ApiResponse<List<CircleSummaryResponse>> = service.listCircles()

    override suspend fun createCircle(name: String): ApiResponse<CircleSummaryResponse> = service.createCircle(name = name)

    override suspend fun transferInitiator(
        circleId: String,
        targetMemberId: String,
    ): ApiResponse<CircleSummaryResponse> = service.transferInitiator(
        circleId = circleId,
        targetMemberId = targetMemberId,
    )

    override suspend fun leave(circleId: String): ApiResponse<Unit> = service.leave(circleId = circleId)
}
