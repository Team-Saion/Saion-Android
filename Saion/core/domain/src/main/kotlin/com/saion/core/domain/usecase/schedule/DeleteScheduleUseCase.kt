package com.saion.core.domain.usecase.schedule

import com.saion.core.domain.repository.HomeRepository
import com.saion.core.domain.repository.ScheduleRepository
import com.saion.core.model.result.AppResult
import javax.inject.Inject

/**
 * 일정을 삭제합니다.
 */
class DeleteScheduleUseCase @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val homeRepository: HomeRepository,
) {
    suspend operator fun invoke(
        circleId: String,
        scheduleId: String,
    ): AppResult<Unit> = when (
        val result = scheduleRepository.deleteSchedule(
            circleId = circleId,
            scheduleId = scheduleId,
        )
    ) {
        is AppResult.Failure -> result
        is AppResult.Success -> {
            homeRepository.refreshHome(circleId = circleId)
            result
        }
    }
}
