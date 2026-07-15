package com.saion.core.domain.usecase.notification

import com.saion.core.domain.repository.NotificationRepository
import com.saion.core.model.notification.NotificationSetting
import com.saion.core.model.result.AppResult
import javax.inject.Inject

/**
 * 현재 알림 설정을 조회합니다.
 */
class GetNotificationSettingUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
) {
    suspend operator fun invoke(): AppResult<NotificationSetting> = notificationRepository.getSetting()
}
