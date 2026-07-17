package com.saion.feature.schedule.impl.ui

import com.saion.core.domain.repository.CurrentCircleRepository
import com.saion.core.domain.repository.ScheduleRepository
import com.saion.core.domain.usecase.circle.ObserveCurrentCircleUseCase
import com.saion.core.domain.usecase.circle.ObserveResolvedCurrentCircleUseCase
import com.saion.core.domain.usecase.circle.SyncCurrentCircleUseCase
import com.saion.core.domain.usecase.schedule.GetScheduleListUseCase
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import com.saion.core.model.schedule.ConfirmationOption
import com.saion.core.model.schedule.ConfirmationType
import com.saion.core.model.schedule.CreateScheduleCommand
import com.saion.core.model.schedule.CreatedSchedule
import com.saion.core.model.schedule.RegisteredConfirmation
import com.saion.core.model.schedule.ScheduleDetail
import com.saion.core.model.schedule.ScheduleListPage
import com.saion.core.model.schedule.ScheduleStatus
import com.saion.core.model.schedule.ScheduleSummary
import com.saion.core.model.schedule.UpdateScheduleCommand
import com.saion.feature.schedule.impl.viewmodel.ScheduleEffect
import com.saion.feature.schedule.impl.viewmodel.ScheduleIntent
import com.saion.feature.schedule.impl.viewmodel.ScheduleSnackbarMessage
import com.saion.feature.schedule.impl.viewmodel.ScheduleState
import com.saion.feature.schedule.impl.viewmodel.ScheduleViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
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
class ScheduleViewModelTest {
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
    fun `현재 써클 id로 첫 페이지를 조회한다`() = runTest {
        val currentCircleRepository = FakeCurrentCircleRepository(initialCircleId = "circle-1")
        val scheduleRepository = FakeScheduleRepository(
            results = listOf(AppResult.Success(schedulePage(schedules = listOf(defaultSchedule("schedule-1"))))),
        )

        ScheduleViewModel(
            observeResolvedCurrentCircleUseCase = observeResolvedCurrentCircleUseCase(currentCircleRepository),
            getScheduleListUseCase = GetScheduleListUseCase(scheduleRepository),
        )

        advanceUntilIdle()

        assertEquals(
            listOf(ScheduleListRequest(circleId = "circle-1", cursor = null, size = 20)),
            scheduleRepository.requests,
        )
    }

