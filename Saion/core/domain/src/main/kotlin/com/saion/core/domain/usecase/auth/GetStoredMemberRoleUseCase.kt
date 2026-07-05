package com.saion.core.domain.usecase.auth

import com.saion.core.domain.repository.AuthRepository
import com.saion.core.model.member.MemberRole
import com.saion.core.model.result.AppResult
import javax.inject.Inject

/**
 * 로컬에 저장된 access token에서 현재 멤버 역할을 읽어옵니다.
 */
class GetStoredMemberRoleUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): AppResult<MemberRole> = authRepository.getStoredMemberRole()
}
