package com.saion.core.domain.usecase.circle

import com.saion.core.domain.repository.CurrentCircleRepository
import com.saion.core.model.result.AppResult
import javax.inject.Inject

class SyncCurrentCircleUseCase @Inject constructor(
    private val currentCircleRepository: CurrentCircleRepository,
) {
    suspend operator fun invoke(): AppResult<String?> = currentCircleRepository.syncCurrentCircle()
}