    @Test
    fun `현재 써클이 없으면 빈 content 상태가 된다`() = runTest {
        val viewModel = ScheduleViewModel(
            observeResolvedCurrentCircleUseCase = observeResolvedCurrentCircleUseCase(FakeCurrentCircleRepository(initialCircleId = null)),
            getScheduleListUseCase = GetScheduleListUseCase(FakeScheduleRepository()),
        )

        advanceUntilIdle()

        assertEquals(
            ScheduleState.Content(
                schedules = persistentListOf(),
                isRefreshing = false,
                isAppending = false,
                nextCursor = null,
                hasNext = false,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `첫 페이지 성공 시 목록과 커서가 반영된다`() = runTest {
        val firstPage = schedulePage(
            schedules = listOf(defaultSchedule("schedule-1"), defaultSchedule("schedule-2")),
            nextCursor = "next-1",
            hasNext = true,
        )
        val viewModel = ScheduleViewModel(
            observeResolvedCurrentCircleUseCase = observeResolvedCurrentCircleUseCase(FakeCurrentCircleRepository(initialCircleId = "circle-1")),
            getScheduleListUseCase = GetScheduleListUseCase(
                FakeScheduleRepository(results = listOf(AppResult.Success(firstPage))),
            ),
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value as ScheduleState.Content
        assertEquals(firstPage.schedules.toImmutableList(), state.schedules)
        assertEquals("next-1", state.nextCursor)
        assertTrue(state.hasNext)
        assertFalse(state.isRefreshing)
        assertFalse(state.isAppending)
    }

    @Test
    fun `첫 페이지가 비어 있어도 content 상태를 유지한다`() = runTest {
        val viewModel = ScheduleViewModel(
            observeResolvedCurrentCircleUseCase = observeResolvedCurrentCircleUseCase(FakeCurrentCircleRepository(initialCircleId = "circle-1")),
            getScheduleListUseCase = GetScheduleListUseCase(
                FakeScheduleRepository(results = listOf(AppResult.Success(schedulePage(schedules = emptyList())))),
            ),
        )

        advanceUntilIdle()

        assertEquals(
            ScheduleState.Content(
                schedules = persistentListOf(),
                isRefreshing = false,
                isAppending = false,
                nextCursor = null,
                hasNext = false,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `새로고침 시 첫 페이지를 다시 조회한다`() = runTest {
        val scheduleRepository = FakeScheduleRepository(
            results = listOf(
                AppResult.Success(schedulePage(schedules = listOf(defaultSchedule("schedule-1")), nextCursor = "next-1", hasNext = true)),
                AppResult.Success(schedulePage(schedules = listOf(defaultSchedule("schedule-2")), nextCursor = null, hasNext = false)),
            ),
        )
        val viewModel = ScheduleViewModel(
            observeResolvedCurrentCircleUseCase = observeResolvedCurrentCircleUseCase(FakeCurrentCircleRepository(initialCircleId = "circle-1")),
            getScheduleListUseCase = GetScheduleListUseCase(scheduleRepository),
        )

        advanceUntilIdle()
        viewModel.dispatch(ScheduleIntent.RefreshRequested)
        advanceUntilIdle()

        assertEquals(
            listOf(
                ScheduleListRequest(circleId = "circle-1", cursor = null, size = 20),
                ScheduleListRequest(circleId = "circle-1", cursor = null, size = 20),
            ),
            scheduleRepository.requests,
        )
        val state = viewModel.uiState.value as ScheduleState.Content
        assertEquals("schedule-2", state.schedules.single().scheduleId)
    }

    @Test
    fun `append 조건 충족 시 다음 페이지를 이어 붙인다`() = runTest {
        val scheduleRepository = FakeScheduleRepository(
            results = listOf(
                AppResult.Success(schedulePage(schedules = listOf(defaultSchedule("schedule-1")), nextCursor = "next-1", hasNext = true)),
                AppResult.Success(schedulePage(schedules = listOf(defaultSchedule("schedule-2")), nextCursor = null, hasNext = false)),
            ),
        )
        val viewModel = ScheduleViewModel(
            observeResolvedCurrentCircleUseCase = observeResolvedCurrentCircleUseCase(FakeCurrentCircleRepository(initialCircleId = "circle-1")),
            getScheduleListUseCase = GetScheduleListUseCase(scheduleRepository),
        )

        advanceUntilIdle()
        viewModel.dispatch(ScheduleIntent.LoadNextPageRequested)
        advanceUntilIdle()

        val state = viewModel.uiState.value as ScheduleState.Content
        assertEquals(listOf("schedule-1", "schedule-2"), state.schedules.map { it.scheduleId })
        assertFalse(state.hasNext)
        assertEquals(
            listOf(
                ScheduleListRequest(circleId = "circle-1", cursor = null, size = 20),
                ScheduleListRequest(circleId = "circle-1", cursor = "next-1", size = 20),
            ),
            scheduleRepository.requests,
        )
    }

    @Test
    fun `hasNext가 false면 append를 요청하지 않는다`() = runTest {
        val scheduleRepository = FakeScheduleRepository(
            results = listOf(AppResult.Success(schedulePage(schedules = listOf(defaultSchedule("schedule-1"))))),
        )
        val viewModel = ScheduleViewModel(
            observeResolvedCurrentCircleUseCase = observeResolvedCurrentCircleUseCase(FakeCurrentCircleRepository(initialCircleId = "circle-1")),
            getScheduleListUseCase = GetScheduleListUseCase(scheduleRepository),
        )

        advanceUntilIdle()
        viewModel.dispatch(ScheduleIntent.LoadNextPageRequested)
        advanceUntilIdle()

        assertEquals(1, scheduleRepository.requests.size)
    }

    @Test
    fun `첫 페이지 실패 시 오류 상태와 스낵바 effect를 보낸다`() = runTest {
        val viewModel = ScheduleViewModel(
            observeResolvedCurrentCircleUseCase = observeResolvedCurrentCircleUseCase(FakeCurrentCircleRepository(initialCircleId = "circle-1")),
            getScheduleListUseCase = GetScheduleListUseCase(
                FakeScheduleRepository(results = listOf(AppResult.Failure(AppError.NetworkUnavailable()))),
            ),
        )

        val effectDeferred = async { viewModel.uiEffect.first() }

        advanceUntilIdle()

        val effect = effectDeferred.await()
        assertEquals(ScheduleState.Error, viewModel.uiState.value)
        assertTrue(effect is ScheduleEffect.ShowSnackbar)
        assertTrue((effect as ScheduleEffect.ShowSnackbar).message is ScheduleSnackbarMessage.Error)
    }

    @Test
    fun `append 실패 시 기존 목록 유지와 스낵바 effect를 보낸다`() = runTest {
        val scheduleRepository = FakeScheduleRepository(
            results = listOf(
                AppResult.Success(schedulePage(schedules = listOf(defaultSchedule("schedule-1")), nextCursor = "next-1", hasNext = true)),
                AppResult.Failure(AppError.Timeout()),
            ),
        )
        val viewModel = ScheduleViewModel(
            observeResolvedCurrentCircleUseCase = observeResolvedCurrentCircleUseCase(FakeCurrentCircleRepository(initialCircleId = "circle-1")),
            getScheduleListUseCase = GetScheduleListUseCase(scheduleRepository),
        )

        advanceUntilIdle()
        val effectDeferred = async { viewModel.uiEffect.first() }
        viewModel.dispatch(ScheduleIntent.LoadNextPageRequested)
        advanceUntilIdle()

        val state = viewModel.uiState.value as ScheduleState.Content
        val effect = effectDeferred.await()
        assertEquals(listOf("schedule-1"), state.schedules.map { it.scheduleId })
        assertFalse(state.isAppending)
        assertTrue(effect is ScheduleEffect.ShowSnackbar)
    }

    @Test
    fun `현재 써클 변경 시 목록을 초기화하고 새 써클 기준 재조회한다`() = runTest {
        val currentCircleRepository = FakeCurrentCircleRepository(initialCircleId = "circle-1")
        val scheduleRepository = FakeScheduleRepository(
            results = listOf(
                AppResult.Success(schedulePage(schedules = listOf(defaultSchedule("schedule-1")))),
                AppResult.Success(schedulePage(schedules = listOf(defaultSchedule("schedule-2")))),
            ),
        )
        val viewModel = ScheduleViewModel(
            observeResolvedCurrentCircleUseCase = observeResolvedCurrentCircleUseCase(currentCircleRepository),
            getScheduleListUseCase = GetScheduleListUseCase(scheduleRepository),
        )

        advanceUntilIdle()
        currentCircleRepository.update("circle-2")
        advanceUntilIdle()

        assertEquals(
            listOf(
                ScheduleListRequest(circleId = "circle-1", cursor = null, size = 20),
                ScheduleListRequest(circleId = "circle-2", cursor = null, size = 20),
            ),
            scheduleRepository.requests,
        )
        val state = viewModel.uiState.value as ScheduleState.Content
        assertEquals("schedule-2", state.schedules.single().scheduleId)
    }
}

private fun observeResolvedCurrentCircleUseCase(repository: CurrentCircleRepository): ObserveResolvedCurrentCircleUseCase =
    ObserveResolvedCurrentCircleUseCase(
        observeCurrentCircleUseCase = ObserveCurrentCircleUseCase(repository),
        syncCurrentCircleUseCase = SyncCurrentCircleUseCase(repository),
    )

private class FakeCurrentCircleRepository(
    initialCircleId: String?,
    private val syncedCircleId: String? = initialCircleId,
) : CurrentCircleRepository {
    private val flow = MutableStateFlow(initialCircleId)

    fun update(circleId: String?) {
        flow.value = circleId
    }

    override fun observeCurrentCircleId(): StateFlow<String?> = flow

    override suspend fun getCurrentCircleId(): String? = flow.value

    override suspend fun selectCircle(circleId: String): AppResult<Unit> = AppResult.Success(Unit)

    override suspend fun syncCurrentCircle(): AppResult<String?> {
        flow.value = syncedCircleId
        return AppResult.Success(flow.value)
    }

    override suspend fun clearCurrentCircle() {
        flow.value = null
    }
}

private data class ScheduleListRequest(
    val circleId: String,
    val cursor: String?,
    val size: Int?,
)

private class FakeScheduleRepository(private val results: List<AppResult<ScheduleListPage>> = listOf(AppResult.Success(schedulePage()))) :
    ScheduleRepository {
    val requests = mutableListOf<ScheduleListRequest>()
    private var resultIndex = 0

    override suspend fun getScheduleList(
        circleId: String,
        cursor: String?,
        size: Int?,
    ): AppResult<ScheduleListPage> {
        requests += ScheduleListRequest(circleId = circleId, cursor = cursor, size = size)
        return results.getOrElse(resultIndex++) { results.last() }
    }

    override suspend fun createSchedule(
        circleId: String,
        command: CreateScheduleCommand,
    ): AppResult<CreatedSchedule> {
        throw UnsupportedOperationException()
    }

    override suspend fun getScheduleDetail(
        circleId: String,
        scheduleId: String,
    ): AppResult<ScheduleDetail> {
        throw UnsupportedOperationException()
    }

    override suspend fun updateSchedule(
        circleId: String,
        scheduleId: String,
        command: UpdateScheduleCommand,
    ): AppResult<Unit> {
        throw UnsupportedOperationException()
    }

    override suspend fun deleteSchedule(
        circleId: String,
        scheduleId: String,
    ): AppResult<Unit> {
        throw UnsupportedOperationException()
    }

    override suspend fun getConfirmationTypes(circleId: String): AppResult<List<ConfirmationOption>> {
        throw UnsupportedOperationException()
    }

    override suspend fun registerConfirmation(
        circleId: String,
        scheduleId: String,
        confirmationType: ConfirmationType,
    ): AppResult<RegisteredConfirmation> {
        throw UnsupportedOperationException()
    }

    override suspend fun cancelConfirmation(
        circleId: String,
        scheduleId: String,
        confirmationId: Long,
    ): AppResult<Unit> {
        throw UnsupportedOperationException()
    }
}

private fun schedulePage(
    schedules: List<ScheduleSummary> = emptyList(),
    nextCursor: String? = null,
    hasNext: Boolean = false,
): ScheduleListPage = ScheduleListPage(
    schedules = schedules,
    nextCursor = nextCursor,
    hasNext = hasNext,
)

private fun defaultSchedule(scheduleId: String): ScheduleSummary = ScheduleSummary(
    scheduleId = scheduleId,
    title = "일정 $scheduleId",
    startDate = "2026-07-18",
    endDate = "2026-07-18",
    startTime = "18:00",
    endTime = "20:00",
    isAllDay = false,
    needConfirm = false,
    status = ScheduleStatus.UPCOMING,
    progressRate = 0,
    dday = 1,
)
