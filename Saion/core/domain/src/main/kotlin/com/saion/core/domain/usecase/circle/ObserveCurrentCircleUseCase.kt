package com.saion.core.domain.usecase.circle

import com.saion.core.domain.repository.CurrentCircleRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveCurrentCircleUseCase @Inject constructor(
    private val currentCircleRepository: CurrentCircleRepository,
) {
    operator fun invoke(): Flow<String?> = currentCircleRepository.observeCurrentCircleId()
}
