package com.saion.core.domain.usecase.schedule

import com.saion.core.domain.repository.ScheduleRepository
import com.saion.core.model.result.AppResult
import com.saion.core.model.schedule.CreateScheduleCommand
import com.saion.core.model.schedule.CreatedSchedule
import javax.inject.Inject

class CreateScheduleUseCase @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
) {
    suspend operator fun invoke(
        circleId: String,
        command: CreateScheduleCommand,
    ): AppResult<CreatedSchedule> = scheduleRepository.createSchedule(
        circleId = circleId,
        command = command,
    )
}
