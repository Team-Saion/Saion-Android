package com.saion.feature.profileedit.impl

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.saion.core.domain.usecase.member.GetMyInfoUseCase
import com.saion.core.domain.usecase.member.UpdateMyProfileWithProfileImageUseCase
import com.saion.core.domain.usecase.member.ValidateNicknameUseCase
import com.saion.core.model.member.NicknameValidationResult
import com.saion.core.model.member.ProfileImageUpload
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import com.saion.core.ui.error.toSnackbarMessage
import com.saion.core.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
@Stable
internal class MyPageProfileEditViewModel @Inject constructor(
    private val getMyInfoUseCase: GetMyInfoUseCase,
    private val updateMyProfileWithProfileImageUseCase: UpdateMyProfileWithProfileImageUseCase,
    private val validateNicknameUseCase: ValidateNicknameUseCase,
    private val profileImageReader: ProfileImageReader,
) : BaseViewModel<ProfileEditUiState, ProfileEditEffect, ProfileEditIntent>(ProfileEditUiState()) {
    init {
        dispatch(ProfileEditIntent.Load)
    }

    override fun handleIntent(intent: ProfileEditIntent) {
        when (intent) {
            ProfileEditIntent.BackClicked -> viewModelScope.launch { emitEffect(ProfileEditEffect.NavigateBack) }
            is ProfileEditIntent.ProfileImageSelected -> updateSelectedProfileImage(intent.imageUri)
            is ProfileEditIntent.NicknameChanged -> updateNickname(intent.nickname)
            ProfileEditIntent.Load -> loadMyInfo()
            ProfileEditIntent.SubmitClicked -> submitProfile()
        }
    }

    private fun loadMyInfo() {
        launchSafely(
            onSuccess = { memberInfo ->
                update {
                    copy(
                        initialNickname = memberInfo.nickname,
                        nickname = memberInfo.nickname,
                        nicknamePlaceholder = memberInfo.nickname,
                        validation = validateNicknameUseCase(memberInfo.nickname),
                        profileImageUrl = memberInfo.profileImageUrl,
                        avatarColorHex = memberInfo.avatarColorHex,
                    )
                }
            },
            onFailure = { error ->
                emitEffect(
                    ProfileEditEffect.ShowSnackbar(
                        error.toDisplayMessage(R.string.profile_edit_error_load_mypage),
                    ),
                )
            },
        ) {
            getMyInfoUseCase()
        }
    }

    private fun updateNickname(nickname: String) {
        val validation = validateNicknameUseCase(nickname)
        update {
            copy(
                nickname = if (validation.result != NicknameValidationResult.TooLong) nickname else this.nickname,
                validation = validation,
            )
        }
    }

    private fun updateSelectedProfileImage(imageUri: String?) {
        update { copy(selectedProfileImageUri = imageUri) }
    }

    private fun submitProfile() {
        val validation = validateNicknameUseCase(currentState.nickname)
        update { copy(validation = validation) }
        if (!validation.isValid) return

        launchSafely(
            onStart = { update { copy(isSubmitting = true) } },
            onSuccess = {
                update { copy(isSubmitting = false) }
                emitEffect(ProfileEditEffect.NavigateBack)
            },
            onFailure = { error ->
                update { copy(isSubmitting = false) }
                emitEffect(
                    ProfileEditEffect.ShowSnackbar(
                        error.toSubmitMessage(
                            saveMessageResId = R.string.profile_edit_error_save_mypage,
                            imageReadMessageResId = R.string.profile_edit_error_read_image,
                        ),
                    ),
                )
            },
            onFinally = { update { copy(isSubmitting = false) } },
        ) {
            val nicknameToUpdate = validation.trimmedNickname.takeIf { it != currentState.initialNickname.trim() }
            val profileImageResult = if (currentState.selectedProfileImageUri != null) {
                readSelectedProfileImage()
            } else {
                AppResult.Success(null)
            }

            when (profileImageResult) {
                is AppResult.Failure -> profileImageResult
                is AppResult.Success -> updateMyProfileWithProfileImageUseCase(
                    nickname = nicknameToUpdate,
                    profileImage = profileImageResult.data,
                )
            }
        }
    }

    private suspend fun readSelectedProfileImage(): AppResult<ProfileImageUpload?> =
        currentState.selectedProfileImageUri?.let { profileImageReader.read(it) } ?: AppResult.Success(null)
}

private fun AppError.toDisplayMessage(defaultMessageResId: Int): ProfileEditSnackbarMessage = toSnackbarMessage(
    defaultMessageResId = defaultMessageResId,
    textMessage = { value, resId -> ProfileEditSnackbarMessage.Text(value, resId) },
    errorMessage = { error, resId -> ProfileEditSnackbarMessage.Error(error, resId) },
)

private fun AppError.toSubmitMessage(
    saveMessageResId: Int,
    imageReadMessageResId: Int,
): ProfileEditSnackbarMessage = if (this is AppError.Unknown && message == PROFILE_IMAGE_READ_ERROR_MESSAGE) {
    ProfileEditSnackbarMessage.Text(value = "", defaultMessageResId = imageReadMessageResId)
} else {
    toDisplayMessage(defaultMessageResId = saveMessageResId)
}
