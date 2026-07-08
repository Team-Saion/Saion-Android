package com.saion.core.domain.usecase.invitation

import com.saion.core.domain.repository.InvitationRepository
import com.saion.core.model.invitation.AcceptedInvitation
import com.saion.core.model.invitation.InvitationDetail
import com.saion.core.model.invitation.InvitationIssuer
import com.saion.core.model.invitation.InvitationType
import com.saion.core.model.invitation.IssuedInvitation
import com.saion.core.model.result.AppResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class InvitationUseCasesTest {
    @Test
    fun `초대장 발급은 모든 인자를 저장소에 그대로 위임한다`() = runBlocking {
        val expected = AppResult.Success(
            IssuedInvitation(
                invitationId = "invite-1",
                token = "token-1",
                expiresAt = "2026-07-08T00:00:00",
            ),
        )
        val repository = FakeInvitationRepository(issueResult = expected)

        val actual = IssueInvitationUseCase(repository).invoke(
            type = InvitationType.CIRCLE,
            targetId = "circle-1",
            inviteToName = "김철수",
            message = "함께 해요",
        )

        assertEquals(
            InvitationUseCaseOutcome(
                result = expected,
                call = InvitationRepositoryCall.IssueInvitation(
                    type = InvitationType.CIRCLE,
                    targetId = "circle-1",
                    inviteToName = "김철수",
                    message = "함께 해요",
                ),
            ),
            InvitationUseCaseOutcome(
                result = actual,
                call = repository.lastCall,
            ),
        )
    }

    @Test
    fun `토큰으로 초대장 조회는 토큰을 그대로 저장소에 전달한다`() = runBlocking {
        val expected = AppResult.Success(
            InvitationDetail(
                invitationId = "invite-1",
                circleName = "유니콘 스터디",
                inviter = InvitationIssuer(
                    nickname = "초대자",
                    avatarColor = "#FFFFFF",
                ),
                expiresAt = "2026-07-08T00:00:00",
            ),
        )
        val repository = FakeInvitationRepository(detailResult = expected)

        val actual = GetInvitationByTokenUseCase(repository).invoke(token = "token-1")

        assertEquals(
            InvitationUseCaseOutcome(
                result = expected,
                call = InvitationRepositoryCall.GetInvitationByToken(token = "token-1"),
            ),
            InvitationUseCaseOutcome(
                result = actual,
                call = repository.lastCall,
            ),
        )
    }

    @Test
    fun `초대 수락은 토큰을 그대로 저장소에 전달한다`() = runBlocking {
        val expected = AppResult.Success(
            AcceptedInvitation(circleId = "circle-1"),
        )
        val repository = FakeInvitationRepository(acceptResult = expected)

        val actual = AcceptInvitationUseCase(repository).invoke(token = "token-1")

        assertEquals(
            InvitationUseCaseOutcome(
                result = expected,
                call = InvitationRepositoryCall.AcceptInvitation(token = "token-1"),
            ),
            InvitationUseCaseOutcome(
                result = actual,
                call = repository.lastCall,
            ),
        )
    }
}

private data class InvitationUseCaseOutcome<T>(
    val result: AppResult<T>,
    val call: InvitationRepositoryCall?,
)

private sealed interface InvitationRepositoryCall {
    data class IssueInvitation(
        val type: InvitationType,
        val targetId: String,
        val inviteToName: String?,
        val message: String?,
    ) : InvitationRepositoryCall

    data class GetInvitationByToken(val token: String) : InvitationRepositoryCall

    data class AcceptInvitation(val token: String) : InvitationRepositoryCall
}

private class FakeInvitationRepository(
    private val issueResult: AppResult<IssuedInvitation> = AppResult.Success(
        IssuedInvitation(invitationId = "default", token = "token", expiresAt = "2026-07-08T00:00:00"),
    ),
    private val detailResult: AppResult<InvitationDetail> = AppResult.Success(
        InvitationDetail(
            invitationId = "default",
            circleName = "default",
            inviter = InvitationIssuer(nickname = "default", avatarColor = "#000000"),
            expiresAt = "2026-07-08T00:00:00",
        ),
    ),
    private val acceptResult: AppResult<AcceptedInvitation> = AppResult.Success(
        AcceptedInvitation(circleId = "default"),
    ),
) : InvitationRepository {
    var lastCall: InvitationRepositoryCall? = null

    override suspend fun issueInvitation(
        type: InvitationType,
        targetId: String,
        inviteToName: String?,
        message: String?,
    ): AppResult<IssuedInvitation> {
        lastCall = InvitationRepositoryCall.IssueInvitation(
            type = type,
            targetId = targetId,
            inviteToName = inviteToName,
            message = message,
        )
        return issueResult
    }

    override suspend fun getInvitationByToken(token: String): AppResult<InvitationDetail> {
        lastCall = InvitationRepositoryCall.GetInvitationByToken(token = token)
        return detailResult
    }

    override suspend fun acceptInvitation(token: String): AppResult<AcceptedInvitation> {
        lastCall = InvitationRepositoryCall.AcceptInvitation(token = token)
        return acceptResult
    }
}
