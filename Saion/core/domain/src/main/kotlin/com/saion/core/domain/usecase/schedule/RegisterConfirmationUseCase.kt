package com.saion.core.domain.usecase.schedule

import com.saion.core.domain.repository.ScheduleRepository
import com.saion.core.model.result.AppResult
import com.saion.core.model.schedule.ConfirmationType
import com.saion.core.model.schedule.RegisteredConfirmation
import javax.inject.Inject

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
