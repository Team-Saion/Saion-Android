package com.saion.core.domain.usecase.member

import com.saion.core.domain.repository.MemberRepository
import com.saion.core.model.member.ProfileImageUpload
import com.saion.core.model.result.AppResult
import javax.inject.Inject

/**
 * 현재 로그인한 멤버의 프로필 이미지를 업로드합니다.
 */
class UploadProfileImageUseCase @Inject constructor(private val memberRepository: MemberRepository) {
    suspend operator fun invoke(image: ProfileImageUpload): AppResult<Unit> = memberRepository.uploadProfileImage(image = image)
}
