package com.saion.feature.mypage.impl.notificationsetting

import com.saion.core.domain.repository.NotificationRepository
import com.saion.core.domain.usecase.notification.GetNotificationSettingUseCase
import com.saion.core.domain.usecase.notification.UpdateNotificationSettingUseCase
import com.saion.core.model.notification.NotificationInboxItem
import com.saion.core.model.notification.NotificationInboxPage
import com.saion.core.model.notification.NotificationSetting
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import com.saion.core.notification.NotificationLifecycleManager
import com.saion.feature.mypage.impl.R
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
                osPermissionGranted = true,
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
                osPermissionGranted = true,
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
                osPermissionGranted = true,
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

    @Test
    fun `권한이 없는 상태에서 off to on 변경은 저장하지 않고 권한 요청 effect를 보낸다`() = runTest {
        val initialSetting = defaultNotificationSetting().copy(d7Enabled = false)
        val viewModel = createViewModel(
            repository = FakeNotificationRepository(
                getSettingResult = AppResult.Success(initialSetting),
            ),
        )
        advanceUntilIdle()
        val effectDeferred = async { viewModel.uiEffect.first() }

        viewModel.dispatch(
            NotificationSettingsIntent.UpdateSetting(
                setting = initialSetting.copy(d7Enabled = true),
                osPermissionGranted = false,
            ),
        )
        advanceUntilIdle()

        assertEquals(NotificationSettingsEffect.RequestNotificationPermission, effectDeferred.await())
    }

    @Test
    fun `권한 허용 후에는 대기 중이던 설정을 저장하고 토큰을 재동기화한다`() = runTest {
        val initialSetting = defaultNotificationSetting().copy(d7Enabled = false)
        val repository = FakeNotificationRepository(
            getSettingResult = AppResult.Success(initialSetting),
            updateSettingResult = AppResult.Success(initialSetting.copy(d7Enabled = true)),
        )
        val notificationManager = FakeNotificationLifecycleManager()
        val viewModel = createViewModel(
            repository = repository,
            notificationLifecycleManager = notificationManager,
        )
        advanceUntilIdle()

        viewModel.dispatch(
            NotificationSettingsIntent.UpdateSetting(
                setting = initialSetting.copy(d7Enabled = true),
                osPermissionGranted = false,
            ),
        )
        runCurrent()
        viewModel.dispatch(
            NotificationSettingsIntent.NotificationPermissionResolved(
                granted = true,
                canRequestAgain = true,
            ),
        )
        runCurrent()
        advanceTimeBy(300)
        advanceUntilIdle()

        assertEquals(listOf(initialSetting.copy(d7Enabled = true)), repository.updatedSettings)
        assertEquals(1, notificationManager.syncOnNotificationPermissionGrantedCallCount)
    }

    @Test
    fun `권한 거부 시 설정을 저장하지 않고 안내 스낵바 effect를 보낸다`() = runTest {
        val initialSetting = defaultNotificationSetting().copy(d7Enabled = false)
        val repository = FakeNotificationRepository(
            getSettingResult = AppResult.Success(initialSetting),
        )
        val viewModel = createViewModel(repository)
        advanceUntilIdle()
        viewModel.dispatch(
            NotificationSettingsIntent.UpdateSetting(
                setting = initialSetting.copy(d7Enabled = true),
                osPermissionGranted = false,
            ),
        )
        runCurrent()
        val effectDeferred = async { viewModel.uiEffect.first { it is NotificationSettingsEffect.ShowSnackbar } }

        viewModel.dispatch(
            NotificationSettingsIntent.NotificationPermissionResolved(
                granted = false,
                canRequestAgain = true,
            ),
        )
        advanceUntilIdle()

        assertTrue(repository.updatedSettings.isEmpty())
        assertEquals(initialSetting, viewModel.uiState.value.setting)
        val effect = effectDeferred.await() as NotificationSettingsEffect.ShowSnackbar
        assertEquals(
            NotificationSettingsSnackbarMessage.Text(
                value = "",
                defaultMessageResId = R.string.notification_settings_error_permission_required,
            ),
            effect.message,
        )
    }

    @Test
    fun `권한을 더 이상 요청할 수 없으면 설정 이동 스낵바 effect를 보낸다`() = runTest {
        val initialSetting = defaultNotificationSetting().copy(d7Enabled = false)
        val repository = FakeNotificationRepository(
            getSettingResult = AppResult.Success(initialSetting),
        )
        val viewModel = createViewModel(repository)
        advanceUntilIdle()
        viewModel.dispatch(
            NotificationSettingsIntent.UpdateSetting(
                setting = initialSetting.copy(d7Enabled = true),
                osPermissionGranted = false,
            ),
        )
        runCurrent()
        val effectDeferred = async { viewModel.uiEffect.first { it is NotificationSettingsEffect.ShowSnackbar } }

        viewModel.dispatch(
            NotificationSettingsIntent.NotificationPermissionResolved(
                granted = false,
                canRequestAgain = false,
            ),
        )
        advanceUntilIdle()

        assertTrue(repository.updatedSettings.isEmpty())
        assertEquals(initialSetting, viewModel.uiState.value.setting)
        val effect = effectDeferred.await() as NotificationSettingsEffect.ShowSnackbar
        assertEquals(
            NotificationSettingsSnackbarMessage.PermissionPermanentlyDenied(
                defaultMessageResId = R.string.notification_settings_error_permission_permanently_denied,
            ),
            effect.message,
        )
    }
}

private fun createViewModel(
    repository: FakeNotificationRepository,
    notificationLifecycleManager: NotificationLifecycleManager = FakeNotificationLifecycleManager(),
): NotificationSettingsViewModel =
    NotificationSettingsViewModel(
        getNotificationSettingUseCase = GetNotificationSettingUseCase(repository),
        updateNotificationSettingUseCase = UpdateNotificationSettingUseCase(repository),
        notificationLifecycleManager = notificationLifecycleManager,
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

private class FakeNotificationLifecycleManager : NotificationLifecycleManager {
    var syncOnNotificationPermissionGrantedCallCount: Int = 0

    override suspend fun syncOnAppLaunchIfSignedIn() = error("Not required for this test")

    override suspend fun syncOnLoginSuccess() = error("Not required for this test")

    override suspend fun syncOnNotificationPermissionGranted() {
        syncOnNotificationPermissionGrantedCallCount += 1
    }

    override suspend fun syncOnNewToken(token: String) = error("Not required for this test")
}

private fun defaultNotificationSetting(): NotificationSetting = NotificationSetting(
    d7Enabled = true,
    d1Enabled = true,
    ddayEnabled = false,
    familyScheduleCheckEnabled = true,
)
