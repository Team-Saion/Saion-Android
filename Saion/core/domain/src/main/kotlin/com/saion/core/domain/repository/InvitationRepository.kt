package com.saion.core.domain.repository

import com.saion.core.model.invitation.AcceptedInvitation
import com.saion.core.model.invitation.InvitationDetail
import com.saion.core.model.invitation.IssuedInvitation
import com.saion.core.model.result.AppResult

interface InvitationRepository {
    suspend fun issueInvitation(targetId: String): AppResult<IssuedInvitation>

    suspend fun getInvitationByToken(token: String): AppResult<InvitationDetail>

    suspend fun acceptInvitation(token: String): AppResult<AcceptedInvitation>
}
