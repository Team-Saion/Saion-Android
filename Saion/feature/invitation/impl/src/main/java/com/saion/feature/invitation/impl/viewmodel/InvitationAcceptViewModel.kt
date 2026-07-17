package com.saion.feature.invitation.impl.viewmodel

import androidx.compose.runtime.Stable
import com.saion.core.domain.usecase.circle.SelectCurrentCircleUseCase
import com.saion.core.domain.usecase.invitation.AcceptInvitationUseCase
import com.saion.core.domain.usecase.invitation.GetInvitationByTokenUseCase
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
                        isLoadFailed = false,
                    )
                }
            },
            onSuccess = { detail ->
                update {
                    copy(
                        isLoading = false,
                        detail = detail,
                        isLoadFailed = false,
                    )
                }
            },
            onFailure = { error ->
                update {
                    copy(
                        isLoading = false,
                        detail = null,
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
}
