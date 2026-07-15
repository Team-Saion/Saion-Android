package com.saion.core.domain.usecase.schedule

import com.saion.core.domain.repository.ScheduleRepository
import com.saion.core.model.result.AppResult
import com.saion.core.model.schedule.UpdateScheduleCommand
import javax.inject.Inject

/**
 * 기존 일정을 수정합니다.
 */
class UpdateScheduleUseCase @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
) {
    suspend operator fun invoke(
        circleId: String,
        scheduleId: String,
        command: UpdateScheduleCommand,
    ): AppResult<Unit> = scheduleRepository.updateSchedule(
        circleId = circleId,
        scheduleId = scheduleId,
        command = command,
    )
}
