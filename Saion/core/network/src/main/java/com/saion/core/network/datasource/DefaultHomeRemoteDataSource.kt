package com.saion.core.network.datasource

import com.saion.core.network.api.HomeService
import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.home.CircleHomeResponse
import com.saion.core.network.model.home.CircleMemberResponse
import javax.inject.Inject

class DefaultHomeRemoteDataSource @Inject constructor(
    private val service: HomeService,
) : HomeRemoteDataSource {
    override suspend fun getHome(circleId: String): ApiResponse<CircleHomeResponse> = service.getHome(circleId = circleId)

    override suspend fun getMembers(circleId: String): ApiResponse<List<CircleMemberResponse>> = service.getMembers(circleId = circleId)
}
