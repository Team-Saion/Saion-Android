package com.saion.core.data.repository

import com.saion.core.datastore.datasource.NotificationSettingLocalDataSource
import com.saion.core.datastore.model.NotificationSettingCache
import com.saion.core.model.notification.NotificationSetting
import com.saion.core.model.result.AppResult
import com.saion.core.network.datasource.NotificationRemoteDataSource
import com.saion.core.network.datasource.NotificationSettingRemoteDataSource
import com.saion.core.network.model.common.ApiResponse
import com.saion.core.network.model.notification.NotificationInboxItemResponse
import com.saion.core.network.model.notification.NotificationInboxPageResponse
import com.saion.core.network.model.notification.NotificationSettingResponse
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationRepositoryImplTest {
    @Test
    fun `최초 알림 설정 조회는 원격 성공값을 반환하고 캐시를 저장한다`() = runBlocking {
        val localDataSource = MemoryNotificationSettingLocalDataSource()
        val remoteDataSource = FakeNotificationSettingRemoteDataSource(
            getSettingResponse = successResponse(
                NotificationSettingResponse(
                    d7Enabled = true,
                    d1Enabled = false,
                    ddayEnabled = true,
                    familyScheduleCheckEnabled = false,
                ),
            ),
        )
        val repository = createRepository(
            localDataSource = localDataSource,
            notificationSettingRemoteDataSource = remoteDataSource,
        )

        val result = repository.getSetting()

        assertEquals(
            AppResult.Success(
                NotificationSetting(
                    d7Enabled = true,
                    d1Enabled = false,
                    ddayEnabled = true,
                    familyScheduleCheckEnabled = false,
                ),
            ),
            result,
        )
        assertEquals(1, remoteDataSource.getSettingCallCount)
        assertEquals(
            NotificationSettingCache(
                hasValue = true,
                d7Enabled = true,
                d1Enabled = false,
                ddayEnabled = true,
                familyScheduleCheckEnabled = false,
            ),
            localDataSource.cache,
        )
    }

    @Test
    fun `최초 조회 후 재조회는 캐시만 사용한다`() = runBlocking {
        val localDataSource = MemoryNotificationSettingLocalDataSource()
        val remoteDataSource = FakeNotificationSettingRemoteDataSource(
            getSettingResponse = successResponse(
                NotificationSettingResponse(
                    d7Enabled = true,
                    d1Enabled = true,
                    ddayEnabled = false,
                    familyScheduleCheckEnabled = true,
                ),
            ),
        )
        val repository = createRepository(
            localDataSource = localDataSource,
            notificationSettingRemoteDataSource = remoteDataSource,
        )

        repository.getSetting()
        val second = repository.getSetting()

        assertEquals(
            AppResult.Success(
                NotificationSetting(
                    d7Enabled = true,
                    d1Enabled = true,
                    ddayEnabled = false,
                    familyScheduleCheckEnabled = true,
                ),
            ),
            second,
        )
        assertEquals(1, remoteDataSource.getSettingCallCount)
    }

    @Test
    fun `최초 원격 조회 실패 시 캐시가 있으면 캐시를 반환한다`() = runBlocking {
        val localDataSource = MemoryNotificationSettingLocalDataSource(
            cache = NotificationSettingCache(
                hasValue = true,
                d7Enabled = false,
                d1Enabled = true,
                ddayEnabled = false,
                familyScheduleCheckEnabled = true,
            ),
        )
        val remoteDataSource = FakeNotificationSettingRemoteDataSource(
            getSettingResponse = errorResponse<NotificationSettingResponse>(statusCode = 500, message = "server error"),
        )
        val repository = createRepository(
            localDataSource = localDataSource,
            notificationSettingRemoteDataSource = remoteDataSource,
        )

        val result = repository.getSetting()

        assertEquals(
            AppResult.Success(
                NotificationSetting(
                    d7Enabled = false,
                    d1Enabled = true,
                    ddayEnabled = false,
                    familyScheduleCheckEnabled = true,
                ),
            ),
            result,
        )
        assertEquals(1, remoteDataSource.getSettingCallCount)
    }

    @Test
    fun `알림 설정 저장 성공 시 서버 응답으로 캐시를 갱신한다`() = runBlocking {
        val localDataSource = MemoryNotificationSettingLocalDataSource()
        val remoteDataSource = FakeNotificationSettingRemoteDataSource(
            getSettingResponse = successResponse(defaultNotificationSettingResponse()),
            updateSettingResponse = successResponse(
                NotificationSettingResponse(
                    d7Enabled = false,
                    d1Enabled = false,
                    ddayEnabled = true,
                    familyScheduleCheckEnabled = true,
                ),
            ),
        )
        val repository = createRepository(
            localDataSource = localDataSource,
            notificationSettingRemoteDataSource = remoteDataSource,
        )

        val result = repository.updateSetting(
            NotificationSetting(
                d7Enabled = false,
                d1Enabled = false,
                ddayEnabled = true,
                familyScheduleCheckEnabled = true,
            ),
        )

        assertEquals(
            AppResult.Success(
                NotificationSetting(
                    d7Enabled = false,
                    d1Enabled = false,
                    ddayEnabled = true,
                    familyScheduleCheckEnabled = true,
                ),
            ),
            result,
        )
        assertEquals(
            NotificationSettingCache(
                hasValue = true,
                d7Enabled = false,
                d1Enabled = false,
                ddayEnabled = true,
                familyScheduleCheckEnabled = true,
            ),
            localDataSource.cache,
        )
        assertTrue(remoteDataSource.lastUpdatedSetting == result.dataOrNull())
    }
}

