package com.saion.core.domain.usecase.invitation

import com.saion.core.domain.repository.InvitationRepository
import com.saion.core.model.invitation.IssuedInvitation
import com.saion.core.model.result.AppResult
import javax.inject.Inject

/**
 * 써클 초대장을 발급합니다.
 *
 * 성공하면 공유에 사용할 토큰과 만료 시각을 반환합니다.
 */
class IssueInvitationUseCase @Inject constructor(private val invitationRepository: InvitationRepository) {
    suspend operator fun invoke(targetId: String): AppResult<IssuedInvitation> =
        invitationRepository.issueInvitation(targetId = targetId)
}
