package com.saion.feature.home.impl

import com.saion.core.domain.repository.CircleRepository
import com.saion.core.domain.repository.HomeRepository
import com.saion.core.domain.repository.InvitationRepository
import com.saion.core.domain.usecase.circle.ListCirclesUseCase
import com.saion.core.domain.usecase.home.GetHomeUseCase
import com.saion.core.domain.usecase.invitation.IssueInvitationUseCase
import com.saion.core.model.circle.CircleSummary
import com.saion.core.model.invitation.AcceptedInvitation
import com.saion.core.model.invitation.InvitationDetail
import com.saion.core.model.invitation.InvitationIssuer
import com.saion.core.model.invitation.InvitationType
import com.saion.core.model.invitation.IssuedInvitation
import com.saion.core.model.home.CircleMember
import com.saion.core.model.home.HomeOverview
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import com.saion.core.model.schedule.ScheduleStatus
import com.saion.core.model.schedule.ScheduleSummary
import com.saion.core.share.InvitationShareClient
import com.saion.core.share.InvitationShareResult
import com.saion.core.ui.event.CircleCreatedEventBus
import com.saion.feature.home.impl.viewmodel.HomeEffect
import com.saion.feature.home.impl.viewmodel.HomeIntent
import com.saion.feature.home.impl.viewmodel.HomeSnackbarMessage
import com.saion.feature.home.impl.viewmodel.HomeState
import com.saion.feature.home.impl.viewmodel.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
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
    fun `첫 번째 서클 id로 홈 조회를 수행한다`() = runTest {
        val circleRepository = FakeCircleRepository(
            result = AppResult.Success(
                listOf(
                    CircleSummary(circleId = "circle-1", name = "비니네", ownerId = "owner-1"),
                    CircleSummary(circleId = "circle-2", name = "다른 서클", ownerId = "owner-2"),
                ),
            ),
        )
        val homeRepository = FakeHomeRepository(
            result = AppResult.Success(defaultOverview()),
        )

        HomeViewModel(
            listCirclesUseCase = ListCirclesUseCase(circleRepository),
            getHomeUseCase = GetHomeUseCase(homeRepository),
            issueInvitationUseCase = IssueInvitationUseCase(FakeInvitationRepository()),
            invitationShareClient = FakeInvitationShareClient(),
        )

        advanceUntilIdle()

        assertEquals("circle-1", homeRepository.requestedCircleId)
    }

    @Test
    fun `서클이 없으면 none 상태가 된다`() = runTest {
        val viewModel = HomeViewModel(
            listCirclesUseCase = ListCirclesUseCase(
                FakeCircleRepository(result = AppResult.Success(emptyList())),
            ),
            getHomeUseCase = GetHomeUseCase(FakeHomeRepository(result = AppResult.Success(defaultOverview()))),
            issueInvitationUseCase = IssueInvitationUseCase(FakeInvitationRepository()),
            invitationShareClient = FakeInvitationShareClient(),
        )

        advanceUntilIdle()

        assertEquals(HomeState.None, viewModel.uiState.value)
    }

    @Test
    fun `홈 조회 성공 시 content 상태가 된다`() = runTest {
        val overview = defaultOverview()
        val viewModel = HomeViewModel(
            listCirclesUseCase = ListCirclesUseCase(
                FakeCircleRepository(
                    result = AppResult.Success(
                        listOf(overview.circle),
                    ),
                ),
            ),
            getHomeUseCase = GetHomeUseCase(
                FakeHomeRepository(result = AppResult.Success(overview)),
            ),
            issueInvitationUseCase = IssueInvitationUseCase(FakeInvitationRepository()),
            invitationShareClient = FakeInvitationShareClient(),
        )

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertTrue(uiState is HomeState.Content)
        assertEquals(overview.circle.name, (uiState as HomeState.Content).circle.name)
        assertEquals(overview.totalScheduleCount, uiState.totalScheduleCount)
    }

    @Test
    fun `서클 조회 실패 시 스낵바 effect를 보낸다`() = runTest {
        val viewModel = HomeViewModel(
            listCirclesUseCase = ListCirclesUseCase(
                FakeCircleRepository(
                    result = AppResult.Failure(AppError.NetworkUnavailable()),
                ),
            ),
            getHomeUseCase = GetHomeUseCase(FakeHomeRepository(result = AppResult.Success(defaultOverview()))),
            issueInvitationUseCase = IssueInvitationUseCase(FakeInvitationRepository()),
            invitationShareClient = FakeInvitationShareClient(),
        )

        val effectDeferred = async { viewModel.uiEffect.first() }

        advanceUntilIdle()

        val effect = effectDeferred.await()
        assertTrue(effect is HomeEffect.ShowSnackbar)
        val message = (effect as HomeEffect.ShowSnackbar).message
        assertTrue(message is HomeSnackbarMessage.Error)
        assertEquals(HomeState.None, viewModel.uiState.value)
    }

    @Test
    fun `서클 생성 이벤트를 받으면 홈을 다시 조회한다`() = runTest {
        val circleRepository = FakeCircleRepository(
            result = AppResult.Success(
                listOf(
                    CircleSummary(circleId = "circle-1", name = "비니네", ownerId = "owner-1"),
                ),
            ),
        )
        val homeRepository = FakeHomeRepository(
            result = AppResult.Success(defaultOverview()),
        )
        HomeViewModel(
            listCirclesUseCase = ListCirclesUseCase(circleRepository),
            getHomeUseCase = GetHomeUseCase(homeRepository),
            issueInvitationUseCase = IssueInvitationUseCase(FakeInvitationRepository()),
            invitationShareClient = FakeInvitationShareClient(),
        )

        advanceUntilIdle()
        assertEquals(1, circleRepository.listCallCount)
        assertEquals(1, homeRepository.requestCount)

        CircleCreatedEventBus.emit()
        advanceUntilIdle()

        assertEquals(2, circleRepository.listCallCount)
        assertEquals(2, homeRepository.requestCount)
    }

    @Test
    fun `초대 클릭 시 초대장을 발급하고 공유를 호출한다`() = runTest {
        val overview = defaultOverview()
        val invitationRepository = FakeInvitationRepository()
        val shareClient = FakeInvitationShareClient()
        val viewModel = HomeViewModel(
            listCirclesUseCase = ListCirclesUseCase(
                FakeCircleRepository(result = AppResult.Success(listOf(overview.circle))),
            ),
            getHomeUseCase = GetHomeUseCase(FakeHomeRepository(result = AppResult.Success(overview))),
            issueInvitationUseCase = IssueInvitationUseCase(invitationRepository),
            invitationShareClient = shareClient,
        )

        advanceUntilIdle()
        viewModel.dispatch(HomeIntent.InviteClicked)
        advanceUntilIdle()

        assertEquals(InvitationType.CIRCLE, invitationRepository.requestedType)
        assertEquals(overview.circle.circleId, invitationRepository.requestedTargetId)
        assertEquals("수빈", shareClient.sharedInviterName)
        assertEquals(overview.circle.name, shareClient.sharedCircleName)
        assertEquals("invite-token", shareClient.sharedInvitation?.token)
        assertTrue((viewModel.uiState.value as HomeState.Content).isInviting.not())
    }
}

