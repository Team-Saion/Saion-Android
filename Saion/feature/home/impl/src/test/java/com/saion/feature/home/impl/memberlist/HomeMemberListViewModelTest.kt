package com.saion.feature.home.impl.memberlist

import com.saion.core.domain.repository.CurrentCircleRepository
import com.saion.core.domain.repository.CircleRepository
import com.saion.core.domain.repository.HomeRepository
import com.saion.core.domain.usecase.circle.ClearCurrentCircleUseCase
import com.saion.core.domain.usecase.circle.LeaveCircleUseCase
import com.saion.core.domain.usecase.circle.ObserveResolvedCurrentCircleUseCase
import com.saion.core.domain.usecase.circle.ObserveCurrentCircleUseCase
import com.saion.core.domain.usecase.circle.SyncCurrentCircleUseCase
import com.saion.core.domain.usecase.home.ObserveHomeMembersUseCase
import com.saion.core.domain.usecase.home.RefreshHomeUseCase
import com.saion.core.model.home.CircleMember
import com.saion.core.model.home.HomeOverview
import com.saion.core.model.member.MemberInfo
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import com.saion.feature.home.impl.memberlist.viewmodel.HomeMemberListEffect
import com.saion.feature.home.impl.memberlist.viewmodel.HomeMemberListIntent
import com.saion.feature.home.impl.memberlist.viewmodel.HomeMemberListSnackbarMessage
import com.saion.feature.home.impl.memberlist.viewmodel.HomeMemberListStatus
import com.saion.feature.home.impl.memberlist.viewmodel.HomeMemberListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
class HomeMemberListViewModelTest {
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
    fun `현재 circle id로 구성원 목록을 조회한다`() = runTest {
        val repository = FakeMemberHomeRepository(
            result = AppResult.Success(defaultMembers()),
        )
        val viewModel = HomeMemberListViewModel(
            observeResolvedCurrentCircleUseCase = observeResolvedCurrentCircleUseCase(
                FakeCurrentCircleRepository(initialCircleId = "circle-1"),
            ),
            observeHomeMembersUseCase = ObserveHomeMembersUseCase(repository),
            refreshHomeUseCase = RefreshHomeUseCase(repository),
            leaveCircleUseCase = LeaveCircleUseCase(FakeLeaveCircleRepository()),
            clearCurrentCircleUseCase = ClearCurrentCircleUseCase(FakeCurrentCircleRepository(initialCircleId = "circle-1")),
        )

        advanceUntilIdle()

        assertEquals("circle-1", repository.requestedCircleId)
        assertEquals(1, repository.requestCount)
    }

    @Test
    fun `조회 성공 시 전체 구성원 목록을 content 상태로 노출한다`() = runTest {
        val repository = FakeMemberHomeRepository(result = AppResult.Success(defaultMembers()))
        val viewModel = HomeMemberListViewModel(
            observeResolvedCurrentCircleUseCase = observeResolvedCurrentCircleUseCase(
                FakeCurrentCircleRepository(initialCircleId = "circle-1"),
            ),
            observeHomeMembersUseCase = ObserveHomeMembersUseCase(repository),
            refreshHomeUseCase = RefreshHomeUseCase(repository),
            leaveCircleUseCase = LeaveCircleUseCase(FakeLeaveCircleRepository()),
            clearCurrentCircleUseCase = ClearCurrentCircleUseCase(FakeCurrentCircleRepository(initialCircleId = "circle-1")),
        )

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(HomeMemberListStatus.Content, uiState.status)
        assertEquals(3, uiState.members.size)
        assertEquals(listOf("member-1", "member-2", "member-3"), uiState.members.map(CircleMember::memberId))
    }

    @Test
    fun `내 구성원이 없으면 전체 목록만 표시한다`() = runTest {
        val repository = FakeMemberHomeRepository(
            result = AppResult.Success(
                listOf(
                    CircleMember(
                        memberId = "member-2",
                        nickname = "하나",
                        avatarColor = "#FFD35C",
                        profileImageUrl = null,
                        isMe = false,
                        role = "MEMBER",
                    ),
                ),
            ),
        )
        val viewModel = HomeMemberListViewModel(
            observeResolvedCurrentCircleUseCase = observeResolvedCurrentCircleUseCase(
                FakeCurrentCircleRepository(initialCircleId = "circle-1"),
            ),
            observeHomeMembersUseCase = ObserveHomeMembersUseCase(repository),
            refreshHomeUseCase = RefreshHomeUseCase(repository),
            leaveCircleUseCase = LeaveCircleUseCase(FakeLeaveCircleRepository()),
            clearCurrentCircleUseCase = ClearCurrentCircleUseCase(FakeCurrentCircleRepository(initialCircleId = "circle-1")),
        )

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(HomeMemberListStatus.Content, uiState.status)
        assertEquals(1, uiState.members.size)
        assertEquals("member-2", uiState.members.first().memberId)
    }

