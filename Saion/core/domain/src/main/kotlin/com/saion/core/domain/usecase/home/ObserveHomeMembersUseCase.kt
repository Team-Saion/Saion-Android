package com.saion.core.domain.usecase.home

import com.saion.core.domain.repository.HomeRepository
import com.saion.core.model.home.CircleMember
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveHomeMembersUseCase @Inject constructor(
    private val homeRepository: HomeRepository,
) {
    operator fun invoke(circleId: String): Flow<List<CircleMember>> = homeRepository.observeMembers(circleId = circleId)
}
