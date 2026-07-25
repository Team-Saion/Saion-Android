package com.saion.feature.schedule.impl.viewmodel

import com.saion.core.domain.repository.CircleRepository
import com.saion.core.domain.repository.CurrentCircleRepository
import com.saion.core.domain.repository.HomeRepository
import com.saion.core.domain.repository.MemberRepository
import com.saion.core.domain.repository.ScheduleRepository
import com.saion.core.domain.usecase.circle.ObserveCurrentCircleUseCase
import com.saion.core.domain.usecase.circle.ObserveResolvedCurrentCircleUseCase
import com.saion.core.domain.usecase.circle.SyncCurrentCircleUseCase
import com.saion.core.domain.usecase.member.GetMyInfoUseCase
import com.saion.core.domain.usecase.schedule.CancelConfirmationUseCase
import com.saion.core.domain.usecase.schedule.DeleteScheduleUseCase
import com.saion.core.domain.usecase.schedule.GetConfirmationTypesUseCase
import com.saion.core.domain.usecase.schedule.ObserveScheduleDetailUseCase
import com.saion.core.domain.usecase.schedule.RefreshScheduleDetailUseCase
import com.saion.core.domain.usecase.schedule.RegisterConfirmationUseCase
import com.saion.core.model.circle.CircleSummary
import com.saion.core.model.home.CircleMember
import com.saion.core.model.home.HomeOverview
import com.saion.core.model.member.MemberInfo
import com.saion.core.model.member.MemberRole
import com.saion.core.model.member.MemberStatus
import com.saion.core.model.member.OnboardingInfo
import com.saion.core.model.member.ProfileImageUpload
import com.saion.core.model.result.AppResult
import com.saion.core.model.schedule.ConfirmationCount
import com.saion.core.model.schedule.ConfirmationOption
import com.saion.core.model.schedule.ConfirmationType
import com.saion.core.model.schedule.CreateScheduleCommand
import com.saion.core.model.schedule.CreatedSchedule
import com.saion.core.model.schedule.MyConfirmation
import com.saion.core.model.schedule.RegisteredConfirmation
import com.saion.core.model.schedule.ScheduleDetail
import com.saion.core.model.schedule.ScheduleListPage
import com.saion.core.model.schedule.ScheduleStatus
import com.saion.core.model.schedule.UpdateScheduleCommand
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ScheduleDetailViewModelTest {
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
    fun `확인 칩 클릭 시 UI는 즉시 선택 상태와 카운트를 갱신한다`() = runTest {
        val scheduleRepository = FakeScheduleRepository(detail = defaultDetail())
        val viewModel = createViewModel(scheduleRepository = scheduleRepository)

        advanceUntilIdle()
        viewModel.dispatch(ScheduleDetailIntent.Load(scheduleId = "schedule-1"))
        advanceUntilIdle()

        viewModel.dispatch(ScheduleDetailIntent.ConfirmationClicked(ConfirmationType.CONFIRMED))
        runCurrent()

        val confirmationOption = requireNotNull(viewModel.uiState.value.confirmationOptions.firstOrNull())
        assertTrue(confirmationOption.isSelected)
        assertEquals(1, confirmationOption.count)
        assertTrue(scheduleRepository.registerRequests.isEmpty())
    }

    @Test
    fun `디바운스 마지막 상태가 서버 원본과 같으면 요청하지 않는다`() = runTest {
        val scheduleRepository = FakeScheduleRepository(detail = defaultDetail())
        val viewModel = createViewModel(scheduleRepository = scheduleRepository)

        advanceUntilIdle()
        viewModel.dispatch(ScheduleDetailIntent.Load(scheduleId = "schedule-1"))
        advanceUntilIdle()

        viewModel.dispatch(ScheduleDetailIntent.ConfirmationClicked(ConfirmationType.CONFIRMED))
        runCurrent()
        viewModel.dispatch(ScheduleDetailIntent.ConfirmationClicked(ConfirmationType.CONFIRMED))
        runCurrent()

        advanceTimeBy(300)
        advanceUntilIdle()

        assertTrue(scheduleRepository.registerRequests.isEmpty())
        assertTrue(scheduleRepository.cancelRequests.isEmpty())
        val confirmationOption = requireNotNull(viewModel.uiState.value.confirmationOptions.firstOrNull())
        assertFalse(confirmationOption.isSelected)
        assertEquals(0, confirmationOption.count)
    }

    @Test
    fun `디바운스 마지막 상태가 서버 원본과 다르면 마지막 상태만 등록한다`() = runTest {
        val scheduleRepository = FakeScheduleRepository(detail = defaultDetail())
        val viewModel = createViewModel(scheduleRepository = scheduleRepository)

        advanceUntilIdle()
        viewModel.dispatch(ScheduleDetailIntent.Load(scheduleId = "schedule-1"))
        advanceUntilIdle()

        viewModel.dispatch(ScheduleDetailIntent.ConfirmationClicked(ConfirmationType.CONFIRMED))
        runCurrent()

        advanceTimeBy(300)
        advanceUntilIdle()

        assertEquals(listOf(ConfirmationType.CONFIRMED), scheduleRepository.registerRequests)
        assertTrue(scheduleRepository.cancelRequests.isEmpty())
    }

    @Test
    fun `확인 요청 중에는 submitting 상태를 켜지 않는다`() = runTest {
        val scheduleRepository = FakeScheduleRepository(
            detail = defaultDetail(),
            registerGate = CompletableDeferred(),
        )
        val viewModel = createViewModel(scheduleRepository = scheduleRepository)

        advanceUntilIdle()
        viewModel.dispatch(ScheduleDetailIntent.Load(scheduleId = "schedule-1"))
        advanceUntilIdle()

        viewModel.dispatch(ScheduleDetailIntent.ConfirmationClicked(ConfirmationType.CONFIRMED))
        runCurrent()

        advanceTimeBy(300)
        runCurrent()

        assertFalse(viewModel.uiState.value.isSubmitting)
        assertEquals(listOf(ConfirmationType.CONFIRMED), scheduleRepository.registerRequests)

        scheduleRepository.registerGate?.complete(Unit)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isSubmitting)
    }

    @Test
    fun `이미 선택된 확인 칩 클릭은 300ms 뒤 취소 요청으로 보낸다`() = runTest {
        val scheduleRepository = FakeScheduleRepository(
            detail = defaultDetail(
                myConfirmation = MyConfirmation(
                    confirmationId = 7L,
                    confirmationType = ConfirmationType.CONFIRMED,
                ),
            ),
        )
        val viewModel = createViewModel(scheduleRepository = scheduleRepository)

        advanceUntilIdle()
        viewModel.dispatch(ScheduleDetailIntent.Load(scheduleId = "schedule-1"))
        advanceUntilIdle()

        viewModel.dispatch(ScheduleDetailIntent.ConfirmationClicked(ConfirmationType.CONFIRMED))
        runCurrent()
        advanceTimeBy(300)
        advanceUntilIdle()

        assertEquals(listOf(7L), scheduleRepository.cancelRequests)
        assertTrue(scheduleRepository.registerRequests.isEmpty())
    }

    @Test
    fun `확인 요청 실패 시 스낵바 effect를 보낸다`() = runTest {
        val scheduleRepository = FakeScheduleRepository(
            detail = defaultDetail(),
            registerResult = AppResult.Failure(TestAppError),
        )
        val viewModel = createViewModel(scheduleRepository = scheduleRepository)

        advanceUntilIdle()
        viewModel.dispatch(ScheduleDetailIntent.Load(scheduleId = "schedule-1"))
        advanceUntilIdle()
        val effectDeferred = async { viewModel.uiEffect.first() }

        viewModel.dispatch(ScheduleDetailIntent.ConfirmationClicked(ConfirmationType.CONFIRMED))
        runCurrent()
        advanceTimeBy(300)
        advanceUntilIdle()

        val effect = effectDeferred.await()
        assertTrue(effect is ScheduleDetailEffect.ShowSnackbar)
        assertTrue((effect as ScheduleDetailEffect.ShowSnackbar).message is ScheduleDetailSnackbarMessage.Error)
        assertFalse(viewModel.uiState.value.isSubmitting)
        val confirmationOption = requireNotNull(viewModel.uiState.value.confirmationOptions.firstOrNull())
        assertFalse(confirmationOption.isSelected)
        assertEquals(0, confirmationOption.count)
    }
}