    @Test
    fun `구성원 목록이 비어 있어도 로딩 상태에 머무르지 않는다`() = runTest {
        val repository = FakeMemberHomeRepository(
            result = AppResult.Success(emptyList()),
        )
        val viewModel = HomeMemberListViewModel(
            observeResolvedCurrentCircleUseCase = observeResolvedCurrentCircleUseCase(
                FakeCurrentCircleRepository(initialCircleId = "circle-1"),
            ),
            observeHomeMembersUseCase = ObserveHomeMembersUseCase(repository),
            refreshHomeUseCase = RefreshHomeUseCase(repository),
            leaveCircleUseCase = LeaveCircleUseCase(FakeLeaveCircleRepository()),
            clearCurrentCircleUseCase = ClearCurrentCircleUseCase(FakeCurrentCircleRepository(initialCircleId = "circle-1")),
        )

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(HomeMemberListStatus.Empty, uiState.status)
        assertTrue(uiState.members.isEmpty())
    }

    @Test
    fun `조회 실패 시 에러 상태와 스낵바 effect를 보낸다`() = runTest {
        val repository = FakeMemberHomeRepository(result = AppResult.Failure(AppError.NetworkUnavailable()))
        val viewModel = HomeMemberListViewModel(
            observeResolvedCurrentCircleUseCase = observeResolvedCurrentCircleUseCase(
                FakeCurrentCircleRepository(initialCircleId = "circle-1"),
            ),
            observeHomeMembersUseCase = ObserveHomeMembersUseCase(repository),
            refreshHomeUseCase = RefreshHomeUseCase(repository),
            leaveCircleUseCase = LeaveCircleUseCase(FakeLeaveCircleRepository()),
            clearCurrentCircleUseCase = ClearCurrentCircleUseCase(FakeCurrentCircleRepository(initialCircleId = "circle-1")),
        )

        val effectDeferred = async { viewModel.uiEffect.first() }

        advanceUntilIdle()

        val effect = effectDeferred.await()
        assertEquals(HomeMemberListStatus.Error, viewModel.uiState.value.status)
        assertTrue(effect is HomeMemberListEffect.ShowSnackbar)
        val message = (effect as HomeMemberListEffect.ShowSnackbar).message
        assertTrue(message is HomeMemberListSnackbarMessage.Error)
    }

    @Test
    fun `현재 써클이 없으면 empty 상태가 된다`() = runTest {
        val repository = FakeMemberHomeRepository(result = AppResult.Success(defaultMembers()))
        val viewModel = HomeMemberListViewModel(
            observeResolvedCurrentCircleUseCase = observeResolvedCurrentCircleUseCase(
                FakeCurrentCircleRepository(initialCircleId = null),
            ),
            observeHomeMembersUseCase = ObserveHomeMembersUseCase(repository),
            refreshHomeUseCase = RefreshHomeUseCase(repository),
            leaveCircleUseCase = LeaveCircleUseCase(FakeLeaveCircleRepository()),
            clearCurrentCircleUseCase = ClearCurrentCircleUseCase(FakeCurrentCircleRepository(initialCircleId = null)),
        )

        advanceUntilIdle()

        assertEquals(HomeMemberListStatus.Empty, viewModel.uiState.value.status)
    }

    @Test
    fun `현재 써클이 비어 있어도 sync가 복구하면 구성원 목록을 조회한다`() = runTest {
        val repository = FakeMemberHomeRepository(
            result = AppResult.Success(defaultMembers()),
        )
        val viewModel = HomeMemberListViewModel(
            observeResolvedCurrentCircleUseCase = observeResolvedCurrentCircleUseCase(
                FakeCurrentCircleRepository(initialCircleId = null, syncedCircleId = "circle-1"),
            ),
            observeHomeMembersUseCase = ObserveHomeMembersUseCase(repository),
            refreshHomeUseCase = RefreshHomeUseCase(repository),
            leaveCircleUseCase = LeaveCircleUseCase(FakeLeaveCircleRepository()),
            clearCurrentCircleUseCase = ClearCurrentCircleUseCase(FakeCurrentCircleRepository(initialCircleId = null, syncedCircleId = "circle-1")),
        )

        advanceUntilIdle()

        assertEquals(HomeMemberListStatus.Content, viewModel.uiState.value.status)
        assertEquals("circle-1", repository.requestedCircleId)
    }

