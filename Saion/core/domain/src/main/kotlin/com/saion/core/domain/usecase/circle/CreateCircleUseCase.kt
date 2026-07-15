package com.saion.core.domain.usecase.circle

import com.saion.core.domain.repository.CircleRepository
import com.saion.core.model.circle.CircleSummary
import com.saion.core.model.result.AppResult
import javax.inject.Inject

/**
 * 새 써클을 생성합니다.
 *
 * 생성에 성공하면 방장 정보가 반영된 써클 요약을 반환합니다.
 */
class CreateCircleUseCase @Inject constructor(
    private val circleRepository: CircleRepository,
) {
    suspend operator fun invoke(name: String): AppResult<CircleSummary> = circleRepository.createCircle(name = name)
}
