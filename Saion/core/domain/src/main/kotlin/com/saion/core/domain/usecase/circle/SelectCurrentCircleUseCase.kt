package com.saion.core.domain.usecase.circle

import com.saion.core.domain.repository.CurrentCircleRepository
import com.saion.core.model.result.AppResult
import javax.inject.Inject

class SelectCurrentCircleUseCase @Inject constructor(
    private val currentCircleRepository: CurrentCircleRepository,
) {
    suspend operator fun invoke(circleId: String): AppResult<Unit> = currentCircleRepository.selectCircle(circleId = circleId)
}
