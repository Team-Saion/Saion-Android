package com.saion.core.network.api

import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.common.ApiResponse.Companion.toApiResponse
import com.saion.core.network.model.home.CircleHomeResponse
import com.saion.core.network.model.home.CircleMemberResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get

/**
 * Home API 원격 호출을 구성합니다.
 */
class HomeService(private val client: HttpClient) {
    suspend fun getHome(circleId: String): ApiResponse<CircleHomeResponse> = client
        .get("/api/v1/homes/$circleId")
        .toApiResponse()

    suspend fun getMembers(circleId: String): ApiResponse<List<CircleMemberResponse>> = client
        .get("/api/v1/homes/$circleId/members")
        .toApiResponse()
}
