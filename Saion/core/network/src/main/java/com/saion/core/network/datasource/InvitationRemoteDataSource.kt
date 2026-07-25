package com.saion.core.network.datasource

import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.invitation.AcceptInvitationResponse
import com.saion.core.network.model.invitation.InvitationDetailResponse
import com.saion.core.network.model.invitation.IssuedInvitationResponse

interface InvitationRemoteDataSource {
    suspend fun issueInvitation(targetId: String): ApiResponse<IssuedInvitationResponse>

    suspend fun getInvitationByToken(token: String): ApiResponse<InvitationDetailResponse>

    suspend fun acceptInvitation(token: String): ApiResponse<AcceptInvitationResponse>
}
