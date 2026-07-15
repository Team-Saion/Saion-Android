package com.saion.core.domain.usecase.member

import com.saion.core.domain.repository.MemberRepository
import com.saion.core.model.member.ProfileImageUpload
import com.saion.core.model.result.AppResult
import javax.inject.Inject

/**
 * 현재 멤버의 프로필 이미지를 업로드합니다.
 *
 * 멀티파트 전송 세부사항은 저장소와 network 계층에서 처리하고, 상위 계층은 업로드할 이미지 정보만 전달합니다.
 */
class UploadProfileImageUseCase @Inject constructor(private val memberRepository: MemberRepository) {
    suspend operator fun invoke(image: ProfileImageUpload): AppResult<Unit> = memberRepository.uploadProfileImage(image = image)
}
