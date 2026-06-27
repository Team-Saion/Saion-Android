package com.saion.core.network.api

import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.common.ApiResponse.Companion.toApiResponse
import com.saion.core.network.model.member.MemberInfoResponse
import com.saion.core.network.model.member.NicknameRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class MemberService(private val client: HttpClient) {
    suspend fun getMyInfo(): ApiResponse<MemberInfoResponse> = client
        .get("/api/v1/members/me")
        .toApiResponse()

    suspend fun completeTerms(): ApiResponse<Unit> = client
        .post("/api/v1/members/me/terms")
        .toApiResponse()

    suspend fun completeNickname(nickname: String): ApiResponse<Unit> = client
        .patch("/api/v1/members/me/nickname") {
            setBody(NicknameRequest(nickname = nickname))
        }.toApiResponse()

    suspend fun logout(): ApiResponse<Unit> = client
        .post("/api/v1/members/me/logout")
        .toApiResponse()
}
