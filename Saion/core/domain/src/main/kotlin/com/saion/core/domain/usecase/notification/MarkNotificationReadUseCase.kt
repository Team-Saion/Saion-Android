package com.saion.core.domain.usecase.notification

import com.saion.core.domain.repository.NotificationRepository
import com.saion.core.model.notification.NotificationInboxItem
import com.saion.core.model.result.AppResult
import javax.inject.Inject

/**
 * 알림을 읽음 처리합니다.
 *
 * 성공하면 읽음 시각이 반영된 최신 알림 항목을 반환합니다.
 */
class MarkNotificationReadUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
) {
    suspend operator fun invoke(notificationId: Long): AppResult<NotificationInboxItem> =
        notificationRepository.markRead(notificationId = notificationId)
}