    @Test
    fun `나가기 버튼 클릭 시 확인 다이얼로그가 열린다`() = runTest {
        val currentCircleRepository = FakeCurrentCircleRepository(initialCircleId = "circle-1")
        val viewModel = HomeMemberListViewModel(
            observeResolvedCurrentCircleUseCase = observeResolvedCurrentCircleUseCase(currentCircleRepository),
            observeHomeMembersUseCase = ObserveHomeMembersUseCase(FakeMemberHomeRepository(AppResult.Success(defaultMembers()))),
            refreshHomeUseCase = RefreshHomeUseCase(FakeMemberHomeRepository(AppResult.Success(defaultMembers()))),
            leaveCircleUseCase = LeaveCircleUseCase(FakeLeaveCircleRepository()),
            clearCurrentCircleUseCase = ClearCurrentCircleUseCase(currentCircleRepository),
        )

        advanceUntilIdle()
        viewModel.dispatch(HomeMemberListIntent.ClickLeave)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.showLeaveDialog)
    }

    @Test
    fun `나가기 다이얼로그 취소 시 닫히고 로딩이 해제된다`() = runTest {
        val currentCircleRepository = FakeCurrentCircleRepository(initialCircleId = "circle-1")
        val viewModel = HomeMemberListViewModel(
            observeResolvedCurrentCircleUseCase = observeResolvedCurrentCircleUseCase(currentCircleRepository),
            observeHomeMembersUseCase = ObserveHomeMembersUseCase(FakeMemberHomeRepository(AppResult.Success(defaultMembers()))),
            refreshHomeUseCase = RefreshHomeUseCase(FakeMemberHomeRepository(AppResult.Success(defaultMembers()))),
            leaveCircleUseCase = LeaveCircleUseCase(FakeLeaveCircleRepository()),
            clearCurrentCircleUseCase = ClearCurrentCircleUseCase(currentCircleRepository),
        )

        advanceUntilIdle()
        viewModel.dispatch(HomeMemberListIntent.ClickLeave)
        advanceUntilIdle()
        viewModel.dispatch(HomeMemberListIntent.DismissLeaveDialog)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.showLeaveDialog)
        assertFalse(viewModel.uiState.value.isLeaveLoading)
    }

    @Test
    fun `나가기 성공 시 완료 effect를 보내고 다이얼로그가 닫힌다`() = runTest {
        val leaveRepository = FakeLeaveCircleRepository(result = AppResult.Success(Unit))
        val homeRepository = FakeMemberHomeRepository(AppResult.Success(defaultMembers()))
        val currentCircleRepository = FakeCurrentCircleRepository(initialCircleId = "circle-1", syncedCircleId = null)
        val viewModel = HomeMemberListViewModel(
            observeResolvedCurrentCircleUseCase = observeResolvedCurrentCircleUseCase(currentCircleRepository),
            observeHomeMembersUseCase = ObserveHomeMembersUseCase(homeRepository),
            refreshHomeUseCase = RefreshHomeUseCase(homeRepository),
            leaveCircleUseCase = LeaveCircleUseCase(leaveRepository),
            clearCurrentCircleUseCase = ClearCurrentCircleUseCase(currentCircleRepository),
        )
        val effectDeferred = async { viewModel.uiEffect.first() }

        advanceUntilIdle()
        viewModel.dispatch(HomeMemberListIntent.ClickLeave)
        advanceUntilIdle()
        viewModel.dispatch(HomeMemberListIntent.ConfirmLeave)
        advanceUntilIdle()

        assertEquals("circle-1", leaveRepository.requestedCircleId)
        assertEquals(HomeMemberListEffect.LeaveCompleted, effectDeferred.await())
        assertFalse(viewModel.uiState.value.showLeaveDialog)
        assertFalse(viewModel.uiState.value.isLeaveLoading)
        assertTrue(currentCircleRepository.clearCallCount > 0)
        assertEquals(null, currentCircleRepository.getCurrentCircleId())
    }

    @Test
    fun `나가기 실패 시 스낵바 effect를 보내고 다이얼로그를 유지한다`() = runTest {
        val leaveRepository = FakeLeaveCircleRepository(result = AppResult.Failure(AppError.NetworkUnavailable()))
        val homeRepository = FakeMemberHomeRepository(AppResult.Success(defaultMembers()))
        val currentCircleRepository = FakeCurrentCircleRepository(initialCircleId = "circle-1")
        val viewModel = HomeMemberListViewModel(
            observeResolvedCurrentCircleUseCase = observeResolvedCurrentCircleUseCase(currentCircleRepository),
            observeHomeMembersUseCase = ObserveHomeMembersUseCase(homeRepository),
            refreshHomeUseCase = RefreshHomeUseCase(homeRepository),
            leaveCircleUseCase = LeaveCircleUseCase(leaveRepository),
            clearCurrentCircleUseCase = ClearCurrentCircleUseCase(currentCircleRepository),
        )
        val effectDeferred = async { viewModel.uiEffect.first() }

        advanceUntilIdle()
        viewModel.dispatch(HomeMemberListIntent.ClickLeave)
        advanceUntilIdle()
        viewModel.dispatch(HomeMemberListIntent.ConfirmLeave)
        advanceUntilIdle()

        val effect = effectDeferred.await()
        assertTrue(effect is HomeMemberListEffect.ShowSnackbar)
        assertTrue((effect as HomeMemberListEffect.ShowSnackbar).message is HomeMemberListSnackbarMessage.Error)
        assertTrue(viewModel.uiState.value.showLeaveDialog)
        assertFalse(viewModel.uiState.value.isLeaveLoading)
        assertEquals(0, currentCircleRepository.clearCallCount)
    }

    @Test
    fun `나가기 로딩 중 재호출 시 중복 요청하지 않는다`() = runTest {
        val leaveRepository = FakeLeaveCircleRepository(result = AppResult.Success(Unit))
        val homeRepository = FakeMemberHomeRepository(AppResult.Success(defaultMembers()))
        val currentCircleRepository = FakeCurrentCircleRepository(initialCircleId = "circle-1")
        val viewModel = HomeMemberListViewModel(
            observeResolvedCurrentCircleUseCase = observeResolvedCurrentCircleUseCase(currentCircleRepository),
            observeHomeMembersUseCase = ObserveHomeMembersUseCase(homeRepository),
            refreshHomeUseCase = RefreshHomeUseCase(homeRepository),
            leaveCircleUseCase = LeaveCircleUseCase(leaveRepository),
            clearCurrentCircleUseCase = ClearCurrentCircleUseCase(currentCircleRepository),
        )

        advanceUntilIdle()
        viewModel.dispatch(HomeMemberListIntent.ClickLeave)
        advanceUntilIdle()
        viewModel.dispatch(HomeMemberListIntent.ConfirmLeave)
        viewModel.dispatch(HomeMemberListIntent.ConfirmLeave)
        advanceUntilIdle()

        assertEquals(1, leaveRepository.leaveCallCount)
    }

    @Test
    fun `현재 써클이 없으면 나가기 API를 호출하지 않고 실패 스낵바를 보낸다`() = runTest {
        val leaveRepository = FakeLeaveCircleRepository(result = AppResult.Success(Unit))
        val homeRepository = FakeMemberHomeRepository(AppResult.Success(defaultMembers()))
        val currentCircleRepository = FakeCurrentCircleRepository(initialCircleId = null)
        val viewModel = HomeMemberListViewModel(
            observeResolvedCurrentCircleUseCase = observeResolvedCurrentCircleUseCase(currentCircleRepository),
            observeHomeMembersUseCase = ObserveHomeMembersUseCase(homeRepository),
            refreshHomeUseCase = RefreshHomeUseCase(homeRepository),
            leaveCircleUseCase = LeaveCircleUseCase(leaveRepository),
            clearCurrentCircleUseCase = ClearCurrentCircleUseCase(currentCircleRepository),
        )
        val effectDeferred = async { viewModel.uiEffect.first() }

        advanceUntilIdle()
        viewModel.dispatch(HomeMemberListIntent.ConfirmLeave)
        advanceUntilIdle()

        val effect = effectDeferred.await()
        assertTrue(effect is HomeMemberListEffect.ShowSnackbar)
        assertTrue((effect as HomeMemberListEffect.ShowSnackbar).message is HomeMemberListSnackbarMessage.Text)
        assertEquals(0, leaveRepository.leaveCallCount)
    }
}

