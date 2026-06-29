package com.saion.feature.auth.impl.login

import android.content.Context
import android.util.Log
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
    val errorMessage: String? = null,
) : UIState

sealed interface LoginIntent : UIIntent {
    data object DismissError : LoginIntent
}

sealed interface LoginEffect : UIEffect {
    data object NavigateNext : LoginEffect
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val socialAuthClient: SocialAuthClient,
    private val loginWithKakaoUseCase: LoginWithKakaoUseCase,
) : BaseViewModel<LoginUiState, LoginEffect, LoginIntent>(LoginUiState()) {
    override fun handleIntent(intent: LoginIntent) {
        when (intent) {
            LoginIntent.DismissError -> update { copy(errorMessage = null) }
        }
    }

    fun login(context: Context) {
        viewModelScope.launch {
            update {
                copy(
                    isLoading = true,
                    errorMessage = null,
                )
            }

            when (
                val socialLoginResult = socialAuthClient.login(
                    provider = SocialAuthProvider.KAKAO,
                    context = context,
                ).also { Log.i("TEST", "$it") }
            ) {
                SocialLoginResult.Cancelled -> {
                    update { copy(isLoading = false) }
                }

                is SocialLoginResult.Failure -> {
                    update {
                        copy(
                            isLoading = false,
                            errorMessage = socialLoginResult.toDisplayMessage(),
                        )
                    }
                }

                is SocialLoginResult.Success -> when (val loginResult = loginWithKakaoUseCase(idToken = socialLoginResult.idToken)) {
                    is AppResult.Failure -> {
                        handleAppError(loginResult.error)
                        update {
                            copy(
                                isLoading = false,
                                errorMessage = loginResult.error.toDisplayMessage(),
                            )
                        }
                    }

                    is AppResult.Success -> {
                        update { copy(isLoading = false) }
                        emitEffect(LoginEffect.NavigateNext)
                    }
                }
            }
        }
    }

    private fun SocialLoginResult.Failure.toDisplayMessage(): String? = when (reason) {
        SocialLoginFailureReason.MISSING_ID_TOKEN -> "카카오 ID 토큰을 받지 못했습니다. OpenID Connect 설정을 확인해주세요."
        SocialLoginFailureReason.PROVIDER_UNAVAILABLE -> message
        SocialLoginFailureReason.SDK_ERROR -> message ?: "카카오 로그인에 실패했습니다. 다시 시도해주세요."
        SocialLoginFailureReason.UNSUPPORTED_PROVIDER -> message
    }

    private fun AppError.toDisplayMessage(): String? = when (this) {
        is AppError.Business -> message
        is AppError.Unknown -> message
        else -> null
    }

    private fun handleAppError(error: AppError) {
        when (error) {
            is AppError.Unauthorized -> {
                GlobalUiEventBus.emit(GlobalUiEvent.SessionExpired)
            }

            else -> Unit
        }
    }

    fun showLoginError(message: String) {
        update {
            copy(
                isLoading = false,
                errorMessage = message,
            )
        }
    }
}
