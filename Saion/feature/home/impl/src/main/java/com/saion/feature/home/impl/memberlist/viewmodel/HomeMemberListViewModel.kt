package com.saion.feature.home.impl.memberlist.viewmodel

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.saion.core.domain.usecase.circle.ObserveResolvedCurrentCircleUseCase
import com.saion.core.domain.usecase.circle.ResolvedCurrentCircle
import com.saion.core.domain.usecase.home.GetHomeMembersUseCase
import com.saion.core.ui.error.toSnackbarMessage
import com.saion.core.ui.viewmodel.BaseViewModel
import com.saion.feature.home.impl.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@HiltViewModel
@Stable
internal class HomeMemberListViewModel @Inject constructor(
    private val observeResolvedCurrentCircleUseCase: ObserveResolvedCurrentCircleUseCase,
    private val getHomeMembersUseCase: GetHomeMembersUseCase,
) : BaseViewModel<HomeMemberListState, HomeMemberListEffect, HomeMemberListIntent>(HomeMemberListState.Loading) {
    init {
        viewModelScope.launch {
            observeResolvedCurrentCircleUseCase()
                .collect { resolvedCurrentCircle ->
                    handleResolvedCurrentCircle(resolvedCurrentCircle)
                }
        }
    }

    override fun handleIntent(intent: HomeMemberListIntent) = Unit

    private fun handleResolvedCurrentCircle(resolvedCurrentCircle: ResolvedCurrentCircle) {
        when (resolvedCurrentCircle) {
            is ResolvedCurrentCircle.Available -> loadMembers(resolvedCurrentCircle.circleId)
            ResolvedCurrentCircle.Missing -> update { HomeMemberListState.Empty }
        }
    }

    private fun loadMembers(circleId: String) {
        launchSafely(
            onStart = {
                update { HomeMemberListState.Loading }
            },
            onSuccess = { members ->
                update { HomeMemberListState.Content(members = members.toImmutableList()) }
            },
            onFailure = { error ->
                update { HomeMemberListState.Error }
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
            getHomeMembersUseCase(circleId = circleId)
        }
    }
}
