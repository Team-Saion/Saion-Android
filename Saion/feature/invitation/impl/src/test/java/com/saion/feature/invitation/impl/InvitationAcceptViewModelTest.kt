package com.saion.feature.invitation.impl

import com.saion.core.domain.repository.CurrentCircleRepository
import com.saion.core.domain.repository.InvitationRepository
import com.saion.core.domain.usecase.circle.SelectCurrentCircleUseCase
import com.saion.core.domain.usecase.invitation.AcceptInvitationUseCase
import com.saion.core.domain.usecase.invitation.GetInvitationByTokenUseCase
import com.saion.core.model.invitation.AcceptedInvitation
import com.saion.core.model.invitation.InvitationDetail
import com.saion.core.model.invitation.InvitationIssuer
import com.saion.core.model.invitation.InvitationType
import com.saion.core.model.invitation.IssuedInvitation
import com.saion.core.model.result.AppResult
import com.saion.feature.invitation.impl.viewmodel.InvitationAcceptEffect
import com.saion.feature.invitation.impl.viewmodel.InvitationAcceptIntent
import com.saion.feature.invitation.impl.viewmodel.InvitationAcceptViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InvitationAcceptViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `초대 수락 성공 시 현재 써클을 갱신하고 닫기 effect를 보낸다`() = runTest {
        val currentCircleRepository = FakeCurrentCircleRepository()
        val invitationRepository = FakeInvitationRepository()
        val viewModel = InvitationAcceptViewModel(
            getInvitationByTokenUseCase = GetInvitationByTokenUseCase(invitationRepository),
            acceptInvitationUseCase = AcceptInvitationUseCase(invitationRepository),
            selectCurrentCircleUseCase = SelectCurrentCircleUseCase(currentCircleRepository),
        )

        viewModel.bind("invite-token")
        advanceUntilIdle()
        val effectDeferred = async { viewModel.uiEffect.first() }

        viewModel.dispatch(InvitationAcceptIntent.AcceptClicked)
        advanceUntilIdle()

        assertEquals("circle-1", currentCircleRepository.selectedCircleId)
        assertEquals(InvitationAcceptEffect.Close, effectDeferred.await())
    }
}

private class FakeInvitationRepository : InvitationRepository {
    override suspend fun issueInvitation(
        type: InvitationType,
        targetId: String,
        inviteToName: String?,
        message: String?,
    ): AppResult<IssuedInvitation> = throw UnsupportedOperationException("Not required for this test")

    override suspend fun getInvitationByToken(token: String): AppResult<InvitationDetail> = AppResult.Success(
        InvitationDetail(
            invitationId = "invite-1",
            circleName = "비니네",
            inviter = InvitationIssuer(
                nickname = "수빈",
                avatarColor = "#FFFFFF",
            ),
            expiresAt = "2026-07-30T00:00:00",
        ),
    )

    override suspend fun acceptInvitation(token: String): AppResult<AcceptedInvitation> =
        AppResult.Success(AcceptedInvitation(circleId = "circle-1"))
}

private class FakeCurrentCircleRepository : CurrentCircleRepository {
    private val flow = MutableStateFlow<String?>(null)

    val selectedCircleId: String?
        get() = flow.value

    override fun observeCurrentCircleId(): Flow<String?> = flow

    override suspend fun getCurrentCircleId(): String? = flow.value

    override suspend fun selectCircle(circleId: String): AppResult<Unit> {
        flow.value = circleId
        return AppResult.Success(Unit)
    }

    override suspend fun syncCurrentCircle(): AppResult<String?> = AppResult.Success(flow.value)

    override suspend fun clearCurrentCircle() {
        flow.value = null
    }
}