private fun createViewModel(
    scheduleRepository: FakeScheduleRepository,
    currentCircleRepository: FakeCurrentCircleRepository = FakeCurrentCircleRepository(initialCircleId = "circle-1"),
    memberRepository: MemberRepository = FakeMemberRepository(),
): ScheduleDetailViewModel = ScheduleDetailViewModel(
    observeResolvedCurrentCircleUseCase = ObserveResolvedCurrentCircleUseCase(
        observeCurrentCircleUseCase = ObserveCurrentCircleUseCase(currentCircleRepository),
        syncCurrentCircleUseCase = SyncCurrentCircleUseCase(
            currentCircleRepository = currentCircleRepository,
            circleRepository = FakeCircleRepository(currentCircleRepository),
        ),
    ),
    observeScheduleDetailUseCase = ObserveScheduleDetailUseCase(scheduleRepository),
    refreshScheduleDetailUseCase = RefreshScheduleDetailUseCase(scheduleRepository),
    getConfirmationTypesUseCase = GetConfirmationTypesUseCase(scheduleRepository),
    registerConfirmationUseCase = RegisterConfirmationUseCase(scheduleRepository),
    cancelConfirmationUseCase = CancelConfirmationUseCase(scheduleRepository),
    deleteScheduleUseCase = DeleteScheduleUseCase(scheduleRepository, FakeHomeRepository()),
    getMyInfoUseCase = GetMyInfoUseCase(memberRepository),
)