private fun observeResolvedCurrentCircleUseCase(
    repository: FakeCurrentCircleRepository,
): ObserveResolvedCurrentCircleUseCase = ObserveResolvedCurrentCircleUseCase(
    observeCurrentCircleUseCase = ObserveCurrentCircleUseCase(repository),
    syncCurrentCircleUseCase = SyncCurrentCircleUseCase(repository, FakeResolvedCircleRepository(repository)),
)

private class FakeCurrentCircleRepository(
    initialCircleId: String?,
    val syncedCircleId: String? = initialCircleId,
) : CurrentCircleRepository {
    private val flow = MutableStateFlow(initialCircleId)
    var clearCallCount: Int = 0

    override fun observeCurrentCircleId(): StateFlow<String?> = flow

    override suspend fun getCurrentCircleId(): String? = flow.value

    override suspend fun selectCircle(circleId: String): AppResult<Unit> {
        flow.value = circleId
        return AppResult.Success(Unit)
    }

    override suspend fun clearCurrentCircle() {
        clearCallCount += 1
        flow.value = null
    }
}

private class FakeResolvedCircleRepository(
    private val repository: FakeCurrentCircleRepository,
) : CircleRepository {
    override fun observeCircles(): Flow<List<com.saion.core.model.circle.CircleSummary>> = flowOf(emptyList())

    override suspend fun listCircles(): AppResult<List<com.saion.core.model.circle.CircleSummary>> = AppResult.Success(emptyList())

    override suspend fun refreshCircles(): AppResult<List<com.saion.core.model.circle.CircleSummary>> =
        AppResult.Success(
            repository.syncedCircleId?.let {
                listOf(com.saion.core.model.circle.CircleSummary(circleId = it, name = "circle", ownerId = "owner"))
            }.orEmpty(),
        )

    override suspend fun createCircle(name: String): AppResult<com.saion.core.model.circle.CircleSummary> = error("Not used")

    override suspend fun transferInitiator(
        circleId: String,
        targetMemberId: String,
    ): AppResult<com.saion.core.model.circle.CircleSummary> = error("Not used")

    override suspend fun leave(circleId: String): AppResult<Unit> = error("Not used")
}

