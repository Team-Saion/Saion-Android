package com.saion.feature.auth.impl.nickname.viewmodel

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.saion.core.domain.usecase.member.CompleteOnboardingUseCase
import com.saion.core.domain.usecase.member.GetOnboardingInfoUseCase
import com.saion.core.domain.usecase.member.ValidateNicknameUseCase
import com.saion.core.model.member.NicknameValidationResult
import com.saion.core.model.member.OnboardingInfo
import com.saion.core.model.result.AppError
import com.saion.core.ui.viewmodel.BaseViewModel
import com.saion.feature.auth.impl.R
import com.saion.feature.auth.impl.ui.AuthSnackbarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
@Stable
internal class NicknameViewModel @Inject constructor(
    private val getOnboardingInfoUseCase: GetOnboardingInfoUseCase,
    private val completeOnboardingUseCase: CompleteOnboardingUseCase,
    private val validateNicknameUseCase: ValidateNicknameUseCase,
) : BaseViewModel<NicknameUiState, NicknameEffect, NicknameIntent>(NicknameUiState()) {
    init {
        dispatch(NicknameIntent.Load)
    }

    override fun handleIntent(intent: NicknameIntent) {
        when (intent) {
            NicknameIntent.BackClicked -> viewModelScope.launch {
                emitEffect(NicknameEffect.NavigateBack)
            }

            is NicknameIntent.NicknameChanged -> updateNickname(intent.nickname)

            NicknameIntent.Load -> loadOnboardingInfo()

            NicknameIntent.SubmitClicked -> submitNickname()
        }
    }

    private fun loadOnboardingInfo() {
        launchSafely(
            onSuccess = { onboardingInfo ->
                applyOnboardingInfo(onboardingInfo)
            },
            onFailure = { error ->
                emitEffect(
                    NicknameEffect.ShowSnackbar(
                        error.toDisplayMessage(R.string.nickname_error_load_onboarding),
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
                nickNamePlaceholder = initialNickname,
                validation = validateNicknameUseCase(initialNickname),
                socialProfileImageUrl = onboardingInfo.socialProfileImageUrl,
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

    private fun submitNickname() {
        val validation = validateNicknameUseCase(currentState.nickname)
        update { copy(validation = validation) }
        if (!validation.isValid) return

        launchSafely(
            onStart = { update { copy(isSubmitting = true) } },
            onSuccess = {
                update { copy(isSubmitting = false) }
                emitEffect(NicknameEffect.NavigateComplete)
            },
            onFailure = { error ->
                update { copy(isSubmitting = false) }
                emitEffect(
                    NicknameEffect.ShowSnackbar(
                        error.toDisplayMessage(R.string.nickname_error_save),
                    ),
                )
            },
            onFinally = { update { copy(isSubmitting = false) } },
        ) {
            completeOnboardingUseCase(validation.trimmedNickname)
        }
    }
}

private fun AppError.toDisplayMessage(defaultMessageResId: Int): AuthSnackbarMessage =
    AuthSnackbarMessage.Error(error = this, defaultMessageResId = defaultMessageResId)
