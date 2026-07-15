package com.saion.core.domain.usecase.member

import com.saion.core.domain.repository.MemberRepository
import com.saion.core.model.member.MemberInfo
import com.saion.core.model.result.AppResult
import javax.inject.Inject

/**
 * 현재 멤버의 프로필 상태를 조회합니다.
 *
 * 상위 계층에는 역할과 상태처럼 앱이 실제로 사용하는 정보만 반환합니다.
 */
class GetMyInfoUseCase @Inject constructor(private val memberRepository: MemberRepository) {
    suspend operator fun invoke(): AppResult<MemberInfo> = memberRepository.getMyInfo()
}
