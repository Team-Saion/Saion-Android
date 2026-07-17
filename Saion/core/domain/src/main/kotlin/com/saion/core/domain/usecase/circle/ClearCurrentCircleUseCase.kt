package com.saion.core.domain.usecase.circle

import com.saion.core.domain.repository.CurrentCircleRepository
import javax.inject.Inject

class ClearCurrentCircleUseCase @Inject constructor(
    private val currentCircleRepository: CurrentCircleRepository,
) {
    suspend operator fun invoke() = currentCircleRepository.clearCurrentCircle()
}
