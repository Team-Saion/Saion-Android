package com.saion.core.domain.usecase.invitation

import com.saion.core.domain.repository.InvitationRepository
import com.saion.core.model.invitation.InvitationType
import com.saion.core.model.invitation.IssuedInvitation
import com.saion.core.model.result.AppResult
import javax.inject.Inject

class IssueInvitationUseCase @Inject constructor(
    private val invitationRepository: InvitationRepository,
) {
    suspend operator fun invoke(
        type: InvitationType,
        targetId: String,
        inviteToName: String? = null,
        message: String? = null,
    ): AppResult<IssuedInvitation> = invitationRepository.issueInvitation(
        type = type,
        targetId = targetId,
        inviteToName = inviteToName,
        message = message,
    )
}
