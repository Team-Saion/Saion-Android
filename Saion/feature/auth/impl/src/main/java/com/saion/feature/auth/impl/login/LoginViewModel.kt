package com.saion.feature.auth.impl.login

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import com.saion.core.auth.SocialAuthClient
import com.saion.core.auth.SocialAuthProvider
import com.saion.core.auth.SocialLoginFailureReason
import com.saion.core.auth.SocialLoginResult
import com.saion.core.domain.usecase.auth.LoginWithKakaoUseCase
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import com.saion.core.ui.event.GlobalUiEvent
import com.saion.core.ui.event.GlobalUiEventBus
import com.saion.core.ui.viewmodel.BaseViewModel
import com.saion.core.ui.viewmodel.UIEffect
import com.saion.core.ui.viewmodel.UIIntent
import com.saion.core.ui.viewmodel.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@Immutable
data class LoginUiState(
    val isLoading: Boolean = false,
) : UIState

sealed interface LoginIntent : UIIntent

sealed interface LoginEffect : UIEffect {
    data object NavigateNext : LoginEffect

    data class ShowSnackbar(val message: String) : LoginEffect
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val socialAuthClient: SocialAuthClient,
    private val loginWithKakaoUseCase: LoginWithKakaoUseCase,
) : BaseViewModel<LoginUiState, LoginEffect, LoginIntent>(LoginUiState()) {
    override fun handleIntent(intent: LoginIntent) = Unit

    fun login(context: Context) {
        viewModelScope.launch {
            update {
                copy(isLoading = true)
            }

            when (
                val socialLoginResult = socialAuthClient.login(
                    provider = SocialAuthProvider.KAKAO,
                    context = context,
                )
            ) {
                SocialLoginResult.Cancelled -> {
                    update { copy(isLoading = false) }
                    emitEffect(LoginEffect.ShowSnackbar("카카오 로그인이 취소되었습니다."))
                }

                is SocialLoginResult.Failure -> {
                    update { copy(isLoading = false) }
                    emitEffect(LoginEffect.ShowSnackbar(socialLoginResult.toDisplayMessage()))
                }

                is SocialLoginResult.Success -> when (val loginResult = loginWithKakaoUseCase(idToken = socialLoginResult.idToken)) {
                    is AppResult.Failure -> {
                        handleAppError(loginResult.error)
                        update { copy(isLoading = false) }
                        emitEffect(LoginEffect.ShowSnackbar(loginResult.error.toDisplayMessage()))
                    }

                    is AppResult.Success -> {
                        update { copy(isLoading = false) }
                        emitEffect(LoginEffect.NavigateNext)
                    }
                }
            }
        }
    }

    private fun SocialLoginResult.Failure.toDisplayMessage(): String = when (reason) {
        SocialLoginFailureReason.MISSING_ID_TOKEN -> "카카오 ID 토큰을 받지 못했습니다. OpenID Connect 설정을 확인해주세요."
        SocialLoginFailureReason.PROVIDER_UNAVAILABLE -> message ?: "카카오 로그인을 사용할 수 없습니다. 잠시 후 다시 시도해주세요."
        SocialLoginFailureReason.SDK_ERROR -> message ?: "카카오 로그인에 실패했습니다. 다시 시도해주세요."
        SocialLoginFailureReason.UNSUPPORTED_PROVIDER -> message ?: "지원하지 않는 로그인 방식입니다."
    }

    private fun AppError.toDisplayMessage(): String = when (this) {
        is AppError.Business -> message ?: "로그인에 실패했습니다. 다시 시도해주세요."
        is AppError.Unknown -> message ?: "로그인에 실패했습니다. 다시 시도해주세요."
        is AppError.NetworkUnavailable -> "네트워크 연결을 확인한 뒤 다시 시도해주세요."
        is AppError.Timeout -> "응답이 지연되고 있습니다. 다시 시도해주세요."
        is AppError.ServerUnavailable -> "서버에 일시적인 문제가 발생했습니다. 잠시 후 다시 시도해주세요."
        is AppError.Unauthorized -> "로그인 정보가 만료되었습니다. 다시 시도해주세요."
    }

    private fun handleAppError(error: AppError) {
        when (error) {
            is AppError.Unauthorized -> {
                GlobalUiEventBus.emit(GlobalUiEvent.SessionExpired)
            }

            else -> Unit
        }
    }
}
