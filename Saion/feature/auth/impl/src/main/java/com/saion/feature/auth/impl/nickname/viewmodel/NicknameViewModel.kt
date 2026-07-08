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
                emitEffect(NicknameEffect.ShowSnackbar(error.toDisplayMessage("온보딩 정보를 불러오지 못했습니다.")))
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
                nickNamePlaceholder = initialNickname.ifBlank { "닉네임" },
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
                emitEffect(NicknameEffect.ShowSnackbar(error.toDisplayMessage("닉네임 저장에 실패했습니다. 다시 시도해주세요.")))
            },
            onFinally = { update { copy(isSubmitting = false) } },
        ) {
            completeOnboardingUseCase(validation.trimmedNickname)
        }
    }
}

private fun AppError.toDisplayMessage(defaultMessage: String): String = when (this) {
    is AppError.Business -> message ?: defaultMessage
    is AppError.Unknown -> message ?: defaultMessage
    is AppError.NetworkUnavailable -> "네트워크 연결을 확인한 뒤 다시 시도해주세요."
    is AppError.Timeout -> "응답이 지연되고 있습니다. 다시 시도해주세요."
    is AppError.ServerUnavailable -> "서버에 일시적인 문제가 발생했습니다. 잠시 후 다시 시도해주세요."
    is AppError.Unauthorized -> "로그인 정보가 만료되었습니다. 다시 로그인해주세요."
}
