package com.saion.feature.home.impl.home.viewmodel

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.saion.core.domain.usecase.circle.ObserveResolvedCurrentCircleUseCase
import com.saion.core.domain.usecase.circle.ResolvedCurrentCircle
import com.saion.core.domain.usecase.home.GetHomeInviterNameUseCase
import com.saion.core.domain.usecase.home.ObserveHomeUseCase
import com.saion.core.domain.usecase.home.RefreshHomeUseCase
import com.saion.core.domain.usecase.invitation.IssueInvitationUseCase
import com.saion.core.domain.usecase.schedule.RequestFamilyNotificationUseCase
import com.saion.core.share.InvitationShareClient
import com.saion.core.share.InvitationShareResult
import com.saion.core.ui.error.toSnackbarMessage
import com.saion.core.ui.viewmodel.BaseViewModel
import com.saion.feature.home.impl.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@HiltViewModel
@Stable
internal class HomeViewModel @Inject constructor(
    private val observeResolvedCurrentCircleUseCase: ObserveResolvedCurrentCircleUseCase,
    private val observeHomeUseCase: ObserveHomeUseCase,
    private val refreshHomeUseCase: RefreshHomeUseCase,
    private val getHomeInviterNameUseCase: GetHomeInviterNameUseCase,
    private val issueInvitationUseCase: IssueInvitationUseCase,
    private val requestFamilyNotificationUseCase: RequestFamilyNotificationUseCase,
    private val invitationShareClient: InvitationShareClient,
) : BaseViewModel<HomeState, HomeEffect, HomeIntent>(HomeState.Loading) {
    private var currentCircleId: String? = null
    private var homeJob: Job? = null

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
            HomeIntent.HeroScheduleShareClicked -> showFamilyNotificationDialog()
            HomeIntent.DismissFamilyNotificationDialog -> hideFamilyNotificationDialog()
            HomeIntent.ConfirmFamilyNotification -> requestFamilyNotification()
        }
    }

    private fun handleResolvedCurrentCircle(resolvedCurrentCircle: ResolvedCurrentCircle) {
        when (resolvedCurrentCircle) {
            is ResolvedCurrentCircle.Available -> {
                if (currentCircleId != resolvedCurrentCircle.circleId) {
                    currentCircleId = resolvedCurrentCircle.circleId
                    observeHome(circleId = resolvedCurrentCircle.circleId)
                    refreshHomeOverview(circleId = resolvedCurrentCircle.circleId)
                }
            }
            ResolvedCurrentCircle.Missing -> {
                homeJob?.cancel()
                homeJob = null
                currentCircleId = null
                update { HomeState.None }
            }
        }
    }

    private fun observeHome(circleId: String) {
        homeJob?.cancel()
        homeJob = viewModelScope.launch {
            observeHomeUseCase(circleId)
                .filterNotNull()
                .collect { overview ->
                    val currentContentState = currentState as? HomeState.Content
                    update {
                        overview.toUiState(
                            isInviting = currentContentState?.isInviting ?: false,
                            isFamilyNotificationDialogVisible = currentContentState?.isFamilyNotificationDialogVisible ?: false,
                            isRequestingFamilyNotification = currentContentState?.isRequestingFamilyNotification ?: false,
                        )
                    }
                }
        }
    }

    private fun refreshHomeOverview(circleId: String) {
        launchSafely(
            onSuccess = {},
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
            refreshHomeUseCase(circleId = circleId)
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

    private fun showFamilyNotificationDialog() {
        val state = currentState as? HomeState.Content ?: return
        if (state.heroSchedule == null || state.isRequestingFamilyNotification) return

        updateContent { copy(isFamilyNotificationDialogVisible = true) }
    }

    private fun hideFamilyNotificationDialog() {
        updateContent { copy(isFamilyNotificationDialogVisible = false) }
    }

    private fun requestFamilyNotification() {
        val state = currentState as? HomeState.Content ?: return
        val circleId = currentCircleId ?: return
        val scheduleId = state.heroSchedule?.scheduleId ?: return
        if (state.isRequestingFamilyNotification) return

        launchSafely(
            onStart = {
                updateContent {
                    copy(
                        isFamilyNotificationDialogVisible = false,
                        isRequestingFamilyNotification = true,
                    )
                }
            },
            onSuccess = {
                emitEffect(
                    HomeEffect.ShowSnackbar(
                        HomeSnackbarMessage.Text(
                            value = "",
                            defaultMessageResId = R.string.home_success_request_family_notification,
                        ),
                    ),
                )
            },
            onFailure = { error ->
                emitEffect(
                    HomeEffect.ShowSnackbar(
                        error.toSnackbarMessage(
                            defaultMessageResId = R.string.home_error_request_family_notification,
                            textMessage = { value, resId -> HomeSnackbarMessage.Text(value, resId) },
                            errorMessage = { appError, resId -> HomeSnackbarMessage.Error(appError, resId) },
                        ),
                    ),
                )
            },
            onFinally = {
                updateContent { copy(isRequestingFamilyNotification = false) }
            },
        ) {
            requestFamilyNotificationUseCase(circleId = circleId, scheduleId = scheduleId)
        }
    }

    private fun updateContent(block: HomeState.Content.() -> HomeState.Content) {
        val state = currentState as? HomeState.Content ?: return
        update { if (this is HomeState.Content) block(this) else state }
    }
}
