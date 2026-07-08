package com.saion.core.domain.usecase.schedule

import com.saion.core.domain.repository.ScheduleRepository
import com.saion.core.model.result.AppResult
import com.saion.core.model.schedule.ConfirmationOption
import javax.inject.Inject

class GetConfirmationTypesUseCase @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
) {
    suspend operator fun invoke(circleId: String): AppResult<List<ConfirmationOption>> =
        scheduleRepository.getConfirmationTypes(circleId = circleId)
}
