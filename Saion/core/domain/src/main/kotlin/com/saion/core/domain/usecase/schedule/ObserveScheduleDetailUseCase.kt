package com.saion.core.domain.usecase.schedule

import com.saion.core.domain.repository.ScheduleRepository
import com.saion.core.model.schedule.ScheduleDetail
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveScheduleDetailUseCase @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
) {
    operator fun invoke(
        circleId: String,
        scheduleId: String,
    ): Flow<ScheduleDetail?> = scheduleRepository.observeScheduleDetail(
        circleId = circleId,
        scheduleId = scheduleId,
    )
}
