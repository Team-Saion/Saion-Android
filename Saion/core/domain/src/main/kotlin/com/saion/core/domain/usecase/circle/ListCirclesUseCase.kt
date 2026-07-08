package com.saion.core.domain.usecase.circle

import com.saion.core.domain.repository.CircleRepository
import com.saion.core.model.circle.CircleSummary
import com.saion.core.model.result.AppResult
import javax.inject.Inject

class ListCirclesUseCase @Inject constructor(
    private val circleRepository: CircleRepository,
) {
    suspend operator fun invoke(): AppResult<List<CircleSummary>> = circleRepository.listCircles()
}
