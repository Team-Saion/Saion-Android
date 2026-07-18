package com.saion.core.domain.usecase.member

import com.saion.core.model.member.ProfileImageUpload
import com.saion.core.model.result.AppResult
import javax.inject.Inject

/**
 * 선택된 프로필 이미지가 있으면 먼저 업로드한 뒤 온보딩을 완료합니다.
 *
 * 두 단계 중 하나라도 실패하면 즉시 실패를 반환합니다.
 */
class CompleteOnboardingWithProfileImageUseCase @Inject constructor(
    private val uploadProfileImageUseCase: UploadProfileImageUseCase,
    private val completeOnboardingUseCase: CompleteOnboardingUseCase,
) {
    suspend operator fun invoke(
        nickname: String,
        profileImage: ProfileImageUpload?,
    ): AppResult<Unit> {
        if (profileImage != null) {
            when (val uploadResult = uploadProfileImageUseCase(profileImage)) {
                is AppResult.Failure -> return uploadResult
                is AppResult.Success -> Unit
            }
        }

        return completeOnboardingUseCase(nickname)
    }
}
