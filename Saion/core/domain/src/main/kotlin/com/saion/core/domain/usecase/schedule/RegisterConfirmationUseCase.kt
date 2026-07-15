package com.saion.core.domain.usecase.schedule

import com.saion.core.domain.repository.ScheduleRepository
import com.saion.core.model.result.AppResult
import com.saion.core.model.schedule.ConfirmationType
import com.saion.core.model.schedule.RegisteredConfirmation
import javax.inject.Inject

/**
 * 일정 확인 응답을 등록하거나 변경합니다.
 */
class RegisterConfirmationUseCase @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
) {
    suspend operator fun invoke(
        circleId: String,
        scheduleId: String,
        confirmationType: ConfirmationType,
    ): AppResult<RegisteredConfirmation> = scheduleRepository.registerConfirmation(
        circleId = circleId,
        scheduleId = scheduleId,
        confirmationType = confirmationType,
    )
}
