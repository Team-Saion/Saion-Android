package com.saion.core.network.datasource

import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.home.CircleHomeResponse
import com.saion.core.network.model.home.CircleMemberResponse

interface HomeRemoteDataSource {
    suspend fun getHome(circleId: String): ApiResponse<CircleHomeResponse>

    suspend fun getMembers(circleId: String): ApiResponse<List<CircleMemberResponse>>
}
