package com.saion.feature.home.impl.home

import com.saion.core.domain.repository.CurrentCircleRepository
import com.saion.core.domain.repository.CircleRepository
import com.saion.core.domain.repository.HomeRepository
import com.saion.core.domain.repository.InvitationRepository
import com.saion.core.domain.usecase.circle.ObserveCurrentCircleUseCase
import com.saion.core.domain.usecase.circle.ObserveResolvedCurrentCircleUseCase
import com.saion.core.domain.usecase.circle.SyncCurrentCircleUseCase
import com.saion.core.domain.usecase.home.GetHomeInviterNameUseCase
import com.saion.core.domain.usecase.home.ObserveHomeUseCase
import com.saion.core.domain.usecase.home.RefreshHomeUseCase
import com.saion.core.model.member.MemberInfo
import com.saion.core.domain.usecase.invitation.IssueInvitationUseCase
import com.saion.core.model.circle.CircleSummary
import com.saion.core.model.home.CircleMember
import com.saion.core.model.home.HomeOverview
import com.saion.core.model.invitation.AcceptedInvitation
import com.saion.core.model.invitation.InvitationDetail
import com.saion.core.model.invitation.InvitationIssuer
import com.saion.core.model.invitation.InvitationType
import com.saion.core.model.invitation.IssuedInvitation
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import com.saion.core.model.schedule.ScheduleStatus
import com.saion.core.model.schedule.ScheduleSummary
import com.saion.core.model.schedule.ScheduleUrgencyLevel
import com.saion.core.share.InvitationShareClient
import com.saion.core.share.InvitationShareResult
import com.saion.feature.home.impl.home.viewmodel.HomeEffect
import com.saion.feature.home.impl.home.viewmodel.HomeIntent
import com.saion.feature.home.impl.home.viewmodel.HomeSnackbarMessage
import com.saion.feature.home.impl.home.viewmodel.HomeState
import com.saion.feature.home.impl.home.viewmodel.HomeViewModel
import com.saion.feature.home.impl.home.viewmodel.toUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
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
    fun `현재 써클 id로 홈 조회를 수행한다`() = runTest {
        val currentCircleRepository = FakeCurrentCircleRepository(initialCircleId = "circle-1")
        val homeRepository = FakeHomeRepository(
            result = AppResult.Success(defaultOverview()),
        )

        HomeViewModel(
            observeResolvedCurrentCircleUseCase = observeResolvedCurrentCircleUseCase(currentCircleRepository),
            observeHomeUseCase = ObserveHomeUseCase(homeRepository),
            refreshHomeUseCase = RefreshHomeUseCase(homeRepository),
            getHomeInviterNameUseCase = GetHomeInviterNameUseCase(),
            issueInvitationUseCase = IssueInvitationUseCase(FakeInvitationRepository()),
            invitationShareClient = FakeInvitationShareClient(),
        )

        advanceUntilIdle()

        assertEquals("circle-1", homeRepository.requestedCircleId)
    }

    @Test
    fun `현재 써클이 없고 sync로 복구되지 않으면 none 상태가 된다`() = runTest {
        val homeRepository = FakeHomeRepository(result = AppResult.Success(defaultOverview()))
        val viewModel = HomeViewModel(
            observeResolvedCurrentCircleUseCase = observeResolvedCurrentCircleUseCase(FakeCurrentCircleRepository(initialCircleId = null)),
            observeHomeUseCase = ObserveHomeUseCase(homeRepository),
            refreshHomeUseCase = RefreshHomeUseCase(homeRepository),
            getHomeInviterNameUseCase = GetHomeInviterNameUseCase(),
            issueInvitationUseCase = IssueInvitationUseCase(FakeInvitationRepository()),
            invitationShareClient = FakeInvitationShareClient(),
        )

        advanceUntilIdle()

        assertEquals(HomeState.None, viewModel.uiState.value)
    }

    @Test
    fun `홈 조회 성공 시 content 상태가 된다`() = runTest {
        val overview = defaultOverview()
        val homeRepository = FakeHomeRepository(result = AppResult.Success(overview))
        val viewModel = HomeViewModel(
            observeResolvedCurrentCircleUseCase = observeResolvedCurrentCircleUseCase(
                FakeCurrentCircleRepository(initialCircleId = overview.circle.circleId),
            ),
            observeHomeUseCase = ObserveHomeUseCase(homeRepository),
            refreshHomeUseCase = RefreshHomeUseCase(homeRepository),
            getHomeInviterNameUseCase = GetHomeInviterNameUseCase(),
            issueInvitationUseCase = IssueInvitationUseCase(FakeInvitationRepository()),
            invitationShareClient = FakeInvitationShareClient(),
        )

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertTrue(uiState is HomeState.Content)
        assertEquals(overview.circle.name, (uiState as HomeState.Content).circle.name)
    }

    @Test
    fun `content 상태에서 나 외의 구성원이 있으면 hero를 숨긴다`() {
        val uiState = defaultOverview(
            mainSchedule = null,
            schedules = emptyList(),
        ).toUiState()

        assertTrue(uiState.shouldShowHero.not())
    }

    @Test
    fun `content 상태에서 나만 있으면 hero를 노출한다`() {
        val uiState = defaultOverview(
            members = listOf(
                CircleMember(
                    memberId = "member-1",
                    nickname = "수빈",
                    avatarColor = "#7DB1FF",
                    profileImageUrl = null,
                    isMe = true,
                    role = "ADMIN",
                ),
            ),
            mainSchedule = null,
        ).toUiState()

        assertTrue(uiState.shouldShowHero)
    }

    @Test
    fun `content 상태에서 main 일정이 있으면 다른 구성원이 있어도 hero를 노출한다`() {
        val uiState = defaultOverview().toUiState()

        assertTrue(uiState.shouldShowHero)
        assertEquals("schedule-0", uiState.heroSchedule?.scheduleId)
    }

    @Test
    fun `section 일정은 main 일정을 제외한 schedules를 그대로 사용한다`() {
        val uiState = defaultOverview().toUiState()

        assertEquals(listOf("schedule-1", "schedule-2"), uiState.sectionSchedules.map(ScheduleSummary::scheduleId))
    }

    @Test
    fun `main 일정이 없어도 schedules가 있으면 hero 없이 section 일정은 유지된다`() {
        val uiState = defaultOverview(mainSchedule = null).toUiState()

        assertTrue(uiState.shouldShowHero.not())
        assertEquals(null, uiState.heroSchedule)
        assertEquals(listOf("schedule-1", "schedule-2"), uiState.sectionSchedules.map(ScheduleSummary::scheduleId))
    }

    @Test
    fun `홈 조회 실패 시 스낵바 effect를 보낸다`() = runTest {
        val homeRepository = FakeHomeRepository(result = AppResult.Failure(AppError.NetworkUnavailable()))
        val viewModel = HomeViewModel(
            observeResolvedCurrentCircleUseCase = observeResolvedCurrentCircleUseCase(FakeCurrentCircleRepository(initialCircleId = "circle-1")),
            observeHomeUseCase = ObserveHomeUseCase(homeRepository),
            refreshHomeUseCase = RefreshHomeUseCase(homeRepository),
            getHomeInviterNameUseCase = GetHomeInviterNameUseCase(),
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
    fun `현재 써클이 변경되면 홈을 다시 조회한다`() = runTest {
        val currentCircleRepository = FakeCurrentCircleRepository(initialCircleId = "circle-1")
        val homeRepository = FakeHomeRepository(
            result = AppResult.Success(defaultOverview()),
        )
        HomeViewModel(
            observeResolvedCurrentCircleUseCase = observeResolvedCurrentCircleUseCase(currentCircleRepository),
            observeHomeUseCase = ObserveHomeUseCase(homeRepository),
            refreshHomeUseCase = RefreshHomeUseCase(homeRepository),
            getHomeInviterNameUseCase = GetHomeInviterNameUseCase(),
            issueInvitationUseCase = IssueInvitationUseCase(FakeInvitationRepository()),
            invitationShareClient = FakeInvitationShareClient(),
        )

        advanceUntilIdle()
        assertEquals(1, homeRepository.requestCount)

        currentCircleRepository.update("circle-2")
        advanceUntilIdle()

        assertEquals(2, homeRepository.requestCount)
    }

    @Test
    fun `초대 클릭 시 초대장을 발급하고 공유를 호출한다`() = runTest {
        val overview = defaultOverview()
        val homeRepository = FakeHomeRepository(result = AppResult.Success(overview))
        val invitationRepository = FakeInvitationRepository()
        val shareClient = FakeInvitationShareClient()
        val viewModel = HomeViewModel(
            observeResolvedCurrentCircleUseCase = observeResolvedCurrentCircleUseCase(
                FakeCurrentCircleRepository(initialCircleId = overview.circle.circleId),
            ),
            observeHomeUseCase = ObserveHomeUseCase(homeRepository),
            refreshHomeUseCase = RefreshHomeUseCase(homeRepository),
            getHomeInviterNameUseCase = GetHomeInviterNameUseCase(),
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

    @Test
    fun `현재 써클이 비어 있어도 sync가 첫 써클을 정하면 홈을 조회한다`() = runTest {
        val currentCircleRepository = FakeCurrentCircleRepository(
            initialCircleId = null,
            syncedCircleId = "circle-1",
        )
        val homeRepository = FakeHomeRepository(
            result = AppResult.Success(defaultOverview()),
        )

        HomeViewModel(
            observeResolvedCurrentCircleUseCase = observeResolvedCurrentCircleUseCase(currentCircleRepository),
            observeHomeUseCase = ObserveHomeUseCase(homeRepository),
            refreshHomeUseCase = RefreshHomeUseCase(homeRepository),
            getHomeInviterNameUseCase = GetHomeInviterNameUseCase(),
            issueInvitationUseCase = IssueInvitationUseCase(FakeInvitationRepository()),
            invitationShareClient = FakeInvitationShareClient(),
        )

        advanceUntilIdle()

        assertEquals(1, currentCircleRepository.syncCallCount)
        assertEquals("circle-1", homeRepository.requestedCircleId)
        assertEquals(1, homeRepository.requestCount)
    }
}

private fun observeResolvedCurrentCircleUseCase(repository: FakeCurrentCircleRepository): ObserveResolvedCurrentCircleUseCase =
    ObserveResolvedCurrentCircleUseCase(
        observeCurrentCircleUseCase = ObserveCurrentCircleUseCase(repository),
        syncCurrentCircleUseCase = SyncCurrentCircleUseCase(repository, FakeResolvedCircleRepository(repository)),
    )

private class FakeCurrentCircleRepository(
    initialCircleId: String?,
    val syncedCircleId: String? = initialCircleId,
) : CurrentCircleRepository {
    private val flow = MutableStateFlow(initialCircleId)
    var syncCallCount: Int = 0

    fun update(circleId: String?) {
        flow.value = circleId
    }

    override fun observeCurrentCircleId(): StateFlow<String?> = flow

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
    private val repository: FakeCurrentCircleRepository,
) : CircleRepository {
    override fun observeCircles(): Flow<List<CircleSummary>> = flowOf(emptyList())

    override suspend fun listCircles(): AppResult<List<CircleSummary>> = AppResult.Success(emptyList())

    override suspend fun refreshCircles(): AppResult<List<CircleSummary>> {
        repository.syncCallCount += 1
        return AppResult.Success(
            repository.syncedCircleId?.let { listOf(CircleSummary(circleId = it, name = "circle", ownerId = "owner")) }.orEmpty(),
        )
    }

    override suspend fun createCircle(name: String): AppResult<CircleSummary> = error("Not used")

    override suspend fun transferInitiator(circleId: String, targetMemberId: String): AppResult<CircleSummary> = error("Not used")

    override suspend fun leave(circleId: String): AppResult<Unit> = error("Not used")
}

private class FakeHomeRepository(private val result: AppResult<HomeOverview>) : HomeRepository {
    var requestedCircleId: String? = null
    var requestCount: Int = 0
    private val homeState = MutableStateFlow<HomeOverview?>(null)

    override fun observeHome(circleId: String): Flow<HomeOverview?> = homeState

    override fun observeMembers(circleId: String): Flow<List<CircleMember>> = homeState.map { it?.members.orEmpty() }

    override suspend fun getHome(circleId: String): AppResult<HomeOverview> {
        return homeState.value?.let { AppResult.Success(it) } ?: refreshHome(circleId)
    }

    override suspend fun refreshHome(circleId: String): AppResult<HomeOverview> {
        requestedCircleId = circleId
        requestCount += 1
        if (result is AppResult.Success) {
            homeState.value = result.data
        }
        return result
    }

    override suspend fun getMembers(circleId: String): AppResult<List<CircleMember>> {
        error("Not used")
    }

    override suspend fun updateCachedMyMemberProfile(
        circleId: String,
        memberInfo: MemberInfo,
    ) = Unit
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

    override suspend fun getInvitationByToken(token: String): AppResult<InvitationDetail> {
        error("Not used")
    }

    override suspend fun acceptInvitation(token: String): AppResult<AcceptedInvitation> {
        error("Not used")
    }
}

private class FakeInvitationShareClient : InvitationShareClient {
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
        return InvitationShareResult.Success
    }
}

private fun defaultOverview(
    members: List<CircleMember> = listOf(
        CircleMember(
            memberId = "member-1",
            nickname = "수빈",
            avatarColor = "#7DB1FF",
            profileImageUrl = null,
            isMe = true,
            role = "ADMIN",
        ),
        CircleMember(
            memberId = "member-2",
            nickname = "민지",
            avatarColor = "#5CE0B1",
            profileImageUrl = null,
            isMe = false,
            role = "MEMBER",
        ),
    ),
    mainSchedule: ScheduleSummary? = ScheduleSummary(
        scheduleId = "schedule-0",
        title = "오늘 일정",
        startDate = "2026-07-17",
        endDate = "2026-07-17",
        startTime = "12:00",
        endTime = "13:00",
        isAllDay = false,
        needConfirm = false,
        status = ScheduleStatus.UPCOMING,
        urgencyLevel = ScheduleUrgencyLevel.URGENT,
        progressRate = 0,
        dday = 0,
    ),
    schedules: List<ScheduleSummary> = listOf(
        ScheduleSummary(
            scheduleId = "schedule-1",
            title = "가족 식사",
            startDate = "2026-07-20",
            endDate = "2026-07-20",
            startTime = "18:00",
            endTime = "20:00",
            isAllDay = false,
            needConfirm = false,
            status = ScheduleStatus.UPCOMING,
            urgencyLevel = ScheduleUrgencyLevel.URGENT,
            progressRate = 0,
            dday = 3,
        ),
        ScheduleSummary(
            scheduleId = "schedule-2",
            title = "장보기",
            startDate = "2026-07-22",
            endDate = "2026-07-22",
            startTime = "15:00",
            endTime = "16:00",
            isAllDay = false,
            needConfirm = false,
            status = ScheduleStatus.UPCOMING,
            urgencyLevel = ScheduleUrgencyLevel.NORMAL,
            progressRate = 0,
            dday = 5,
        ),
    ),
): HomeOverview = HomeOverview(
    circle = CircleSummary(circleId = "circle-1", name = "비니네", ownerId = "owner-1"),
    members = members,
    canInvite = true,
    mainSchedule = mainSchedule,
    schedules = schedules,
    totalScheduleCount = 3L,
)
