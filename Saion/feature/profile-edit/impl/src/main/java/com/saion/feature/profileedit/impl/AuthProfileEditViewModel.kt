package com.saion.feature.profileedit.impl

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.saion.core.domain.usecase.member.CompleteOnboardingWithProfileImageUseCase
import com.saion.core.domain.usecase.member.GetOnboardingInfoUseCase
import com.saion.core.domain.usecase.member.ValidateNicknameUseCase
import com.saion.core.model.member.NicknameValidationResult
import com.saion.core.model.member.OnboardingInfo
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
internal class AuthProfileEditViewModel @Inject constructor(
    private val getOnboardingInfoUseCase: GetOnboardingInfoUseCase,
    private val completeOnboardingWithProfileImageUseCase: CompleteOnboardingWithProfileImageUseCase,
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
            ProfileEditIntent.Load -> loadOnboardingInfo()
            ProfileEditIntent.SubmitClicked -> submitNickname()
        }
    }

    private fun loadOnboardingInfo() {
        launchSafely(
            onSuccess = ::applyOnboardingInfo,
            onFailure = { error ->
                emitEffect(
                    ProfileEditEffect.ShowSnackbar(
                        error.toDisplayMessage(R.string.profile_edit_error_load_auth),
                    ),
                )
            },
        ) {
            getOnboardingInfoUseCase()
        }
    }

    private fun applyOnboardingInfo(onboardingInfo: OnboardingInfo) {
        val initialNickname = onboardingInfo.socialNickname.orEmpty()
        update {
            copy(
                nickname = initialNickname,
                nicknamePlaceholder = initialNickname,
                validation = validateNicknameUseCase(initialNickname),
                profileImageUrl = onboardingInfo.socialProfileImageUrl,
                avatarColorHex = onboardingInfo.avatarColorHex.ifBlank { avatarColorHex },
            )
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

    private fun submitNickname() {
        val validation = validateNicknameUseCase(currentState.nickname)
        update { copy(validation = validation) }
        if (!validation.isValid) return

        launchSafely(
            onStart = { update { copy(isSubmitting = true) } },
            onSuccess = {
                update { copy(isSubmitting = false) }
                emitEffect(ProfileEditEffect.NavigateComplete)
            },
            onFailure = { error ->
                update { copy(isSubmitting = false) }
                emitEffect(
                    ProfileEditEffect.ShowSnackbar(
                        error.toSubmitMessage(
                            saveMessageResId = R.string.profile_edit_error_save_auth,
                            imageReadMessageResId = R.string.profile_edit_error_read_image,
                        ),
                    ),
                )
            },
            onFinally = { update { copy(isSubmitting = false) } },
        ) {
            when (val profileImageResult = readSelectedProfileImage()) {
                is AppResult.Failure -> profileImageResult
                is AppResult.Success -> completeOnboardingWithProfileImageUseCase(
                    nickname = validation.trimmedNickname,
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
