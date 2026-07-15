package com.saion.core.domain.usecase.circle

import com.saion.core.domain.repository.CircleRepository
import com.saion.core.model.circle.CircleSummary
import com.saion.core.model.result.AppResult
import javax.inject.Inject

/**
 * 써클 방장 권한을 다른 구성원에게 위임합니다.
 *
 * 성공하면 위임 결과가 반영된 최신 써클 요약을 반환합니다.
 */
class TransferCircleInitiatorUseCase @Inject constructor(
    private val circleRepository: CircleRepository,
) {
    suspend operator fun invoke(
        circleId: String,
        targetMemberId: String,
    ): AppResult<CircleSummary> = circleRepository.transferInitiator(
        circleId = circleId,
        targetMemberId = targetMemberId,
    )
}
