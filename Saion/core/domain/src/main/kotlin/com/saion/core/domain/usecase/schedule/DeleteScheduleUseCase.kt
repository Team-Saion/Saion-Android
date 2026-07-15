package com.saion.core.domain.usecase.schedule

import com.saion.core.domain.repository.ScheduleRepository
import com.saion.core.model.result.AppResult
import javax.inject.Inject

/**
 * 일정을 삭제합니다.
 */
class DeleteScheduleUseCase @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
) {
    suspend operator fun invoke(
        circleId: String,
        scheduleId: String,
    ): AppResult<Unit> = scheduleRepository.deleteSchedule(
        circleId = circleId,
        scheduleId = scheduleId,
    )
}
