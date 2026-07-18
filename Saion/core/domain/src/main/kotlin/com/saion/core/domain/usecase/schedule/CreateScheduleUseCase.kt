package com.saion.core.domain.usecase.schedule

import com.saion.core.domain.repository.HomeRepository
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
    private val homeRepository: HomeRepository,
) {
    suspend operator fun invoke(
        circleId: String,
        command: CreateScheduleCommand,
    ): AppResult<CreatedSchedule> = when (
        val result = scheduleRepository.createSchedule(
            circleId = circleId,
            command = command,
        )
    ) {
        is AppResult.Failure -> result
        is AppResult.Success -> {
            homeRepository.refreshHome(circleId = circleId)
            result
        }
    }
}
