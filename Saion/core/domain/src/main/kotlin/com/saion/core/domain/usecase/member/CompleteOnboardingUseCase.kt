package com.saion.core.domain.usecase.member

import com.saion.core.domain.repository.MemberRepository
import com.saion.core.model.result.AppResult
import javax.inject.Inject

class CompleteOnboardingUseCase @Inject constructor(private val memberRepository: MemberRepository) {
    suspend operator fun invoke(nickname: String): AppResult<Unit> = memberRepository.completeOnboarding(nickname = nickname)
}
