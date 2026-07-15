package com.saion.core.data.repository

import com.saion.core.data.util.safeRequest
import com.saion.core.domain.repository.NotificationRepository
import com.saion.core.model.notification.NotificationInboxItem
import com.saion.core.model.notification.NotificationInboxPage
import com.saion.core.model.notification.NotificationRoute
import com.saion.core.model.notification.NotificationRouteType
import com.saion.core.model.notification.NotificationSetting
import com.saion.core.model.notification.NotificationType
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import com.saion.core.network.datasource.NotificationRemoteDataSource
import com.saion.core.network.datasource.NotificationSettingRemoteDataSource
import com.saion.core.network.model.notification.NotificationInboxItemResponse
import com.saion.core.network.model.notification.NotificationInboxPageResponse
import com.saion.core.network.model.notification.NotificationRouteResponse
import com.saion.core.network.model.notification.NotificationSettingResponse
import javax.inject.Inject

internal class NotificationRepositoryImpl @Inject constructor(
    private val notificationRemoteDataSource: NotificationRemoteDataSource,
    private val notificationSettingRemoteDataSource: NotificationSettingRemoteDataSource,
) : NotificationRepository {
    override suspend fun getInbox(
        cursor: Long?,
        size: Int?,
    ): AppResult<NotificationInboxPage> = safeRequest(
        request = { notificationRemoteDataSource.getInbox(cursor = cursor, size = size) },
    ) { response ->
        response.toDomain()
    }

    override suspend fun markRead(notificationId: Long): AppResult<NotificationInboxItem> = safeRequest(
        request = { notificationRemoteDataSource.markRead(notificationId = notificationId) },
    ) { response ->
        response.toDomain()
    }

    override suspend fun getSetting(): AppResult<NotificationSetting> = safeRequest(
        request = { notificationSettingRemoteDataSource.getSetting() },
    ) { response ->
        AppResult.Success(response.toDomain())
    }

    override suspend fun updateSetting(setting: NotificationSetting): AppResult<NotificationSetting> = safeRequest(
        request = {
            notificationSettingRemoteDataSource.updateSetting(
                d7Enabled = setting.d7Enabled,
                d1Enabled = setting.d1Enabled,
                ddayEnabled = setting.ddayEnabled,
                familyScheduleCheckEnabled = setting.familyScheduleCheckEnabled,
            )
        },
    ) { response ->
        AppResult.Success(response.toDomain())
    }
}

private fun NotificationInboxPageResponse.toDomain(): AppResult<NotificationInboxPage> {
    val items = items.map { response ->
        response.toDomain().getOrElse { error ->
            return AppResult.Failure(error)
        }
    }
    return AppResult.Success(
        NotificationInboxPage(
            items = items,
            nextCursor = nextCursor,
        ),
    )
}

private fun NotificationInboxItemResponse.toDomain(): AppResult<NotificationInboxItem> {
    val notificationType = NotificationType.from(type)
        ?: return AppResult.Failure(
            AppError.Unknown(message = "Notification type is missing or invalid."),
        )
    val notificationRoute = route.toDomain()
        ?: return AppResult.Failure(
            AppError.Unknown(message = "Notification route type is missing or invalid."),
        )

    return AppResult.Success(
        NotificationInboxItem(
            id = id,
            type = notificationType,
            title = title,
            body = body,
            occurredAt = occurredAt,
            readAt = readAt,
            route = notificationRoute,
        ),
    )
}

private fun NotificationRouteResponse.toDomain(): NotificationRoute? = NotificationRouteType.from(type)
    ?.let { routeType ->
        NotificationRoute(
            type = routeType,
            circleId = circleId,
            scheduleId = scheduleId,
        )
    }

private fun NotificationSettingResponse.toDomain(): NotificationSetting = NotificationSetting(
    d7Enabled = d7Enabled,
    d1Enabled = d1Enabled,
    ddayEnabled = ddayEnabled,
    familyScheduleCheckEnabled = familyScheduleCheckEnabled,
)

private inline fun <T> AppResult<T>.getOrElse(onFailure: (AppError) -> Nothing): T = when (this) {
    is AppResult.Success -> data
    is AppResult.Failure -> onFailure(error)
}
