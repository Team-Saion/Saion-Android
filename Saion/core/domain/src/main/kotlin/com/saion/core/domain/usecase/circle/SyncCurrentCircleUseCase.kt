package com.saion.core.domain.usecase.circle

import com.saion.core.domain.repository.CircleRepository
import com.saion.core.domain.repository.CurrentCircleRepository
import com.saion.core.model.circle.CircleSummary
import com.saion.core.model.result.AppResult
import javax.inject.Inject

class SyncCurrentCircleUseCase @Inject constructor(
    private val currentCircleRepository: CurrentCircleRepository,
    private val circleRepository: CircleRepository,
) {
    suspend operator fun invoke(): AppResult<String?> = when (val result = getCirclesForResolution()) {
        is AppResult.Success -> {
            val resolvedCircleId = resolveCircleId(
                circles = result.data,
                selectedCircleId = currentCircleRepository.getCurrentCircleId(),
            )
            when {
                resolvedCircleId == null -> currentCircleRepository.clearCurrentCircle()
                resolvedCircleId != currentCircleRepository.getCurrentCircleId() -> currentCircleRepository.selectCircle(resolvedCircleId)
            }
            AppResult.Success(resolvedCircleId)
        }

        is AppResult.Failure -> result
    }

    private suspend fun getCirclesForResolution(): AppResult<List<CircleSummary>> = when (val cachedResult = circleRepository.listCircles()) {
        is AppResult.Success -> if (cachedResult.data.isNotEmpty()) cachedResult else circleRepository.refreshCircles()
        is AppResult.Failure -> circleRepository.refreshCircles()
    }
}

private fun resolveCircleId(
    circles: List<CircleSummary>,
    selectedCircleId: String?,
): String? {
    if (circles.isEmpty()) return null
    if (selectedCircleId != null && circles.any { it.circleId == selectedCircleId }) {
        return selectedCircleId
    }
    return circles.first().circleId
}
