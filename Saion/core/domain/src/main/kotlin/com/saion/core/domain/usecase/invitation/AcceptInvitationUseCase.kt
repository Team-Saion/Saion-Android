package com.saion.core.domain.usecase.invitation

import com.saion.core.domain.repository.InvitationRepository
import com.saion.core.model.invitation.AcceptedInvitation
import com.saion.core.model.result.AppResult
import javax.inject.Inject

/**
 * 초대 링크를 수락합니다.
 *
 * 성공하면 참여한 써클 식별자를 반환합니다.
 */
class AcceptInvitationUseCase @Inject constructor(
    private val invitationRepository: InvitationRepository,
) {
    suspend operator fun invoke(token: String): AppResult<AcceptedInvitation> =
        invitationRepository.acceptInvitation(token = token)
}
