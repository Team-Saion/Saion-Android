package com.saion.feature.circlecreate.impl.viewmodel

import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import com.saion.core.domain.usecase.circle.CreateCircleUseCase
import com.saion.core.model.result.AppError
import com.saion.core.ui.event.GlobalUiEvent
import com.saion.core.ui.event.GlobalUiEventBus
import com.saion.core.ui.viewmodel.BaseViewModel
import com.saion.feature.circlecreate.impl.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
@Stable
internal class CircleCreateViewModel @Inject constructor(
    private val createCircleUseCase: CreateCircleUseCase,
) : BaseViewModel<CircleCreateUiState, CircleCreateEffect, CircleCreateIntent>(CircleCreateUiState()) {
    override fun handleIntent(intent: CircleCreateIntent) {
        when (intent) {
            is CircleCreateIntent.NameChanged -> handleNameChanged(intent.value)
            CircleCreateIntent.SubmitClicked -> submit()
        }
    }

    private fun handleNameChanged(value: String) {
        val validationMessageResId = value.validationMessageResId(showEmptyError = true)
        update {
            copy(
                name = value,
                hasEditedName = true,
                validationMessageResId = validationMessageResId,
                isSubmitEnabled = value.isSubmittable(validationMessageResId),
            )
        }
    }

    private fun submit() {
        if (currentState.isSubmitting) return

        val validationMessageResId = currentState.name.validationMessageResId(showEmptyError = true)
        if (validationMessageResId != null) {
            update {
                copy(
                    hasEditedName = true,
                    validationMessageResId = validationMessageResId,
                    isSubmitEnabled = false,
                )
            }
            return
        }

        val trimmedName = currentState.name.trim()
        launchSafely(
            onStart = {
                update { copy(isSubmitting = true) }
            },
            onSuccess = {
                GlobalUiEventBus.emit(GlobalUiEvent.CircleCreated)
                emitEffect(CircleCreateEffect.Close)
            },
            onFailure = { error ->
                emitEffect(
                    CircleCreateEffect.ShowSnackbar(
                        error.toSnackbarMessage(R.string.circle_create_error_default),
                    ),
                )
            },
            onFinally = {
                update { copy(isSubmitting = false) }
            },
        ) {
            createCircleUseCase(name = trimmedName)
        }
    }
}

private fun String.isSubmittable(@StringRes validationMessageResId: Int?): Boolean =
    trim().isNotEmpty() && validationMessageResId == null

@StringRes
private fun String.validationMessageResId(showEmptyError: Boolean): Int? {
    val trimmed = trim()
    return when {
        trimmed.isEmpty() && showEmptyError -> R.string.circle_create_error_required
        trimmed.isEmpty() -> null
        trimmed.length > MAX_CIRCLE_NAME_LENGTH -> R.string.circle_create_error_too_long
        BLACKLIST_REGEX.containsMatchIn(this) -> R.string.circle_create_error_invalid_character
        else -> null
    }
}

private fun AppError.toSnackbarMessage(@StringRes defaultMessageResId: Int): CircleCreateSnackbarMessage = when (this) {
    is AppError.Business -> CircleCreateSnackbarMessage.Text(message.orEmpty(), defaultMessageResId)
    is AppError.Unknown -> CircleCreateSnackbarMessage.Text(message.orEmpty(), defaultMessageResId)
    is AppError.NetworkUnavailable,
    is AppError.Timeout,
    is AppError.ServerUnavailable,
    is AppError.Unauthorized,
    -> CircleCreateSnackbarMessage.Error(
        error = this,
        defaultMessageResId = defaultMessageResId,
    )
}

private const val MAX_CIRCLE_NAME_LENGTH = 20
private val BLACKLIST_REGEX = Regex("[<>&\"'\\\\]|\\p{Cntrl}")
