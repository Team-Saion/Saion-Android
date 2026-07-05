package com.saion.core.domain.usecase.member

import com.saion.core.domain.repository.MemberRepository
import com.saion.core.model.result.AppResult
import javax.inject.Inject

/**
 * 현재 로그인한 멤버를 탈퇴 처리합니다.
 */
class WithdrawUseCase @Inject constructor(private val memberRepository: MemberRepository) {
    suspend operator fun invoke(): AppResult<Unit> = memberRepository.withdraw()
}
