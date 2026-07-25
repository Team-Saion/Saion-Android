package com.saion.core.network.datasource

import com.saion.core.network.api.InvitationService
import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.invitation.AcceptInvitationResponse
import com.saion.core.network.model.invitation.InvitationDetailResponse
import com.saion.core.network.model.invitation.IssuedInvitationResponse
import javax.inject.Inject

class DefaultInvitationRemoteDataSource @Inject constructor(
    private val service: InvitationService,
) : InvitationRemoteDataSource {
    override suspend fun issueInvitation(targetId: String): ApiResponse<IssuedInvitationResponse> =
        service.issueInvitation(targetId = targetId)

    override suspend fun getInvitationByToken(token: String): ApiResponse<InvitationDetailResponse> =
        service.getInvitationByToken(token = token)

    override suspend fun acceptInvitation(token: String): ApiResponse<AcceptInvitationResponse> =
        service.acceptInvitation(token = token)
}
