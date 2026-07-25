package com.saion.feature.home.impl.memberlist.viewmodel

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.saion.core.domain.usecase.circle.ClearCurrentCircleUseCase
import com.saion.core.domain.usecase.circle.LeaveCircleUseCase
import com.saion.core.domain.usecase.circle.ObserveResolvedCurrentCircleUseCase
import com.saion.core.domain.usecase.circle.ResolvedCurrentCircle
import com.saion.core.domain.usecase.home.ObserveHomeMembersUseCase
import com.saion.core.domain.usecase.home.RefreshHomeUseCase
import com.saion.core.ui.error.toSnackbarMessage
import com.saion.core.ui.viewmodel.BaseViewModel
import com.saion.feature.home.impl.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@HiltViewModel
@Stable
internal class HomeMemberListViewModel @Inject constructor(
    private val observeResolvedCurrentCircleUseCase: ObserveResolvedCurrentCircleUseCase,
    private val observeHomeMembersUseCase: ObserveHomeMembersUseCase,
    private val refreshHomeUseCase: RefreshHomeUseCase,
    private val leaveCircleUseCase: LeaveCircleUseCase,
    private val clearCurrentCircleUseCase: ClearCurrentCircleUseCase,
) : BaseViewModel<HomeMemberListState, HomeMemberListEffect, HomeMemberListIntent>(HomeMemberListState()) {
    private var currentCircleId: String? = null
    private var membersJob: Job? = null
    private var latestMembers: ImmutableList<com.saion.core.model.home.CircleMember> = emptyList<com.saion.core.model.home.CircleMember>().toImmutableList()

    init {
        viewModelScope.launch {
            observeResolvedCurrentCircleUseCase()
                .collect { resolvedCurrentCircle ->
                    handleResolvedCurrentCircle(resolvedCurrentCircle)
                }
        }
    }

    override fun handleIntent(intent: HomeMemberListIntent) {
        when (intent) {
            HomeMemberListIntent.ClickLeave -> update { copy(showLeaveDialog = true) }
            HomeMemberListIntent.DismissLeaveDialog -> {
                if (currentState.isLeaveLoading) return
                update {
                    copy(
                        showLeaveDialog = false,
                        isLeaveLoading = false,
                    )
                }
            }
            HomeMemberListIntent.ConfirmLeave -> {
                if (currentState.isLeaveLoading) return
                leaveCurrentCircle()
            }
        }
    }

    private fun handleResolvedCurrentCircle(resolvedCurrentCircle: ResolvedCurrentCircle) {
        when (resolvedCurrentCircle) {
            is ResolvedCurrentCircle.Available -> {
                if (currentCircleId != resolvedCurrentCircle.circleId) {
                    currentCircleId = resolvedCurrentCircle.circleId
                    observeMembers(circleId = resolvedCurrentCircle.circleId)
                    refreshMembers(circleId = resolvedCurrentCircle.circleId)
                }
            }
            ResolvedCurrentCircle.Missing -> {
                membersJob?.cancel()
                membersJob = null
                currentCircleId = null
                latestMembers = emptyList<com.saion.core.model.home.CircleMember>().toImmutableList()
                update {
                    copy(
                        status = HomeMemberListStatus.Empty,
                        members = latestMembers,
                    )
                }
            }
        }
    }

    private fun observeMembers(circleId: String) {
        membersJob?.cancel()
        membersJob = viewModelScope.launch {
            observeHomeMembersUseCase(circleId)
                .collect { members ->
                    latestMembers = members.toImmutableList()
                    update {
                        copy(
                            status = HomeMemberListStatus.Content,
                            members = latestMembers,
                        )
                    }
                }
        }
    }

    private fun refreshMembers(circleId: String) {
        launchSafely(
            onStart = {
                update { copy(status = HomeMemberListStatus.Loading) }
            },
            onSuccess = {
                update {
                    copy(
                        status = if (latestMembers.isEmpty()) {
                            HomeMemberListStatus.Empty
                        } else {
                            HomeMemberListStatus.Content
                        },
                        members = latestMembers,
                    )
                }
            },
            onFailure = { error ->
                update { copy(status = HomeMemberListStatus.Error) }
                emitEffect(
                    HomeMemberListEffect.ShowSnackbar(
                        error.toSnackbarMessage(
                            defaultMessageResId = R.string.home_member_list_error_load,
                            textMessage = { value, resId -> HomeMemberListSnackbarMessage.Text(value, resId) },
                            errorMessage = { appError, resId -> HomeMemberListSnackbarMessage.Error(appError, resId) },
                        ),
                    ),
                )
            },
        ) {
            refreshHomeUseCase(circleId = circleId)
        }
    }

    private fun leaveCurrentCircle() {
        val circleId = currentCircleId
        if (circleId == null) {
            viewModelScope.launch {
                emitEffect(
                    HomeMemberListEffect.ShowSnackbar(
                        HomeMemberListSnackbarMessage.Text(
                            value = "",
                            defaultMessageResId = R.string.home_member_list_error_leave,
                        ),
                    ),
                )
            }
            return
        }

        update {
            copy(
                showLeaveDialog = true,
                isLeaveLoading = true,
            )
        }

        launchSafely(
            onSuccess = {
                update {
                    copy(
                        showLeaveDialog = false,
                        isLeaveLoading = false,
                    )
                }
                emitEffect(HomeMemberListEffect.LeaveCompleted)
            },
            onFailure = { error ->
                update { copy(isLeaveLoading = false) }
                emitEffect(
                    HomeMemberListEffect.ShowSnackbar(
                        error.toSnackbarMessage(
                            defaultMessageResId = R.string.home_member_list_error_leave,
                            textMessage = { value, resId -> HomeMemberListSnackbarMessage.Text(value, resId) },
                            errorMessage = { appError, resId -> HomeMemberListSnackbarMessage.Error(appError, resId) },
                        ),
                    ),
                )
            },
        ) {
            when (val result = leaveCircleUseCase(circleId = circleId)) {
                is com.saion.core.model.result.AppResult.Success -> {
                    clearCurrentCircleUseCase()
                    result
                }

                is com.saion.core.model.result.AppResult.Failure -> result
            }
        }
    }
}
