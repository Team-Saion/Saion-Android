package com.saion.core.domain.usecase.notification

import com.saion.core.domain.repository.NotificationRepository
import com.saion.core.model.notification.NotificationInboxItem
import com.saion.core.model.notification.NotificationInboxPage
import com.saion.core.model.notification.NotificationRoute
import com.saion.core.model.notification.NotificationRouteType
import com.saion.core.model.notification.NotificationSetting
import com.saion.core.model.notification.NotificationType
import com.saion.core.model.result.AppResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class NotificationUseCasesTest {
    @Test
    fun `알림 보관함 조회는 cursor와 size를 그대로 저장소에 전달한다`() = runBlocking {
        val expected = AppResult.Success(defaultNotificationInboxPage())
        val repository = FakeNotificationRepository(inboxResult = expected)

        val actual = GetNotificationInboxUseCase(repository).invoke(cursor = 10L, size = 30)

        assertEquals(
            NotificationUseCaseOutcome(
                result = expected,
                call = NotificationRepositoryCall.GetInbox(cursor = 10L, size = 30),
            ),
            NotificationUseCaseOutcome(result = actual, call = repository.lastCall),
        )
    }

    @Test
    fun `알림 읽음 처리는 notificationId를 그대로 저장소에 전달한다`() = runBlocking {
        val expected = AppResult.Success(defaultNotificationInboxItem())
        val repository = FakeNotificationRepository(markReadResult = expected)

        val actual = MarkNotificationReadUseCase(repository).invoke(notificationId = 7L)

        assertEquals(
            NotificationUseCaseOutcome(
                result = expected,
                call = NotificationRepositoryCall.MarkRead(notificationId = 7L),
            ),
            NotificationUseCaseOutcome(result = actual, call = repository.lastCall),
        )
    }

    @Test
    fun `알림 설정 조회는 저장소 결과를 그대로 반환한다`() = runBlocking {
        val expected = AppResult.Success(defaultNotificationSetting())
        val repository = FakeNotificationRepository(getSettingResult = expected)

        val actual = GetNotificationSettingUseCase(repository).invoke()

        assertEquals(
            NotificationUseCaseOutcome(
                result = expected,
                call = NotificationRepositoryCall.GetSetting,
            ),
            NotificationUseCaseOutcome(result = actual, call = repository.lastCall),
        )
    }

    @Test
    fun `알림 설정 변경은 setting을 그대로 저장소에 전달한다`() = runBlocking {
        val setting = defaultNotificationSetting()
        val expected = AppResult.Success(setting)
        val repository = FakeNotificationRepository(updateSettingResult = expected)

        val actual = UpdateNotificationSettingUseCase(repository).invoke(setting = setting)

        assertEquals(
            NotificationUseCaseOutcome(
                result = expected,
                call = NotificationRepositoryCall.UpdateSetting(setting = setting),
            ),
            NotificationUseCaseOutcome(result = actual, call = repository.lastCall),
        )
    }
}

private data class NotificationUseCaseOutcome<T>(
    val result: AppResult<T>,
    val call: NotificationRepositoryCall?,
)

private sealed interface NotificationRepositoryCall {
    data class GetInbox(
        val cursor: Long?,
        val size: Int?,
    ) : NotificationRepositoryCall

    data class MarkRead(val notificationId: Long) : NotificationRepositoryCall

    data object GetSetting : NotificationRepositoryCall

    data class UpdateSetting(val setting: NotificationSetting) : NotificationRepositoryCall
}

private class FakeNotificationRepository(
    private val inboxResult: AppResult<NotificationInboxPage> = AppResult.Success(defaultNotificationInboxPage()),
    private val markReadResult: AppResult<NotificationInboxItem> = AppResult.Success(defaultNotificationInboxItem()),
    private val getSettingResult: AppResult<NotificationSetting> = AppResult.Success(defaultNotificationSetting()),
    private val updateSettingResult: AppResult<NotificationSetting> = AppResult.Success(defaultNotificationSetting()),
) : NotificationRepository {
    var lastCall: NotificationRepositoryCall? = null

    override suspend fun getInbox(
        cursor: Long?,
        size: Int?,
    ): AppResult<NotificationInboxPage> {
        lastCall = NotificationRepositoryCall.GetInbox(cursor = cursor, size = size)
        return inboxResult
    }

    override suspend fun markRead(notificationId: Long): AppResult<NotificationInboxItem> {
        lastCall = NotificationRepositoryCall.MarkRead(notificationId = notificationId)
        return markReadResult
    }

    override fun observeSetting(): Flow<NotificationSetting?> = flowOf(null)

    override suspend fun getSetting(): AppResult<NotificationSetting> {
        lastCall = NotificationRepositoryCall.GetSetting
        return getSettingResult
    }

    override suspend fun refreshSetting(): AppResult<NotificationSetting> = getSetting()

    override suspend fun updateSetting(setting: NotificationSetting): AppResult<NotificationSetting> {
        lastCall = NotificationRepositoryCall.UpdateSetting(setting = setting)
        return updateSettingResult
    }
}

private fun defaultNotificationInboxPage(): NotificationInboxPage = NotificationInboxPage(
    items = listOf(defaultNotificationInboxItem()),
    nextCursor = 11L,
)

private fun defaultNotificationInboxItem(): NotificationInboxItem = NotificationInboxItem(
    id = 1L,
    type = NotificationType.SCHEDULE_CREATED,
    title = "새 일정이 등록됐어요",
    body = "민수님이 일정을 추가했어요.",
    occurredAt = "2026-07-15T09:00:00",
    readAt = null,
    route = NotificationRoute(
        type = NotificationRouteType.SCHEDULE_DETAIL,
        circleId = "circle-1",
        scheduleId = "schedule-1",
    ),
)

private fun defaultNotificationSetting(): NotificationSetting = NotificationSetting(
    d7Enabled = true,
    d1Enabled = true,
    ddayEnabled = false,
    familyScheduleCheckEnabled = true,
)
