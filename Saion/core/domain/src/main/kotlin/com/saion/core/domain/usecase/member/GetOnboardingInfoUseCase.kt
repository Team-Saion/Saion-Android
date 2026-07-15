package com.saion.core.domain.usecase.member

import com.saion.core.domain.repository.MemberRepository
import com.saion.core.model.member.OnboardingInfo
import com.saion.core.model.result.AppResult
import javax.inject.Inject

/**
 * 온보딩 사전정보를 조회합니다.
 *
 * 소셜 닉네임, 프로필 이미지 URL, 기본 아바타 색상을 불러올 때 사용합니다.
 */
class GetOnboardingInfoUseCase @Inject constructor(private val memberRepository: MemberRepository) {
    suspend operator fun invoke(): AppResult<OnboardingInfo> = memberRepository.getOnboardingInfo()
}
