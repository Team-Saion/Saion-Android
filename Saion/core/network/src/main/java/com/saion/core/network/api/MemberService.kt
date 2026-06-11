package com.saion.core.network.api

import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.common.ApiResponse.Companion.toApiResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.post

class MemberService(private val client: HttpClient) {
    suspend fun logout(): ApiResponse<Unit> = client
        .post("/api/v1/members/me/logout")
        .toApiResponse()
}
