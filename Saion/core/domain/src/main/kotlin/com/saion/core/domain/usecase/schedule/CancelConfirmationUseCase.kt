package com.saion.core.domain.usecase.schedule

import com.saion.core.domain.repository.ScheduleRepository
import com.saion.core.model.result.AppResult
import javax.inject.Inject

class CancelConfirmationUseCase @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
) {
    suspend operator fun invoke(
        circleId: String,
        scheduleId: String,
        confirmationId: Long,
    ): AppResult<Unit> = scheduleRepository.cancelConfirmation(
        circleId = circleId,
        scheduleId = scheduleId,
        confirmationId = confirmationId,
    )
}