private class FakeCircleRepository(private val result: AppResult<List<CircleSummary>>) : CircleRepository {
    var listCallCount: Int = 0

    override suspend fun listCircles(): AppResult<List<CircleSummary>> {
        listCallCount += 1
        return result
    }

    override suspend fun createCircle(name: String): AppResult<CircleSummary> {
        error("Not used")
    }

    override suspend fun transferInitiator(
        circleId: String,
        targetMemberId: String,
    ): AppResult<CircleSummary> {
        error("Not used")
    }
}

private class FakeHomeRepository(private val result: AppResult<HomeOverview>) : HomeRepository {
    var requestedCircleId: String? = null
    var requestCount: Int = 0

    override suspend fun getHome(circleId: String): AppResult<HomeOverview> {
        requestedCircleId = circleId
        requestCount += 1
        return result
    }

    override suspend fun getMembers(circleId: String): AppResult<List<CircleMember>> {
        error("Not used")
    }
}

private class FakeInvitationRepository : InvitationRepository {
    var requestedType: InvitationType? = null
    var requestedTargetId: String? = null

    override suspend fun issueInvitation(
        type: InvitationType,
        targetId: String,
        inviteToName: String?,
        message: String?,
    ): AppResult<IssuedInvitation> {
        requestedType = type
        requestedTargetId = targetId
        return AppResult.Success(
            IssuedInvitation(
                invitationId = "invite-1",
                token = "invite-token",
                expiresAt = "2026-07-30T00:00:00",
            ),
        )
    }

    override suspend fun getInvitationByToken(token: String): AppResult<InvitationDetail> = AppResult.Success(
        InvitationDetail(
            invitationId = "invite-1",
            circleName = "비니네",
            inviter = InvitationIssuer(
                nickname = "초대자",
                avatarColor = "#FFD35C",
            ),
            expiresAt = "2026-07-30T00:00:00",
        ),
    )

    override suspend fun acceptInvitation(token: String): AppResult<AcceptedInvitation> = AppResult.Success(
        AcceptedInvitation(circleId = "circle-1"),
    )
}

private class FakeInvitationShareClient(
    private val result: InvitationShareResult = InvitationShareResult.Success,
) : InvitationShareClient {
    var sharedInviterName: String? = null
    var sharedCircleName: String? = null
    var sharedInvitation: IssuedInvitation? = null

    override suspend fun shareInvitation(
        inviterName: String,
        circleName: String,
        invitation: IssuedInvitation,
    ): InvitationShareResult {
        sharedInviterName = inviterName
        sharedCircleName = circleName
        sharedInvitation = invitation
        return result
    }
}

private fun defaultOverview(): HomeOverview = HomeOverview(
    circle = CircleSummary(
        circleId = "circle-1",
        name = "비니네",
        ownerId = "owner-1",
    ),
    members = listOf(
        CircleMember(
            memberId = "member-1",
            nickname = "수빈 (나)",
            avatarColor = "#FFD35C",
            profileImageUrl = null,
            isMe = true,
            role = "MEMBER",
        ),
    ),
    canInvite = true,
    mainSchedule = ScheduleSummary(
        scheduleId = "schedule-1",
        title = "가족 식사",
        startDate = "2026-06-28",
        endDate = "2026-06-28",
        startTime = "18:00",
        endTime = "20:00",
        isAllDay = false,
        needConfirm = false,
        status = ScheduleStatus.UPCOMING,
        progressRate = 0,
        dday = 0,
    ),
    schedules = emptyList(),
    totalScheduleCount = 1L,
)
