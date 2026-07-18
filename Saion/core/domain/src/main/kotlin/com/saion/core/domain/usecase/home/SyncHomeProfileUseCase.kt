package com.saion.core.domain.usecase.home

import com.saion.core.domain.repository.CurrentCircleRepository
import com.saion.core.domain.repository.HomeRepository
import com.saion.core.domain.repository.MemberRepository
import com.saion.core.model.result.AppResult
import javax.inject.Inject

class SyncHomeProfileUseCase @Inject constructor(
    private val currentCircleRepository: CurrentCircleRepository,
    private val memberRepository: MemberRepository,
    private val homeRepository: HomeRepository,
) {
    suspend operator fun invoke(): AppResult<Unit> {
        val circleId = currentCircleRepository.getCurrentCircleId() ?: return AppResult.Success(Unit)
        return when (val myInfo = memberRepository.getMyInfo()) {
            is AppResult.Success -> {
                homeRepository.updateCachedMyMemberProfile(circleId = circleId, memberInfo = myInfo.data)
                AppResult.Success(Unit)
            }

            is AppResult.Failure -> myInfo
        }
    }
}
