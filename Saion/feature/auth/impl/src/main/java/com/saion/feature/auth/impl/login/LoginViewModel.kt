package com.saion.feature.auth.impl.login

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.saion.core.auth.SocialAuthClient
import com.saion.core.auth.SocialAuthProvider
import com.saion.core.auth.SocialLoginFailureReason
import com.saion.core.auth.SocialLoginResult
import com.saion.core.domain.usecase.auth.LoginWithKakaoUseCase
import com.saion.core.model.member.MemberRole
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import com.saion.core.notification.NotificationLifecycleManager
import com.saion.core.ui.event.GlobalUiEvent
import com.saion.core.ui.event.GlobalUiEventBus
import com.saion.core.ui.viewmodel.BaseViewModel
import com.saion.core.ui.viewmodel.UIEffect
import com.saion.core.ui.viewmodel.UIIntent
import com.saion.core.ui.viewmodel.UIState
import com.saion.feature.auth.api.key.AuthStartStep
import com.saion.feature.auth.impl.R
import com.saion.feature.auth.impl.ui.AuthSnackbarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@Immutable
internal data class LoginUiState(val isLoading: Boolean = false) : UIState

internal sealed interface LoginIntent : UIIntent

internal sealed interface LoginEffect : UIEffect {
    data class NavigateNext(val startStep: AuthStartStep) : LoginEffect

    data object NavigateMain : LoginEffect

    data class ShowSnackbar(val message: AuthSnackbarMessage) : LoginEffect
}

@HiltViewModel
@Stable
internal class LoginViewModel @Inject constructor(
    private val socialAuthClient: SocialAuthClient,
    private val loginWithKakaoUseCase: LoginWithKakaoUseCase,
    private val notificationLifecycleManager: NotificationLifecycleManager,
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
                    emitEffect(LoginEffect.ShowSnackbar(AuthSnackbarMessage.Res(R.string.login_error_cancelled)))
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
                        launch { notificationLifecycleManager.syncOnLoginSuccess() }
                        update { copy(isLoading = false) }
                        when (loginResult.data) {
                            MemberRole.PENDING -> emitEffect(LoginEffect.NavigateNext(AuthStartStep.TERMS))

                            MemberRole.MEMBER,
                            MemberRole.ADMIN,
                            -> emitEffect(LoginEffect.NavigateMain)
                        }
                    }
                }
            }
        }
    }

    private fun SocialLoginResult.Failure.toDisplayMessage(): AuthSnackbarMessage = when (reason) {
        SocialLoginFailureReason.MISSING_ID_TOKEN -> AuthSnackbarMessage.Res(R.string.login_error_missing_id_token)
        SocialLoginFailureReason.PROVIDER_UNAVAILABLE -> AuthSnackbarMessage.Text(
            value = message.orEmpty(),
            defaultMessageResId = R.string.login_error_provider_unavailable,
        )

        SocialLoginFailureReason.SDK_ERROR -> AuthSnackbarMessage.Text(
            value = message.orEmpty(),
            defaultMessageResId = R.string.login_error_kakao_failed,
        )

        SocialLoginFailureReason.UNSUPPORTED_PROVIDER -> AuthSnackbarMessage.Text(
            value = message.orEmpty(),
            defaultMessageResId = R.string.login_error_unsupported_provider,
        )
    }

    private fun AppError.toDisplayMessage(): AuthSnackbarMessage =
        AuthSnackbarMessage.Error(error = this, defaultMessageResId = R.string.login_error_default)

    private fun handleAppError(error: AppError) {
        when (error) {
            is AppError.Unauthorized -> {
                GlobalUiEventBus.emit(GlobalUiEvent.SessionExpired)
            }

            else -> Unit
        }
    }
}
