package com.saion.core.domain.usecase.schedule

import com.saion.core.domain.repository.ScheduleRepository
import com.saion.core.model.result.AppResult
import javax.inject.Inject

/**
 * 일정 내용을 가족에게 다시 알립니다.
 */
class RequestFamilyNotificationUseCase @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
) {
    suspend operator fun invoke(
        circleId: String,
        scheduleId: String,
    ): AppResult<Unit> = scheduleRepository.requestFamilyNotification(
        circleId = circleId,
        scheduleId = scheduleId,
    )
}
