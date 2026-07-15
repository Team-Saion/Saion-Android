package com.saion.core.domain.usecase.home

import com.saion.core.domain.repository.HomeRepository
import com.saion.core.model.home.HomeOverview
import com.saion.core.model.result.AppResult
import javax.inject.Inject

/**
 * 써클 홈 화면 구성을 조회합니다.
 *
 * 홈 카드, 구성원, 대표 일정에 필요한 데이터를 한 번에 불러올 때 사용합니다.
 */
class GetHomeUseCase @Inject constructor(
    private val homeRepository: HomeRepository,
) {
    suspend operator fun invoke(circleId: String): AppResult<HomeOverview> = homeRepository.getHome(circleId = circleId)
}
