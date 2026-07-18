package com.saion.feature.mypage.impl.withdraw.viewmodel

import androidx.compose.runtime.Stable
import com.saion.core.domain.usecase.member.WithdrawUseCase
import com.saion.core.ui.error.toSnackbarMessage
import com.saion.core.ui.viewmodel.BaseViewModel
import com.saion.feature.mypage.impl.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
@Stable
internal class WithdrawViewModel @Inject constructor(
    private val withdrawUseCase: WithdrawUseCase,
) : BaseViewModel<WithdrawState, WithdrawEffect, WithdrawIntent>(WithdrawState()) {
    override fun handleIntent(intent: WithdrawIntent) {
        when (intent) {
            is WithdrawIntent.ReasonChanged -> update { copy(reason = intent.value) }
            WithdrawIntent.ClickWithdraw -> update { copy(showWithdrawDialog = true) }
            WithdrawIntent.DismissWithdrawDialog -> update {
                copy(
                    showWithdrawDialog = false,
                    isSubmitting = false,
                )
            }

            WithdrawIntent.ConfirmWithdraw -> {
                if (currentState.isSubmitting) return
                withdraw()
            }
        }
    }

    private fun withdraw() {
        launchSafely(
            onStart = {
                update {
                    copy(
                        showWithdrawDialog = true,
                        isSubmitting = true,
                    )
                }
            },
            onSuccess = {
                update {
                    copy(
                        showWithdrawDialog = false,
                        isSubmitting = false,
                    )
                }
                emitEffect(WithdrawEffect.WithdrawCompleted)
            },
            onFailure = { error ->
                update { copy(isSubmitting = false) }
                emitEffect(
                    WithdrawEffect.ShowSnackbar(
                        error.toSnackbarMessage(
                            defaultMessageResId = R.string.withdraw_error_submit,
                            textMessage = { value, resId -> WithdrawSnackbarMessage.Text(value, resId) },
                            errorMessage = { appError, resId -> WithdrawSnackbarMessage.Error(appError, resId) },
                        ),
                    ),
                )
            },
        ) {
            withdrawUseCase(reason = currentState.normalizedReason())
        }
    }
}

private const val DEFAULT_WITHDRAW_REASON = "사유 없음"

private fun WithdrawState.normalizedReason(): String = reason.ifBlank { DEFAULT_WITHDRAW_REASON }
