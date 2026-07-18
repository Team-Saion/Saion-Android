package com.saion.feature.mypage.impl.notificationsetting

import com.saion.core.domain.repository.NotificationRepository
import com.saion.core.domain.usecase.notification.GetNotificationSettingUseCase
import com.saion.core.domain.usecase.notification.UpdateNotificationSettingUseCase
import com.saion.core.model.notification.NotificationInboxItem
import com.saion.core.model.notification.NotificationInboxPage
import com.saion.core.model.notification.NotificationSetting
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import com.saion.feature.mypage.impl.notificationsetting.viewmodel.NotificationSettingsEffect
import com.saion.feature.mypage.impl.notificationsetting.viewmodel.NotificationSettingsIntent
import com.saion.feature.mypage.impl.notificationsetting.viewmodel.NotificationSettingsSnackbarMessage
import com.saion.feature.mypage.impl.notificationsetting.viewmodel.NotificationSettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationSettingsViewModelTest {
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
    fun `초기 조회 성공 시 상태에 알림 설정이 반영된다`() = runTest {
        val initialSetting = defaultNotificationSetting()
        val viewModel = createViewModel(
            repository = FakeNotificationRepository(
                getSettingResult = AppResult.Success(initialSetting),
            ),
        )

        advanceUntilIdle()

        assertEquals(initialSetting, viewModel.uiState.value.setting)
        assertTrue(viewModel.uiState.value.isLoading.not())
    }

    @Test
    fun `토글 변경은 300ms debounce 후 마지막 상태만 저장한다`() = runTest {
        val repository = FakeNotificationRepository(
            getSettingResult = AppResult.Success(defaultNotificationSetting()),
            updateSettingResult = AppResult.Success(
                NotificationSetting(
                    d7Enabled = false,
                    d1Enabled = false,
                    ddayEnabled = false,
                    familyScheduleCheckEnabled = true,
                ),
            ),
        )
        val viewModel = createViewModel(repository)

        advanceUntilIdle()

        viewModel.dispatch(
            NotificationSettingsIntent.UpdateSetting(
                NotificationSetting(
                    d7Enabled = false,
                    d1Enabled = true,
                    ddayEnabled = false,
                    familyScheduleCheckEnabled = true,
                ),
            ),
        )
        runCurrent()
        viewModel.dispatch(
            NotificationSettingsIntent.UpdateSetting(
                NotificationSetting(
                    d7Enabled = false,
                    d1Enabled = false,
                    ddayEnabled = false,
                    familyScheduleCheckEnabled = true,
                ),
            ),
        )
        runCurrent()

        advanceTimeBy(299)
        runCurrent()
        assertEquals(0, repository.updatedSettings.size)

        advanceTimeBy(1)
        advanceUntilIdle()

        assertEquals(1, repository.updatedSettings.size)
        assertEquals(
            NotificationSetting(
                d7Enabled = false,
                d1Enabled = false,
                ddayEnabled = false,
                familyScheduleCheckEnabled = true,
            ),
            repository.updatedSettings.single(),
        )
    }

    @Test
    fun `저장 실패 시 마지막 성공 상태로 롤백하고 스낵바 effect를 보낸다`() = runTest {
        val initialSetting = defaultNotificationSetting()
        val viewModel = createViewModel(
            repository = FakeNotificationRepository(
                getSettingResult = AppResult.Success(initialSetting),
                updateSettingResult = AppResult.Failure(AppError.NetworkUnavailable()),
            ),
        )
        advanceUntilIdle()
        val effectDeferred = async { viewModel.uiEffect.first() }

        viewModel.dispatch(
            NotificationSettingsIntent.UpdateSetting(
                initialSetting.copy(d7Enabled = false),
            ),
        )
        runCurrent()
        advanceTimeBy(300)
        advanceUntilIdle()

        assertEquals(initialSetting, viewModel.uiState.value.setting)
        val effect = effectDeferred.await()
        assertTrue(effect is NotificationSettingsEffect.ShowSnackbar)
        assertTrue((effect as NotificationSettingsEffect.ShowSnackbar).message is NotificationSettingsSnackbarMessage.Error)
    }
}

private fun createViewModel(repository: FakeNotificationRepository): NotificationSettingsViewModel =
    NotificationSettingsViewModel(
        getNotificationSettingUseCase = GetNotificationSettingUseCase(repository),
        updateNotificationSettingUseCase = UpdateNotificationSettingUseCase(repository),
    )

private class FakeNotificationRepository(
    private val getSettingResult: AppResult<NotificationSetting>,
    private val updateSettingResult: AppResult<NotificationSetting> = getSettingResult,
) : NotificationRepository {
    val updatedSettings: MutableList<NotificationSetting> = mutableListOf()

    override suspend fun getInbox(
        cursor: Long?,
        size: Int?,
    ): AppResult<NotificationInboxPage> {
        throw UnsupportedOperationException("Not required for this test")
    }

    override suspend fun markRead(notificationId: Long): AppResult<NotificationInboxItem> {
        throw UnsupportedOperationException("Not required for this test")
    }

    override fun observeSetting(): Flow<NotificationSetting?> = flowOf(null)

    override suspend fun getSetting(): AppResult<NotificationSetting> = getSettingResult

    override suspend fun refreshSetting(): AppResult<NotificationSetting> = getSetting()

    override suspend fun updateSetting(setting: NotificationSetting): AppResult<NotificationSetting> {
        updatedSettings += setting
        return updateSettingResult
    }
}

private fun defaultNotificationSetting(): NotificationSetting = NotificationSetting(
    d7Enabled = true,
    d1Enabled = true,
    ddayEnabled = false,
    familyScheduleCheckEnabled = true,
)
