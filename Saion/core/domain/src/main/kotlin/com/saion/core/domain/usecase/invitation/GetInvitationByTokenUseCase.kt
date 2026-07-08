package com.saion.core.domain.usecase.invitation

import com.saion.core.domain.repository.InvitationRepository
import com.saion.core.model.invitation.InvitationDetail
import com.saion.core.model.result.AppResult
import javax.inject.Inject

class GetInvitationByTokenUseCase @Inject constructor(
    private val invitationRepository: InvitationRepository,
) {
    suspend operator fun invoke(token: String): AppResult<InvitationDetail> =
        invitationRepository.getInvitationByToken(token = token)
}
