package com.saion.core.domain.usecase.member

import com.saion.core.domain.repository.MemberRepository
import com.saion.core.model.result.AppResult
import javax.inject.Inject

/**
 * 현재 멤버를 탈퇴 처리합니다.
 *
 * 요청이 성공하면 로컬 세션도 함께 정리합니다.
 */
class WithdrawUseCase @Inject constructor(private val memberRepository: MemberRepository) {
    suspend operator fun invoke(reason: String = ""): AppResult<Unit> = memberRepository.withdraw(reason = reason)
}
