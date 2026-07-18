package com.saion.feature.invitation.impl.viewmodel

import androidx.compose.runtime.Stable
import com.saion.core.domain.usecase.circle.SelectCurrentCircleUseCase
import com.saion.core.domain.usecase.invitation.AcceptInvitationUseCase
import com.saion.core.domain.usecase.invitation.GetInvitationByTokenUseCase
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import com.saion.core.ui.error.toSnackbarMessage
import com.saion.core.ui.viewmodel.BaseViewModel
import com.saion.feature.invitation.impl.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
@Stable
internal class InvitationAcceptViewModel @Inject constructor(
    private val getInvitationByTokenUseCase: GetInvitationByTokenUseCase,
    private val acceptInvitationUseCase: AcceptInvitationUseCase,
    private val selectCurrentCircleUseCase: SelectCurrentCircleUseCase,
) : BaseViewModel<InvitationAcceptState, InvitationAcceptEffect, InvitationAcceptIntent>(InvitationAcceptState()) {
    private var token: String? = null

    fun bind(token: String) {
        if (this.token == token && (currentState.detail != null || currentState.isLoadFailed)) return
        this.token = token
        dispatch(InvitationAcceptIntent.Load)
    }

    override fun handleIntent(intent: InvitationAcceptIntent) {
        when (intent) {
            InvitationAcceptIntent.Load -> loadInvitation()
            InvitationAcceptIntent.AcceptClicked -> acceptInvitation()
            InvitationAcceptIntent.CloseClicked -> close()
        }
    }

    private fun loadInvitation() {
        val currentToken = token ?: return
        launchSafely(
            onStart = {
                update {
                    copy(
                        isLoading = true,
                        isExpired = false,
                        isLoadFailed = false,
                    )
                }
            },
            onSuccess = { detail ->
                update {
                    copy(
                        isLoading = false,
                        detail = detail,
                        isExpired = false,
                        isLoadFailed = false,
                    )
                }
            },
            onFailure = { error ->
                if (error.isExpiredInvitation()) {
                    update {
                        copy(
                            isLoading = false,
                            detail = null,
                            isExpired = true,
                            isLoadFailed = false,
                        )
                    }
                    return@launchSafely
                }
                update {
                    copy(
                        isLoading = false,
                        detail = null,
                        isExpired = false,
                        isLoadFailed = true,
                    )
                }
                emitEffect(
                    InvitationAcceptEffect.ShowSnackbar(
                        error.toSnackbarMessage(
                            defaultMessageResId = R.string.invitation_error_load,
                            textMessage = { value, resId -> InvitationAcceptSnackbarMessage.Text(value, resId) },
                            errorMessage = { appError, resId -> InvitationAcceptSnackbarMessage.Error(appError, resId) },
                        ),
                    ),
                )
            },
        ) {
            getInvitationByTokenUseCase(token = currentToken)
        }
    }

    private fun acceptInvitation() {
        val currentToken = token ?: return
        if (currentState.isAccepting || currentState.detail == null) return

        launchSafely(
            onStart = {
                update { copy(isAccepting = true) }
            },
            onSuccess = { invitation ->
                selectCurrentCircleUseCase(invitation.circleId)
                emitEffect(InvitationAcceptEffect.Close)
            },
            onFailure = { error ->
                if (error.isExpiredInvitation()) {
                    update {
                        copy(
                            detail = null,
                            isExpired = true,
                            isLoadFailed = false,
                        )
                    }
                    return@launchSafely
                }
                emitEffect(
                    InvitationAcceptEffect.ShowSnackbar(
                        error.toSnackbarMessage(
                            defaultMessageResId = R.string.invitation_error_accept,
                            textMessage = { value, resId -> InvitationAcceptSnackbarMessage.Text(value, resId) },
                            errorMessage = { appError, resId -> InvitationAcceptSnackbarMessage.Error(appError, resId) },
                        ),
                    ),
                )
            },
            onFinally = {
                update { copy(isAccepting = false) }
            },
        ) {
            acceptInvitationUseCase(token = currentToken)
        }
    }

    private fun close() {
        launchSafely(
            onSuccess = {
                emitEffect(InvitationAcceptEffect.Close)
            },
        ) {
            AppResult.Success(Unit)
        }
    }

    private fun AppError.isExpiredInvitation(): Boolean =
        this is AppError.Business && rawCode == EXPIRED_INVITATION_ERROR_CODE

    private companion object {
        const val EXPIRED_INVITATION_ERROR_CODE = "I410_1"
    }
}
