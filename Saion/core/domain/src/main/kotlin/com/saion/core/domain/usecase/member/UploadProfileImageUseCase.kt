package com.saion.core.domain.usecase.member

import com.saion.core.domain.repository.MemberRepository
import com.saion.core.model.member.ProfileImageUpload
import com.saion.core.model.result.AppResult
import javax.inject.Inject

class UploadProfileImageUseCase @Inject constructor(private val memberRepository: MemberRepository) {
    suspend operator fun invoke(image: ProfileImageUpload): AppResult<Unit> = memberRepository.uploadProfileImage(image = image)
}
