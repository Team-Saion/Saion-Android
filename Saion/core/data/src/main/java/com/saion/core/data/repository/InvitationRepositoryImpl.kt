package com.saion.core.data.repository

import com.saion.core.data.util.safeRequest
import com.saion.core.domain.repository.InvitationRepository
import com.saion.core.model.invitation.AcceptedInvitation
import com.saion.core.model.invitation.InvitationDetail
import com.saion.core.model.invitation.InvitationIssuer
import com.saion.core.model.invitation.IssuedInvitation
import com.saion.core.model.result.AppResult
import com.saion.core.network.datasource.InvitationRemoteDataSource
import com.saion.core.network.model.invitation.AcceptInvitationResponse
import com.saion.core.network.model.invitation.InvitationDetailResponse
import com.saion.core.network.model.invitation.IssuedInvitationResponse
import com.saion.core.network.model.invitation.InviterResponse
import javax.inject.Inject

internal class InvitationRepositoryImpl @Inject constructor(
    private val invitationRemoteDataSource: InvitationRemoteDataSource,
) : InvitationRepository {
    override suspend fun issueInvitation(targetId: String): AppResult<IssuedInvitation> = safeRequest(
        request = {
            invitationRemoteDataSource.issueInvitation(targetId = targetId)
        },
    ) { response ->
        AppResult.Success(response.toDomain())
    }

    override suspend fun getInvitationByToken(token: String): AppResult<InvitationDetail> = safeRequest(
        request = { invitationRemoteDataSource.getInvitationByToken(token = token) },
    ) { response ->
        AppResult.Success(response.toDomain())
    }

    override suspend fun acceptInvitation(token: String): AppResult<AcceptedInvitation> = safeRequest(
        request = { invitationRemoteDataSource.acceptInvitation(token = token) },
    ) { response ->
        AppResult.Success(response.toDomain())
    }
}

private fun IssuedInvitationResponse.toDomain(): IssuedInvitation = IssuedInvitation(
    invitationId = invitationId,
    token = token,
    expiresAt = expiresAt,
)

private fun InvitationDetailResponse.toDomain(): InvitationDetail = InvitationDetail(
    invitationId = invitationId,
    circleName = circleName,
    inviter = inviter.toDomain(),
    expiresAt = expiresAt,
)

private fun InviterResponse.toDomain(): InvitationIssuer = InvitationIssuer(
    nickname = nickname,
    avatarColor = avatarColor,
)

private fun AcceptInvitationResponse.toDomain(): AcceptedInvitation = AcceptedInvitation(
    circleId = circleId,
)
