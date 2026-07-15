package com.saion.core.domain.usecase.member

import com.saion.core.domain.repository.MemberRepository
import com.saion.core.model.result.AppResult
import javax.inject.Inject

/**
 * 현재 세션을 로그아웃합니다.
 *
 * 요청이 성공하면 로컬에 저장된 토큰도 함께 제거합니다.
 */
class LogoutUseCase @Inject constructor(private val memberRepository: MemberRepository) {
    suspend operator fun invoke(): AppResult<Unit> = memberRepository.logout()
}
