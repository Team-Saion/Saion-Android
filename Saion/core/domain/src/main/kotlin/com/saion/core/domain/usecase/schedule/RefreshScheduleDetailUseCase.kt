package com.saion.core.domain.usecase.schedule

import com.saion.core.domain.repository.ScheduleRepository
import com.saion.core.model.result.AppResult
import com.saion.core.model.schedule.ScheduleDetail
import javax.inject.Inject

class RefreshScheduleDetailUseCase @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
) {
    suspend operator fun invoke(
        circleId: String,
        scheduleId: String,
    ): AppResult<ScheduleDetail> = scheduleRepository.refreshScheduleDetail(
        circleId = circleId,
        scheduleId = scheduleId,
    )
}
