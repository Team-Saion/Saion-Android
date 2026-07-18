package com.saion.core.domain.usecase.member

import com.saion.core.model.member.ProfileImageUpload
import com.saion.core.model.result.AppResult
import com.saion.core.domain.usecase.home.SyncHomeProfileUseCase
import javax.inject.Inject

/**
 * 전달된 프로필 변경사항만 순서대로 반영합니다.
 *
 * 두 단계 중 하나라도 실패하면 즉시 실패를 반환합니다.
 */
class UpdateMyProfileWithProfileImageUseCase @Inject constructor(
    private val uploadProfileImageUseCase: UploadProfileImageUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val syncHomeProfileUseCase: SyncHomeProfileUseCase,
) {
    suspend operator fun invoke(
        nickname: String?,
        profileImage: ProfileImageUpload?,
    ): AppResult<Unit> {
        if (profileImage != null) {
            when (val uploadResult = uploadProfileImageUseCase(profileImage)) {
                is AppResult.Failure -> return uploadResult
                is AppResult.Success -> Unit
            }
        }

        val result = if (nickname != null) {
            updateProfileUseCase(nickname)
        } else {
            AppResult.Success(Unit)
        }

        return when (result) {
            is AppResult.Success -> syncHomeProfileUseCase()
            is AppResult.Failure -> result
        }
    }
}