private fun createRepository(
    localDataSource: MemoryNotificationSettingLocalDataSource,
    notificationSettingRemoteDataSource: FakeNotificationSettingRemoteDataSource,
): NotificationRepositoryImpl = NotificationRepositoryImpl(
    notificationRemoteDataSource = FakeNotificationRemoteDataSource(),
    notificationSettingRemoteDataSource = notificationSettingRemoteDataSource,
    notificationSettingLocalDataSource = localDataSource,
)

private class MemoryNotificationSettingLocalDataSource(var cache: NotificationSettingCache? = null) : NotificationSettingLocalDataSource {
    override suspend fun getSetting(): NotificationSettingCache? = cache

    override suspend fun saveSetting(setting: NotificationSettingCache) {
        cache = setting
    }

    override suspend fun clearSetting() {
        cache = null
    }
}

private class FakeNotificationSettingRemoteDataSource(
    private val getSettingResponse: ApiResponse<NotificationSettingResponse>,
    private val updateSettingResponse: ApiResponse<NotificationSettingResponse> = getSettingResponse,
) : NotificationSettingRemoteDataSource {
    var getSettingCallCount: Int = 0
    var lastUpdatedSetting: NotificationSetting? = null

    override suspend fun getSetting(): ApiResponse<NotificationSettingResponse> {
        getSettingCallCount += 1
        return getSettingResponse
    }

    override suspend fun updateSetting(
        d7Enabled: Boolean,
        d1Enabled: Boolean,
        ddayEnabled: Boolean,
        familyScheduleCheckEnabled: Boolean,
    ): ApiResponse<NotificationSettingResponse> {
        lastUpdatedSetting = NotificationSetting(
            d7Enabled = d7Enabled,
            d1Enabled = d1Enabled,
            ddayEnabled = ddayEnabled,
            familyScheduleCheckEnabled = familyScheduleCheckEnabled,
        )
        return updateSettingResponse
    }
}

private class FakeNotificationRemoteDataSource : NotificationRemoteDataSource {
    override suspend fun getInbox(
        cursor: Long?,
        size: Int?,
    ): ApiResponse<NotificationInboxPageResponse> {
        throw UnsupportedOperationException("Not required for this test")
    }

    override suspend fun markRead(notificationId: Long): ApiResponse<NotificationInboxItemResponse> {
        throw UnsupportedOperationException("Not required for this test")
    }
}

private fun defaultNotificationSettingResponse(): NotificationSettingResponse = NotificationSettingResponse(
    d7Enabled = true,
    d1Enabled = true,
    ddayEnabled = false,
    familyScheduleCheckEnabled = true,
)

private fun <T> successResponse(data: T): ApiResponse<T> = ApiResponse(
    statusCode = 200,
    isSuccess = true,
    data = data,
    errorCode = null,
    message = null,
    timestamp = "2026-07-17T00:00:00",
)

private fun <T> errorResponse(
    statusCode: Int,
    message: String,
): ApiResponse<T> = ApiResponse(
    statusCode = statusCode,
    isSuccess = false,
    data = null,
    errorCode = null,
    message = message,
    timestamp = "2026-07-17T00:00:00",
)

private fun <T> AppResult<T>.dataOrNull(): T? = when (this) {
    is AppResult.Success -> data
    is AppResult.Failure -> null
}
