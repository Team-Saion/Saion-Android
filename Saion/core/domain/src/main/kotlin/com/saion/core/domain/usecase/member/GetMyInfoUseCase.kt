package com.saion.core.domain.usecase.member

import com.saion.core.domain.repository.MemberRepository
import com.saion.core.model.member.MemberInfo
import com.saion.core.model.result.AppResult
import javax.inject.Inject

class GetMyInfoUseCase @Inject constructor(private val memberRepository: MemberRepository) {
    suspend operator fun invoke(): AppResult<MemberInfo> = memberRepository.getMyInfo()
}
