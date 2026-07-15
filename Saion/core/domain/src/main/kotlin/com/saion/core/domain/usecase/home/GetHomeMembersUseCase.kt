package com.saion.core.domain.usecase.home

import com.saion.core.domain.repository.HomeRepository
import com.saion.core.model.home.CircleMember
import com.saion.core.model.result.AppResult
import javax.inject.Inject

/**
 * 써클 구성원 목록을 조회합니다.
 */
class GetHomeMembersUseCase @Inject constructor(
    private val homeRepository: HomeRepository,
) {
    suspend operator fun invoke(circleId: String): AppResult<List<CircleMember>> = homeRepository.getMembers(circleId = circleId)
}
