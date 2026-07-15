package com.saion.core.domain.usecase.notification

import com.saion.core.domain.repository.NotificationRepository
import com.saion.core.model.notification.NotificationSetting
import com.saion.core.model.result.AppResult
import javax.inject.Inject

class UpdateNotificationSettingUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
) {
    suspend operator fun invoke(setting: NotificationSetting): AppResult<NotificationSetting> =
        notificationRepository.updateSetting(setting = setting)
}
