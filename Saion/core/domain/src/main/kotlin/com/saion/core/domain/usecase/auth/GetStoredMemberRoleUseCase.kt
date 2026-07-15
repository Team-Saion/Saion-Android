package com.saion.core.domain.usecase.auth

import com.saion.core.domain.repository.AuthRepository
import com.saion.core.model.member.MemberRole
import com.saion.core.model.result.AppResult
import javax.inject.Inject

/**
 * 로컬에 저장된 access token의 `roles` 클레임에서 현재 멤버 역할을 읽어옵니다.
 *
 * 네트워크 요청 없이 온보딩 분기나 세션 복원에 필요한 역할 정보를 확인할 때 사용합니다.
 */
class GetStoredMemberRoleUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): AppResult<MemberRole> = authRepository.getStoredMemberRole()
}
