package com.saion.feature.home.impl.notificationhistory

import com.saion.core.domain.repository.NotificationRepository
import com.saion.core.domain.usecase.notification.GetNotificationInboxUseCase
import com.saion.core.domain.usecase.notification.MarkNotificationReadUseCase
import com.saion.core.model.notification.NotificationInboxItem
import com.saion.core.model.notification.NotificationInboxPage
import com.saion.core.model.notification.NotificationRoute
import com.saion.core.model.notification.NotificationRouteType
import com.saion.core.model.notification.NotificationSetting
import com.saion.core.model.notification.NotificationType
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import com.saion.feature.home.impl.notificationhistory.viewmodel.NotificationHistoryEffect
import com.saion.feature.home.impl.notificationhistory.viewmodel.NotificationHistoryIntent
import com.saion.feature.home.impl.notificationhistory.viewmodel.NotificationHistorySnackbarMessage
import com.saion.feature.home.impl.notificationhistory.viewmodel.NotificationHistoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationHistoryViewModelTest {
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
    fun `초기 조회 성공 시 상태에 알림 목록이 반영된다`() = runTest {
        val inboxItem = defaultNotificationItem()
        val viewModel = createViewModel(
            repository = FakeNotificationRepository(
                inboxResult = AppResult.Success(
                    NotificationInboxPage(
                        items = listOf(inboxItem),
                        nextCursor = null,
                    ),
                ),
            ),
        )

        advanceUntilIdle()

        assertEquals(listOf(inboxItem), viewModel.uiState.value.items)
        assertTrue(viewModel.uiState.value.isLoading.not())
        assertTrue(viewModel.uiState.value.isRefreshing.not())
        assertTrue(viewModel.uiState.value.isLoadFailed.not())
    }

    @Test
    fun `초기 조회 실패 시 스낵바 effect를 보낸다`() = runTest {
        val viewModel = createViewModel(
            repository = FakeNotificationRepository(
                inboxResult = AppResult.Failure(AppError.NetworkUnavailable()),
            ),
        )
        val effectDeferred = async { viewModel.uiEffect.first() }

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isLoadFailed)
        val effect = effectDeferred.await()
        assertTrue(effect is NotificationHistoryEffect.ShowSnackbar)
        assertTrue((effect as NotificationHistoryEffect.ShowSnackbar).message is NotificationHistorySnackbarMessage.Error)
    }

    @Test
    fun `읽지 않은 홈 알림을 누르면 읽음 처리 후 홈 이동 effect를 보낸다`() = runTest {
        val item = defaultNotificationItem(routeType = NotificationRouteType.HOME)
        val updatedItem = item.copy(readAt = "2026-07-17T09:00:00")
        val repository = FakeNotificationRepository(
            inboxResult = AppResult.Success(NotificationInboxPage(items = listOf(item), nextCursor = null)),
            markReadResult = AppResult.Success(updatedItem),
        )
        val viewModel = createViewModel(repository)
        advanceUntilIdle()
        val effectDeferred = async { viewModel.uiEffect.first() }

        viewModel.dispatch(NotificationHistoryIntent.NotificationClicked(item))
        advanceUntilIdle()

        assertEquals(item.id, repository.markReadRequestedId)
        assertEquals(updatedItem, viewModel.uiState.value.items.single())
        assertEquals(NotificationHistoryEffect.NavigateToHome, effectDeferred.await())
    }

    @Test
    fun `스케줄 상세 알림은 읽음 처리 후 상세 이동 effect를 보낸다`() = runTest {
        val item = defaultNotificationItem(routeType = NotificationRouteType.SCHEDULE_DETAIL)
        val updatedItem = item.copy(readAt = "2026-07-17T09:00:00")
        val viewModel = createViewModel(
            repository = FakeNotificationRepository(
                inboxResult = AppResult.Success(NotificationInboxPage(items = listOf(item), nextCursor = null)),
                markReadResult = AppResult.Success(updatedItem),
            ),
        )
        advanceUntilIdle()
        val effectDeferred = async { viewModel.uiEffect.first() }

        viewModel.dispatch(NotificationHistoryIntent.NotificationClicked(item))
        advanceUntilIdle()

        assertEquals(NotificationHistoryEffect.NavigateToScheduleDetail(scheduleId = "schedule-1"), effectDeferred.await())
    }

    @Test
    fun `스케줄 상세 알림에 scheduleId가 없으면 스낵바 effect를 보낸다`() = runTest {
        val item = defaultNotificationItem(
            routeType = NotificationRouteType.SCHEDULE_DETAIL,
            scheduleId = null,
        )
        val updatedItem = item.copy(readAt = "2026-07-17T09:00:00")
        val viewModel = createViewModel(
            repository = FakeNotificationRepository(
                inboxResult = AppResult.Success(NotificationInboxPage(items = listOf(item), nextCursor = null)),
                markReadResult = AppResult.Success(updatedItem),
            ),
        )
        advanceUntilIdle()
        val effectDeferred = async { viewModel.uiEffect.first() }

        viewModel.dispatch(NotificationHistoryIntent.NotificationClicked(item))
        advanceUntilIdle()

        assertEquals(
            NotificationHistoryEffect.ShowSnackbar(
                NotificationHistorySnackbarMessage.Cautionary(
                    value = "",
                    defaultMessageResId = com.saion.feature.home.impl.R.string.notification_history_error_invalid_schedule_route,
                ),
            ),
            effectDeferred.await(),
        )
    }

    @Test
    fun `이미 읽은 알림은 읽음 처리 없이 바로 이동한다`() = runTest {
        val item = defaultNotificationItem(
            routeType = NotificationRouteType.SCHEDULE_LIST,
            readAt = "2026-07-17T09:00:00",
        )
        val repository = FakeNotificationRepository(
            inboxResult = AppResult.Success(NotificationInboxPage(items = listOf(item), nextCursor = null)),
        )
        val viewModel = createViewModel(repository)
        advanceUntilIdle()
        val effectDeferred = async { viewModel.uiEffect.first() }

        viewModel.dispatch(NotificationHistoryIntent.NotificationClicked(item))
        advanceUntilIdle()

        assertNull(repository.markReadRequestedId)
        assertEquals(NotificationHistoryEffect.NavigateToSchedule, effectDeferred.await())
    }

    @Test
    fun `읽음 처리 실패 시 목록은 유지되고 스낵바 effect를 보낸다`() = runTest {
        val item = defaultNotificationItem()
        val repository = FakeNotificationRepository(
            inboxResult = AppResult.Success(NotificationInboxPage(items = listOf(item), nextCursor = null)),
            markReadResult = AppResult.Failure(AppError.NetworkUnavailable()),
        )
        val viewModel = createViewModel(repository)
        advanceUntilIdle()
        val effectDeferred = async { viewModel.uiEffect.first() }

        viewModel.dispatch(NotificationHistoryIntent.NotificationClicked(item))
        advanceUntilIdle()

        assertEquals(listOf(item), viewModel.uiState.value.items)
        val effect = effectDeferred.await()
        assertTrue(effect is NotificationHistoryEffect.ShowSnackbar)
    }

    @Test
    fun `새로고침 요청 시 refresh 상태로 재조회한다`() = runTest {
        val inboxItem = defaultNotificationItem()
        val refreshGate = Job()
        val repository = FakeNotificationRepository(
            inboxResult = AppResult.Success(NotificationInboxPage(items = listOf(inboxItem), nextCursor = null)),
            onGetInbox = { callCount ->
                if (callCount == 2) {
                    refreshGate.join()
                }
            },
        )
        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        launch {
            viewModel.dispatch(NotificationHistoryIntent.RefreshRequested)
        }
        runCurrent()

        assertTrue(viewModel.uiState.value.isRefreshing)
        assertTrue(viewModel.uiState.value.isLoading.not())

        refreshGate.complete()
        advanceUntilIdle()

        assertEquals(2, repository.getInboxCallCount)
        assertTrue(viewModel.uiState.value.isRefreshing.not())
        assertTrue(viewModel.uiState.value.isLoading.not())
    }
}

