package com.saion.core.domain.usecase.schedule

import com.saion.core.domain.repository.ScheduleRepository
import com.saion.core.model.result.AppResult
import com.saion.core.model.schedule.CreateScheduleCommand
import com.saion.core.model.schedule.CreatedSchedule
import javax.inject.Inject

/**
 * 새 일정을 생성합니다.
 *
 * 성공하면 생성된 일정 식별자를 포함한 결과를 반환합니다.
 */
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
