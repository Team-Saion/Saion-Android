package com.saion.feature.circlecreate.impl.viewmodel

import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import com.saion.core.ui.viewmodel.BaseViewModel
import com.saion.feature.circlecreate.impl.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
@Stable
internal class CircleJoinViewModel @Inject constructor() :
    BaseViewModel<CircleJoinUiState, CircleJoinEffect, CircleJoinIntent>(CircleJoinUiState()) {
    override fun handleIntent(intent: CircleJoinIntent) {
        when (intent) {
            is CircleJoinIntent.CodeChanged -> handleCodeChanged(intent.value)
            CircleJoinIntent.SubmitClicked -> submit()
        }
    }

    private fun handleCodeChanged(value: String) {
        val validationMessageResId = value.validationMessageResId(showEmptyError = true)
        update {
            copy(
                code = value,
                hasEditedCode = true,
                validationMessageResId = validationMessageResId,
                isSubmitEnabled = value.isSubmittable(validationMessageResId),
            )
        }
    }

    private fun submit() {
        val validationMessageResId = currentState.code.validationMessageResId(showEmptyError = true)
        if (validationMessageResId != null) {
            update {
                copy(
                    hasEditedCode = true,
                    validationMessageResId = validationMessageResId,
                    isSubmitEnabled = false,
                )
            }
            return
        }

        val trimmedCode = currentState.code.trim()
        launchSafely(
            onSuccess = {
                emitEffect(CircleJoinEffect.OpenInvitation(token = trimmedCode))
            },
        ) {
            com.saion.core.model.result.AppResult.Success(Unit)
        }
    }
}

private fun String.isSubmittable(@StringRes validationMessageResId: Int?): Boolean =
    trim().isNotEmpty() && validationMessageResId == null

@StringRes
private fun String.validationMessageResId(showEmptyError: Boolean): Int? = when {
    trim().isEmpty() && showEmptyError -> R.string.circle_join_error_required
    trim().isEmpty() -> null
    else -> null
}
