package com.saion.core.domain.usecase.notification

import com.saion.core.domain.repository.NotificationRepository
import com.saion.core.model.notification.NotificationInboxPage
import com.saion.core.model.result.AppResult
import javax.inject.Inject

/**
 * 알림 보관함을 조회합니다.
 *
 * 커서 기반 페이지네이션 파라미터를 그대로 전달합니다.
 */
class GetNotificationInboxUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
) {
    suspend operator fun invoke(
        cursor: Long? = null,
        size: Int? = null,
    ): AppResult<NotificationInboxPage> = notificationRepository.getInbox(cursor = cursor, size = size)
}
