package com.saion.core.domain.usecase.home

import com.saion.core.domain.repository.HomeRepository
import com.saion.core.model.home.CircleMember
import com.saion.core.model.result.AppResult
import javax.inject.Inject

class GetHomeMembersUseCase @Inject constructor(
    private val homeRepository: HomeRepository,
) {
    suspend operator fun invoke(circleId: String): AppResult<List<CircleMember>> = homeRepository.getMembers(circleId = circleId)
}
