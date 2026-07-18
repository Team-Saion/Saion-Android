package com.saion.core.domain.usecase.circle

import com.saion.core.domain.repository.CircleRepository
import com.saion.core.model.result.AppResult
import javax.inject.Inject

/**
 * 현재 멤버를 지정한 써클에서 탈퇴시킵니다.
 */
class LeaveCircleUseCase @Inject constructor(
    private val circleRepository: CircleRepository,
) {
    suspend operator fun invoke(circleId: String): AppResult<Unit> = circleRepository.leave(circleId = circleId)
}
