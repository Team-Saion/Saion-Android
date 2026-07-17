package com.saion.core.domain.usecase.member

import com.saion.core.domain.repository.MemberRepository
import com.saion.core.model.member.MemberInfo
import com.saion.core.model.member.MemberRole
import com.saion.core.model.member.MemberStatus
import com.saion.core.model.result.AppResult
import javax.inject.Inject

/**
 * 현재 멤버의 상태나 역할을 개발 편의용으로 변경합니다.
 */
class ChangeStateUseCase @Inject constructor(private val memberRepository: MemberRepository) {
    suspend operator fun invoke(
        status: MemberStatus? = null,
        role: MemberRole? = null,
    ): AppResult<MemberInfo> = memberRepository.changeState(
        status = status,
        role = role,
    )
}