private class FakeLeaveCircleRepository(
    private val result: AppResult<Unit> = AppResult.Success(Unit),
) : CircleRepository {
    var requestedCircleId: String? = null
    var leaveCallCount: Int = 0

    override fun observeCircles(): Flow<List<com.saion.core.model.circle.CircleSummary>> = flowOf(emptyList())

    override suspend fun listCircles(): AppResult<List<com.saion.core.model.circle.CircleSummary>> = AppResult.Success(emptyList())

    override suspend fun refreshCircles(): AppResult<List<com.saion.core.model.circle.CircleSummary>> = AppResult.Success(emptyList())

    override suspend fun createCircle(name: String): AppResult<com.saion.core.model.circle.CircleSummary> = error("Not used")

    override suspend fun transferInitiator(
        circleId: String,
        targetMemberId: String,
    ): AppResult<com.saion.core.model.circle.CircleSummary> = error("Not used")

    override suspend fun leave(circleId: String): AppResult<Unit> {
        requestedCircleId = circleId
        leaveCallCount += 1
        return result
    }
}

private class FakeMemberHomeRepository(
    private val result: AppResult<List<CircleMember>>,
) : HomeRepository {
    var requestedCircleId: String? = null
    var requestCount: Int = 0
    private val membersState = MutableStateFlow<List<CircleMember>>(emptyList())

    override fun observeHome(circleId: String): Flow<HomeOverview?> = flowOf(null)

    override fun observeMembers(circleId: String): Flow<List<CircleMember>> = membersState

    override suspend fun getHome(circleId: String): AppResult<HomeOverview> {
        error("Not used")
    }

    override suspend fun refreshHome(circleId: String): AppResult<HomeOverview> {
        requestedCircleId = circleId
        requestCount += 1
        if (result is AppResult.Success) {
            membersState.value = result.data
            return AppResult.Success(
                HomeOverview(
                    circle = com.saion.core.model.circle.CircleSummary(circleId = circleId, name = "circle", ownerId = "owner"),
                    members = result.data,
                    canInvite = true,
                    mainSchedule = null,
                    schedules = emptyList(),
                    totalScheduleCount = 0L,
                ),
            )
        }
        return AppResult.Failure((result as AppResult.Failure).error)
    }

    override suspend fun getMembers(circleId: String): AppResult<List<CircleMember>> {
        requestedCircleId = circleId
        requestCount += 1
        return result
    }

    override suspend fun updateCachedMyMemberProfile(
        circleId: String,
        memberInfo: MemberInfo,
    ) = Unit
}

private fun defaultMembers(): List<CircleMember> = listOf(
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
    CircleMember(
        memberId = "member-3",
        nickname = "도윤",
        avatarColor = "#C784F7",
        profileImageUrl = null,
        isMe = false,
        role = "PENDING",
    ),
)
