package com.saion.core.network.api

import com.saion.core.network.model.circle.CircleSummaryResponse
import com.saion.core.network.model.circle.CircleTransferInitiatorRequest
import com.saion.core.network.model.circle.CreateCircleRequest
import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.common.ApiResponse.Companion.toApiResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody

/**
 * Circle API 원격 호출을 구성합니다.
 */
class CircleService(private val client: HttpClient) {
    suspend fun listCircles(): ApiResponse<List<CircleSummaryResponse>> = client
        .get("/api/v1/circles")
        .toApiResponse()

    suspend fun createCircle(name: String): ApiResponse<CircleSummaryResponse> = client
        .post("/api/v1/circles") {
            setBody(CreateCircleRequest(name = name))
        }.toApiResponse()

    suspend fun transferInitiator(
        circleId: String,
        targetMemberId: String,
    ): ApiResponse<CircleSummaryResponse> = client
        .patch("/api/v1/circles/$circleId/initiator") {
            setBody(CircleTransferInitiatorRequest(targetMemberId = targetMemberId))
        }.toApiResponse()

    suspend fun leave(circleId: String): ApiResponse<Unit> = client
        .delete("/api/v1/circles/$circleId/members/me")
        .toApiResponse()
}
