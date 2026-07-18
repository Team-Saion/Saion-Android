package com.saion.core.domain.usecase.circle

import com.saion.core.domain.repository.CircleRepository
import com.saion.core.domain.repository.CurrentCircleRepository
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import javax.inject.Inject

class SelectCurrentCircleUseCase @Inject constructor(
    private val currentCircleRepository: CurrentCircleRepository,
    private val circleRepository: CircleRepository,
) {
    suspend operator fun invoke(circleId: String): AppResult<Unit> = when (val result = circleRepository.listCircles()) {
        is AppResult.Success -> {
            val hasCircle = result.data.any { it.circleId == circleId }
            if (hasCircle) {
                currentCircleRepository.selectCircle(circleId)
            } else {
                AppResult.Failure(AppError.Unknown(message = "Selected circle is missing or invalid."))
            }
        }

        is AppResult.Failure -> result
    }
}
