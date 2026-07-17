package com.saion.feature.home.impl.home.viewmodel

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.saion.core.domain.usecase.circle.ObserveResolvedCurrentCircleUseCase
import com.saion.core.domain.usecase.circle.ResolvedCurrentCircle
import com.saion.core.domain.usecase.home.GetHomeUseCase
import com.saion.core.domain.usecase.home.GetHomeInviterNameUseCase
import com.saion.core.domain.usecase.invitation.IssueInvitationUseCase
import com.saion.core.share.InvitationShareClient
import com.saion.core.share.InvitationShareResult
import com.saion.core.ui.error.toSnackbarMessage
import com.saion.core.ui.viewmodel.BaseViewModel
import com.saion.feature.home.impl.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
@Stable
internal class HomeViewModel @Inject constructor(
    private val observeResolvedCurrentCircleUseCase: ObserveResolvedCurrentCircleUseCase,
    private val getHomeUseCase: GetHomeUseCase,
    private val getHomeInviterNameUseCase: GetHomeInviterNameUseCase,
    private val issueInvitationUseCase: IssueInvitationUseCase,
    private val invitationShareClient: InvitationShareClient,
) : BaseViewModel<HomeState, HomeEffect, HomeIntent>(HomeState.Loading) {
    init {
        viewModelScope.launch {
            observeResolvedCurrentCircleUseCase()
                .collect { resolvedCurrentCircle ->
                    handleResolvedCurrentCircle(resolvedCurrentCircle)
                }
        }
    }

    override fun handleIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.InviteClicked -> invite()
            HomeIntent.HeroScheduleShareClicked -> Unit
        }
    }

    private fun handleResolvedCurrentCircle(resolvedCurrentCircle: ResolvedCurrentCircle) {
        when (resolvedCurrentCircle) {
            is ResolvedCurrentCircle.Available -> loadHomeOverview(resolvedCurrentCircle.circleId)
            ResolvedCurrentCircle.Missing -> update { HomeState.None }
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
                emitEffect(
                    HomeEffect.ShowSnackbar(
                        error.toSnackbarMessage(
                            defaultMessageResId = R.string.home_error_load_overview,
                            textMessage = { value, resId -> HomeSnackbarMessage.Text(value, resId) },
                            errorMessage = { appError, resId -> HomeSnackbarMessage.Error(appError, resId) },
                        ),
                    ),
                )
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
                val inviterName = getHomeInviterNameUseCase(state.members)
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
                emitEffect(
                    HomeEffect.ShowSnackbar(
                        error.toSnackbarMessage(
                            defaultMessageResId = R.string.home_error_issue_invitation,
                            textMessage = { value, resId -> HomeSnackbarMessage.Text(value, resId) },
                            errorMessage = { appError, resId -> HomeSnackbarMessage.Error(appError, resId) },
                        ),
                    ),
                )
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
