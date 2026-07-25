package com.saion.feature.invitation.impl

import com.saion.core.domain.repository.CircleRepository
import com.saion.core.domain.repository.CurrentCircleRepository
import com.saion.core.domain.repository.InvitationRepository
import com.saion.core.domain.usecase.circle.SelectCurrentCircleUseCase
import com.saion.core.domain.usecase.invitation.AcceptInvitationUseCase
import com.saion.core.domain.usecase.invitation.GetInvitationByTokenUseCase
import com.saion.core.model.circle.CircleSummary
import com.saion.core.model.invitation.AcceptedInvitation
import com.saion.core.model.invitation.InvitationDetail
import com.saion.core.model.invitation.InvitationIssuer
import com.saion.core.model.invitation.IssuedInvitation
import com.saion.core.model.result.AppError
import com.saion.core.model.result.BusinessErrorType
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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
            selectCurrentCircleUseCase = SelectCurrentCircleUseCase(
                currentCircleRepository = currentCircleRepository,
                circleRepository = FakeResolvedCircleRepository(),
            ),
        )

        viewModel.bind("invite-token")
        advanceUntilIdle()
        val effectDeferred = async { viewModel.uiEffect.first() }

        viewModel.dispatch(InvitationAcceptIntent.AcceptClicked)
        advanceUntilIdle()

        assertEquals("circle-1", currentCircleRepository.selectedCircleId)
        assertEquals(InvitationAcceptEffect.Close, effectDeferred.await())
    }

    @Test
    fun `만료된 초대장 조회 실패 시 만료 상태를 노출한다`() = runTest {
        val viewModel = InvitationAcceptViewModel(
            getInvitationByTokenUseCase = GetInvitationByTokenUseCase(
                FakeInvitationRepository(
                    getInvitationResult = AppResult.Failure(
                        AppError.Business(
                            businessType = BusinessErrorType.NOT_FOUND,
                            rawCode = "I410_1",
                            message = "만료된 초대장이에요. 초대자에게 다시 요청해주세요.",
                        ),
                    ),
                ),
            ),
            acceptInvitationUseCase = AcceptInvitationUseCase(FakeInvitationRepository()),
            selectCurrentCircleUseCase = SelectCurrentCircleUseCase(
                currentCircleRepository = FakeCurrentCircleRepository(),
                circleRepository = FakeResolvedCircleRepository(),
            ),
        )

        viewModel.bind("expired-token")
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isExpired)
        assertFalse(viewModel.uiState.value.isLoadFailed)
        assertEquals(null, viewModel.uiState.value.detail)
    }

    @Test
    fun `만료가 아닌 초대장 조회 실패 시 일반 실패 상태를 유지한다`() = runTest {
        val viewModel = InvitationAcceptViewModel(
            getInvitationByTokenUseCase = GetInvitationByTokenUseCase(
                FakeInvitationRepository(
                    getInvitationResult = AppResult.Failure(
                        AppError.Business(
                            businessType = BusinessErrorType.NOT_FOUND,
                            rawCode = "I404_1",
                            message = "초대장을 찾을 수 없습니다.",
                        ),
                    ),
                ),
            ),
            acceptInvitationUseCase = AcceptInvitationUseCase(FakeInvitationRepository()),
            selectCurrentCircleUseCase = SelectCurrentCircleUseCase(
                currentCircleRepository = FakeCurrentCircleRepository(),
                circleRepository = FakeResolvedCircleRepository(),
            ),
        )

        viewModel.bind("missing-token")
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isExpired)
        assertTrue(viewModel.uiState.value.isLoadFailed)
        assertEquals(null, viewModel.uiState.value.detail)
    }
}

private class FakeInvitationRepository(
    private val getInvitationResult: AppResult<InvitationDetail> = AppResult.Success(
        InvitationDetail(
            invitationId = "invite-1",
            circleName = "비니네",
            inviter = InvitationIssuer(
                nickname = "수빈",
                avatarColor = "#FFFFFF",
            ),
            expiresAt = "2026-07-30T00:00:00",
        ),
    ),
    private val acceptInvitationResult: AppResult<AcceptedInvitation> = AppResult.Success(
        AcceptedInvitation(circleId = "circle-1"),
    ),
) : InvitationRepository {
    override suspend fun issueInvitation(targetId: String): AppResult<IssuedInvitation> =
        throw UnsupportedOperationException("Not required for this test")

    override suspend fun getInvitationByToken(token: String): AppResult<InvitationDetail> = getInvitationResult

    override suspend fun acceptInvitation(token: String): AppResult<AcceptedInvitation> = acceptInvitationResult
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

    override suspend fun clearCurrentCircle() {
        flow.value = null
    }
}

private class FakeResolvedCircleRepository(
) : CircleRepository {
    private val circles = listOf(
        CircleSummary(circleId = "circle-1", name = "비니네", ownerId = "owner-1"),
    )

    override fun observeCircles(): Flow<List<CircleSummary>> = flowOf(emptyList())

    override suspend fun listCircles(): AppResult<List<CircleSummary>> = AppResult.Success(circles)

    override suspend fun refreshCircles(): AppResult<List<CircleSummary>> = AppResult.Success(circles)

    override suspend fun createCircle(name: String): AppResult<CircleSummary> = error("Not used")

    override suspend fun transferInitiator(
        circleId: String,
        targetMemberId: String,
    ): AppResult<CircleSummary> = error("Not used")

    override suspend fun leave(circleId: String): AppResult<Unit> = error("Not used")
}
