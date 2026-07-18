package com.saion.core.domain.usecase.home

import com.saion.core.domain.repository.HomeRepository
import com.saion.core.model.home.HomeOverview
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveHomeUseCase @Inject constructor(
    private val homeRepository: HomeRepository,
) {
    operator fun invoke(circleId: String): Flow<HomeOverview?> = homeRepository.observeHome(circleId = circleId)
}