private fun createViewModel(repository: FakeNotificationRepository): NotificationHistoryViewModel =
    NotificationHistoryViewModel(
        getNotificationInboxUseCase = GetNotificationInboxUseCase(repository),
        markNotificationReadUseCase = MarkNotificationReadUseCase(repository),
    )

private class FakeNotificationRepository(
    private val inboxResult: AppResult<NotificationInboxPage>,
    private val markReadResult: AppResult<NotificationInboxItem> = AppResult.Failure(AppError.Unknown()),
    private val onGetInbox: suspend (callCount: Int) -> Unit = {},
) : NotificationRepository {
    var markReadRequestedId: Long? = null
    var getInboxCallCount: Int = 0

    override suspend fun getInbox(
        cursor: Long?,
        size: Int?,
    ): AppResult<NotificationInboxPage> {
        getInboxCallCount += 1
        onGetInbox(getInboxCallCount)
        return inboxResult
    }

    override suspend fun markRead(notificationId: Long): AppResult<NotificationInboxItem> {
        markReadRequestedId = notificationId
        return markReadResult
    }

    override fun observeSetting(): Flow<NotificationSetting?> = flowOf(null)

    override suspend fun getSetting(): AppResult<NotificationSetting> {
        throw UnsupportedOperationException("Not required for this test")
    }

    override suspend fun refreshSetting(): AppResult<NotificationSetting> = getSetting()

    override suspend fun updateSetting(setting: NotificationSetting): AppResult<NotificationSetting> {
        throw UnsupportedOperationException("Not required for this test")
    }
}

private fun defaultNotificationItem(
    routeType: NotificationRouteType = NotificationRouteType.HOME,
    readAt: String? = null,
    scheduleId: String? = "schedule-1",
): NotificationInboxItem = NotificationInboxItem(
    id = 1L,
    type = NotificationType.SCHEDULE_CREATED,
    title = "오늘 가족 일정이 있어요",
    body = "'{일정 제목}', 확인하고 알려주세요.",
    occurredAt = "2026-07-17T08:00:00",
    readAt = readAt,
    route = NotificationRoute(
        type = routeType,
        circleId = "circle-1",
        scheduleId = scheduleId,
    ),
)
