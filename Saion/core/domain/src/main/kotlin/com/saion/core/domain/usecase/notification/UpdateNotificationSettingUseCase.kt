package com.saion.core.domain.usecase.notification

import com.saion.core.domain.repository.NotificationRepository
import com.saion.core.model.notification.NotificationSetting
import com.saion.core.model.result.AppResult
import javax.inject.Inject

/**
 * 알림 설정을 변경합니다.
 *
 * 저장된 최종 설정값을 다시 반환하므로 호출자는 서버 기준 상태를 그대로 사용할 수 있습니다.
 */
class UpdateNotificationSettingUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
) {
    suspend operator fun invoke(setting: NotificationSetting): AppResult<NotificationSetting> =
        notificationRepository.updateSetting(setting = setting)
}
