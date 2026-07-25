package com.saion.core.network.api

import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.common.ApiResponse.Companion.toApiResponse
import com.saion.core.network.model.invitation.AcceptInvitationResponse
import com.saion.core.network.model.invitation.InvitationDetailResponse
import com.saion.core.network.model.invitation.IssueInvitationRequest
import com.saion.core.network.model.invitation.IssuedInvitationResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

/**
 * Invitation API 원격 호출을 구성합니다.
 */
class InvitationService(private val client: HttpClient) {
    suspend fun issueInvitation(targetId: String): ApiResponse<IssuedInvitationResponse> = client
        .post("/api/v1/invitations") {
            setBody(
                IssueInvitationRequest(
                    targetId = targetId,
                ),
            )
        }.toApiResponse()

    suspend fun getInvitationByToken(token: String): ApiResponse<InvitationDetailResponse> = client
        .get("/api/v1/invitations/$token")
        .toApiResponse()

    suspend fun acceptInvitation(token: String): ApiResponse<AcceptInvitationResponse> = client
        .post("/api/v1/invitations/$token")
        .toApiResponse()
}
