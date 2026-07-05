package com.saion.core.domain.usecase.member

import com.saion.core.domain.repository.MemberRepository
import com.saion.core.model.member.MemberInfo
import com.saion.core.model.result.AppResult
import javax.inject.Inject

/**
 * 현재 로그인한 멤버의 프로필을 조회합니다.
 */
class GetMyInfoUseCase @Inject constructor(private val memberRepository: MemberRepository) {
    suspend operator fun invoke(): AppResult<MemberInfo> = memberRepository.getMyInfo()
}
