package com.saion.feature.home.impl.viewmodel

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.saion.core.domain.usecase.circle.ListCirclesUseCase
import com.saion.core.domain.usecase.home.GetHomeUseCase
import com.saion.core.domain.usecase.invitation.IssueInvitationUseCase
import com.saion.core.model.result.AppError
import com.saion.core.share.InvitationShareClient
import com.saion.core.share.InvitationShareResult
import com.saion.core.ui.event.CircleCreatedEventBus
import com.saion.core.ui.viewmodel.BaseViewModel
import com.saion.feature.home.impl.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
@Stable
internal class HomeViewModel @Inject constructor(
    private val listCirclesUseCase: ListCirclesUseCase,
    private val getHomeUseCase: GetHomeUseCase,
    private val issueInvitationUseCase: IssueInvitationUseCase,
    private val invitationShareClient: InvitationShareClient,
) : BaseViewModel<HomeState, HomeEffect, HomeIntent>(HomeState.Loading) {
    init {
        dispatch(HomeIntent.Load)
        viewModelScope.launch {
            CircleCreatedEventBus.events.collect {
                dispatch(HomeIntent.Load)
            }
        }
    }

    override fun handleIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.Load -> loadHome()
            HomeIntent.InviteClicked -> invite()
        }
    }

    private fun loadHome() {
        launchSafely(
            onSuccess = { circles ->
                val firstCircle = circles.firstOrNull() ?: return@launchSafely update { HomeState.None }
                loadHomeOverview(firstCircle.circleId)
            },
            onFailure = { error ->
                update { HomeState.None }
                emitEffect(HomeEffect.ShowSnackbar(error.toSnackbarMessage(R.string.home_error_load_circles)))
            },
        ) {
            listCirclesUseCase()
        }
    }

    private fun loadHomeOverview(circleId: String) {
        launchSafely(
            onSuccess = { overview ->
                val isInviting = (currentState as? HomeState.Content)?.isInviting ?: false
                update { overview.toUiState(isInviting = isInviting) }
            },
            onFailure = { error ->
                update { HomeState.None }
                emitEffect(HomeEffect.ShowSnackbar(error.toSnackbarMessage(R.string.home_error_load_overview)))
            },
        ) {
            getHomeUseCase(circleId = circleId)
        }
    }

    private fun invite() {
        val state = currentState as? HomeState.Content ?: return
        if (!state.canInvite || state.isInviting) return

        launchSafely(
            onStart = {
                updateContent { copy(isInviting = true) }
            },
            onSuccess = { invitation ->
                val inviterName = state.members.firstOrNull { it.isMe }?.nickname?.removeSuffix(" (나)") ?: DEFAULT_INVITER_NAME
                when (val result = invitationShareClient.shareInvitation(inviterName, state.circle.name, invitation)) {
                    InvitationShareResult.Success -> Unit

                    is InvitationShareResult.Failure -> {
                        emitEffect(
                            HomeEffect.ShowSnackbar(
                                HomeSnackbarMessage.Text(
                                    value = result.message.orEmpty(),
                                    defaultMessageResId = R.string.home_error_share_invitation,
                                ),
                            ),
                        )
                    }
                }
            },
            onFailure = { error ->
                emitEffect(HomeEffect.ShowSnackbar(error.toSnackbarMessage(R.string.home_error_issue_invitation)))
            },
            onFinally = {
                updateContent { copy(isInviting = false) }
            },
        ) {
            issueInvitationUseCase(targetId = state.circle.circleId)
        }
    }

    private fun updateContent(block: HomeState.Content.() -> HomeState.Content) {
        val state = currentState as? HomeState.Content ?: return
        update { if (this is HomeState.Content) block(this) else state }
    }
}

private fun AppError.toSnackbarMessage(defaultMessageResId: Int): HomeSnackbarMessage = when (this) {
    is AppError.Business -> HomeSnackbarMessage.Text(message.orEmpty(), defaultMessageResId)

    is AppError.Unknown -> HomeSnackbarMessage.Text(message.orEmpty(), defaultMessageResId)

    is AppError.NetworkUnavailable,
    is AppError.Timeout,
    is AppError.ServerUnavailable,
    is AppError.Unauthorized,
    -> HomeSnackbarMessage.Error(
        error = this,
        defaultMessageResId = defaultMessageResId,
    )
}

private const val DEFAULT_INVITER_NAME = "가족"
