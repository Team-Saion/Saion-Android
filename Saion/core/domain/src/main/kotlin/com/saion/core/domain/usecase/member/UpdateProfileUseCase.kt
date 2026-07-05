package com.saion.core.domain.usecase.member

import com.saion.core.domain.repository.MemberRepository
import com.saion.core.model.result.AppResult
import javax.inject.Inject

/**
 * 현재 로그인한 멤버의 닉네임을 수정합니다.
 */
class UpdateProfileUseCase @Inject constructor(private val memberRepository: MemberRepository) {
    suspend operator fun invoke(nickname: String): AppResult<Unit> = memberRepository.updateProfile(nickname = nickname)
}
