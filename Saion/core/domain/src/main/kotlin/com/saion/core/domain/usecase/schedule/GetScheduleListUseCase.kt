package com.saion.core.domain.usecase.schedule

import com.saion.core.domain.repository.ScheduleRepository
import com.saion.core.model.result.AppResult
import com.saion.core.model.schedule.ScheduleListPage
import javax.inject.Inject

/**
 * 일정 목록을 조회합니다.
 *
 * 커서와 페이지 크기를 그대로 전달해 다음 페이지를 이어서 불러올 수 있습니다.
 */
class GetScheduleListUseCase @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
) {
    suspend operator fun invoke(
        circleId: String,
        cursor: String? = null,
        size: Int? = null,
    ): AppResult<ScheduleListPage> = scheduleRepository.getScheduleList(
        circleId = circleId,
        cursor = cursor,
        size = size,
    )
}