private class FakeScheduleRepository(
    detail: ScheduleDetail,
    val registerGate: CompletableDeferred<Unit>? = null,
    private val registerResult: AppResult<RegisteredConfirmation> = AppResult.Success(
        RegisteredConfirmation(confirmationType = ConfirmationType.CONFIRMED),
    ),
    private val cancelResult: AppResult<Unit> = AppResult.Success(Unit),
) : ScheduleRepository {
    private val detailFlow = MutableStateFlow<ScheduleDetail?>(detail)

    val registerRequests = mutableListOf<ConfirmationType>()
    val cancelRequests = mutableListOf<Long>()

    override fun observeScheduleList(circleId: String): Flow<ScheduleListPage?> = flowOf(null)

    override fun observeScheduleDetail(
        circleId: String,
        scheduleId: String,
    ): Flow<ScheduleDetail?> = detailFlow

    override suspend fun getCachedScheduleList(circleId: String): ScheduleListPage? = null

    override suspend fun getScheduleList(
        circleId: String,
        cursor: String?,
        size: Int?,
    ): AppResult<ScheduleListPage> = error("Not required for this test")

    override suspend fun refreshScheduleList(
        circleId: String,
        cursor: String?,
        size: Int?,
    ): AppResult<ScheduleListPage> = error("Not required for this test")

    override suspend fun createSchedule(
        circleId: String,
        command: CreateScheduleCommand,
    ): AppResult<CreatedSchedule> = error("Not required for this test")

    override suspend fun getScheduleDetail(
        circleId: String,
        scheduleId: String,
    ): AppResult<ScheduleDetail> = AppResult.Success(requireNotNull(detailFlow.value))

    override suspend fun getCachedScheduleDetail(
        circleId: String,
        scheduleId: String,
    ): ScheduleDetail? = detailFlow.value

    override suspend fun refreshScheduleDetail(
        circleId: String,
        scheduleId: String,
    ): AppResult<ScheduleDetail> = AppResult.Success(requireNotNull(detailFlow.value))

    override suspend fun updateSchedule(
        circleId: String,
        scheduleId: String,
        command: UpdateScheduleCommand,
    ): AppResult<Unit> = error("Not required for this test")

    override suspend fun deleteSchedule(
        circleId: String,
        scheduleId: String,
    ): AppResult<Unit> = AppResult.Success(Unit)

    override suspend fun getConfirmationTypes(circleId: String): AppResult<List<ConfirmationOption>> = AppResult.Success(
        listOf(ConfirmationOption(value = ConfirmationType.CONFIRMED, label = "확인했어요")),
    )

    override suspend fun registerConfirmation(
        circleId: String,
        scheduleId: String,
        confirmationType: ConfirmationType,
    ): AppResult<RegisteredConfirmation> {
        registerRequests += confirmationType
        registerGate?.await()
        return registerResult
    }

    override suspend fun cancelConfirmation(
        circleId: String,
        scheduleId: String,
        confirmationId: Long,
    ): AppResult<Unit> {
        cancelRequests += confirmationId
        return cancelResult
    }

    override suspend fun requestFamilyNotification(
        circleId: String,
        scheduleId: String,
    ): AppResult<Unit> = error("Not required for this test")
}

