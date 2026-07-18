package com.saion.core.domain.usecase.schedule

import com.saion.core.domain.repository.ScheduleRepository
import com.saion.core.model.result.AppResult
import com.saion.core.model.schedule.ScheduleListPage
import javax.inject.Inject

class RefreshScheduleListUseCase @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
) {
    suspend operator fun invoke(
        circleId: String,
        cursor: String? = null,
        size: Int? = null,
    ): AppResult<ScheduleListPage> = scheduleRepository.refreshScheduleList(
        circleId = circleId,
        cursor = cursor,
        size = size,
    )
}
