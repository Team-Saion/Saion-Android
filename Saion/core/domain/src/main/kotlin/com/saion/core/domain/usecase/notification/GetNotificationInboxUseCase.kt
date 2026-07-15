package com.saion.core.domain.usecase.notification

import com.saion.core.domain.repository.NotificationRepository
import com.saion.core.model.notification.NotificationInboxPage
import com.saion.core.model.result.AppResult
import javax.inject.Inject

class GetNotificationInboxUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
) {
    suspend operator fun invoke(
        cursor: Long? = null,
        size: Int? = null,
    ): AppResult<NotificationInboxPage> = notificationRepository.getInbox(cursor = cursor, size = size)
}