private class FakeCurrentCircleRepository(
    initialCircleId: String?,
) : CurrentCircleRepository {
    private val flow = MutableStateFlow(initialCircleId)

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

private class FakeCircleRepository(
    private val currentCircleRepository: FakeCurrentCircleRepository,
) : CircleRepository {
    override fun observeCircles(): Flow<List<CircleSummary>> = flowOf(emptyList())

    override suspend fun listCircles(): AppResult<List<CircleSummary>> = AppResult.Success(
        currentCircleRepository.getCurrentCircleId()?.let {
            listOf(CircleSummary(circleId = it, name = "circle", ownerId = "member-1"))
        }.orEmpty(),
    )

    override suspend fun refreshCircles(): AppResult<List<CircleSummary>> = listCircles()

    override suspend fun createCircle(name: String): AppResult<CircleSummary> = error("Not required for this test")

    override suspend fun transferInitiator(
        circleId: String,
        targetMemberId: String,
    ): AppResult<CircleSummary> = error("Not required for this test")

    override suspend fun leave(circleId: String): AppResult<Unit> = error("Not required for this test")
}

private class FakeMemberRepository : MemberRepository {
    override fun observeMyInfo(): Flow<MemberInfo?> = flowOf(null)

    override suspend fun getMyInfo(): AppResult<MemberInfo> = AppResult.Success(
        MemberInfo(
            memberId = "member-1",
            nickname = "me",
            profileImageUrl = null,
            avatarColorHex = "#FFFFFF",
            role = MemberRole.MEMBER,
            status = MemberStatus.ACTIVE,
        ),
    )

    override suspend fun refreshMyInfo(): AppResult<MemberInfo> = getMyInfo()

    override suspend fun getOnboardingInfo(): AppResult<OnboardingInfo> = error("Not required for this test")

    override suspend fun completeOnboarding(nickname: String): AppResult<Unit> = error("Not required for this test")

    override suspend fun updateProfile(nickname: String): AppResult<Unit> = error("Not required for this test")

    override suspend fun changeState(
        status: MemberStatus?,
        role: MemberRole?,
    ): AppResult<MemberInfo> = error("Not required for this test")

    override suspend fun uploadProfileImage(image: ProfileImageUpload): AppResult<Unit> = error("Not required for this test")

    override suspend fun logout(): AppResult<Unit> = error("Not required for this test")

    override suspend fun withdraw(reason: String): AppResult<Unit> = error("Not required for this test")
}

private class FakeHomeRepository : HomeRepository {
    override fun observeHome(circleId: String): Flow<HomeOverview?> = flowOf(null)

    override fun observeMembers(circleId: String): Flow<List<CircleMember>> = flowOf(emptyList())

    override suspend fun getHome(circleId: String): AppResult<HomeOverview> = error("Not required for this test")

    override suspend fun refreshHome(circleId: String): AppResult<HomeOverview> = error("Not required for this test")

    override suspend fun getMembers(circleId: String): AppResult<List<CircleMember>> = error("Not required for this test")

    override suspend fun updateCachedMyMemberProfile(
        circleId: String,
        memberInfo: MemberInfo,
    ) = Unit
}

private fun defaultDetail(
    myConfirmation: MyConfirmation? = null,
): ScheduleDetail = ScheduleDetail(
    scheduleId = "schedule-1",
    title = "일정",
    startDate = "2026-07-25",
    endDate = "2026-07-25",
    startTime = "10:00",
    endTime = "11:00",
    isAllDay = false,
    needConfirm = true,
    status = ScheduleStatus.UPCOMING,
    progressRate = 0,
    dday = 0,
    memo = null,
    confirmations = listOf(ConfirmationCount(type = ConfirmationType.CONFIRMED, count = 0)),
    myConfirmation = myConfirmation,
    createdBy = "member-1",
    createdAt = "2026-07-25T09:00:00",
)

private val TestAppError = com.saion.core.model.result.AppError.NetworkUnavailable()
