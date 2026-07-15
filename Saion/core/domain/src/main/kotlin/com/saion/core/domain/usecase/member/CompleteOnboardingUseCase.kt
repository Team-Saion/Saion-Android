package com.saion.core.domain.usecase.member

import com.saion.core.domain.repository.MemberRepository
import com.saion.core.model.result.AppResult
import javax.inject.Inject

/**
 * 온보딩을 완료합니다.
 *
 * 서버가 새 토큰을 발급하면 저장소에서 세션을 갱신합니다.
 */
class CompleteOnboardingUseCase @Inject constructor(private val memberRepository: MemberRepository) {
    suspend operator fun invoke(nickname: String): AppResult<Unit> = memberRepository.completeOnboarding(nickname = nickname)
}
