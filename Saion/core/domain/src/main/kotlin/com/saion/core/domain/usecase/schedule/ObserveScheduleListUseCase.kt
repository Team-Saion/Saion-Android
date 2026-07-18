package com.saion.core.domain.usecase.schedule

import com.saion.core.domain.repository.ScheduleRepository
import com.saion.core.model.schedule.ScheduleListPage
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveScheduleListUseCase @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
) {
    operator fun invoke(circleId: String): Flow<ScheduleListPage?> = scheduleRepository.observeScheduleList(circleId)
}
