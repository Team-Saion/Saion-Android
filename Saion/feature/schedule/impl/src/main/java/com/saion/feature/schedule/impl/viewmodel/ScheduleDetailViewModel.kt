package com.saion.feature.schedule.impl.viewmodel

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.saion.core.domain.usecase.circle.ObserveResolvedCurrentCircleUseCase
import com.saion.core.domain.usecase.circle.ResolvedCurrentCircle
import com.saion.core.domain.usecase.member.GetMyInfoUseCase
import com.saion.core.domain.usecase.schedule.CancelConfirmationUseCase
import com.saion.core.domain.usecase.schedule.DeleteScheduleUseCase
import com.saion.core.domain.usecase.schedule.GetConfirmationTypesUseCase
import com.saion.core.domain.usecase.schedule.GetScheduleDetailUseCase
import com.saion.core.domain.usecase.schedule.RegisterConfirmationUseCase
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import com.saion.core.model.schedule.ConfirmationOption
import com.saion.core.model.schedule.ConfirmationType
import com.saion.core.model.schedule.ScheduleDetail
import com.saion.core.ui.error.toSnackbarMessage
import com.saion.core.ui.viewmodel.BaseViewModel
import com.saion.feature.schedule.impl.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@HiltViewModel
@Stable
internal class ScheduleDetailViewModel @Inject constructor(
    private val observeResolvedCurrentCircleUseCase: ObserveResolvedCurrentCircleUseCase,
    private val getScheduleDetailUseCase: GetScheduleDetailUseCase,
    private val getConfirmationTypesUseCase: GetConfirmationTypesUseCase,
    private val registerConfirmationUseCase: RegisterConfirmationUseCase,
    private val cancelConfirmationUseCase: CancelConfirmationUseCase,
    private val deleteScheduleUseCase: DeleteScheduleUseCase,
    private val getMyInfoUseCase: GetMyInfoUseCase,
) : BaseViewModel<ScheduleDetailState, ScheduleDetailEffect, ScheduleDetailIntent>(ScheduleDetailState()) {
    private var scheduleId: String? = null
    private var currentCircleId: String? = null
    private var myMemberId: String? = null

    init {
        viewModelScope.launch {
            observeResolvedCurrentCircleUseCase().collect { resolved ->
                currentCircleId = (resolved as? ResolvedCurrentCircle.Available)?.circleId
                if (resolved is ResolvedCurrentCircle.Available && scheduleId != null) {
                    load()
                }
            }
        }
    }

    override fun handleIntent(intent: ScheduleDetailIntent) {
        when (intent) {
            is ScheduleDetailIntent.Load -> {
                scheduleId = intent.scheduleId
                load()
            }
            is ScheduleDetailIntent.ConfirmationClicked -> updateConfirmation(intent.type)
            ScheduleDetailIntent.DeleteClicked -> update { copy(isDeleteDialogVisible = true) }
            ScheduleDetailIntent.DeleteDismissed -> update { copy(isDeleteDialogVisible = false) }
            ScheduleDetailIntent.DeleteConfirmed -> delete()
        }
    }

    private fun load() {
        val circleId = currentCircleId ?: return
        val scheduleId = scheduleId ?: return
        launchSafely(
            onStart = { update { copy(isLoading = true) } },
            onSuccess = { loaded -> update { loaded } },
            onFailure = { error ->
                update { copy(isLoading = false) }
                emitEffect(
                    ScheduleDetailEffect.ShowSnackbar(
                        error.toSnackbarMessage(
                            defaultMessageResId = R.string.schedule_detail_error_load,
                            textMessage = { value, resId -> ScheduleDetailSnackbarMessage.Text(value, resId) },
                            errorMessage = { appError, resId -> ScheduleDetailSnackbarMessage.Error(appError, resId) },
                        ),
                    ),
                )
            },
        ) {
            val detailDeferred = async { getScheduleDetailUseCase(circleId = circleId, scheduleId = scheduleId) }
            val optionDeferred = async { getConfirmationTypesUseCase(circleId = circleId) }
            val myInfoDeferred = async { getMyInfoUseCase() }

            when (val detailResult = detailDeferred.await()) {
                is AppResult.Failure -> detailResult
                is AppResult.Success -> {
                    val optionsResult = optionDeferred.await()
                    val myInfoResult = myInfoDeferred.await()
                    myMemberId = (myInfoResult as? AppResult.Success)?.data?.memberId
                    val options = (optionsResult as? AppResult.Success)?.data.orEmpty()
                    AppResult.Success(detailResult.data.toUiState(options = options, myMemberId = myMemberId))
                }
            }
        }
    }

    private fun updateConfirmation(type: ConfirmationType) {
        val circleId = currentCircleId ?: return
        val scheduleId = scheduleId ?: return
        val state = currentState
        val detail = state.detail ?: return
        if (state.isSubmitting) return

        val currentConfirmation = detail.myConfirmation
        val request = if (currentConfirmation?.confirmationType == type) {
            suspend { cancelConfirmationUseCase(circleId = circleId, scheduleId = scheduleId, confirmationId = currentConfirmation.confirmationId) }
        } else {
            suspend { registerConfirmationUseCase(circleId = circleId, scheduleId = scheduleId, confirmationType = type).toUnitResult() }
        }

        launchSafely(
            onStart = { update { copy(isSubmitting = true) } },
            onSuccess = {
                update { copy(isSubmitting = false) }
                load()
            },
            onFailure = { error ->
                update { copy(isSubmitting = false) }
                emitEffect(
                    ScheduleDetailEffect.ShowSnackbar(
                        error.toSnackbarMessage(
                            defaultMessageResId = R.string.schedule_detail_error_confirmation,
                            textMessage = { value, resId -> ScheduleDetailSnackbarMessage.Text(value, resId) },
                            errorMessage = { appError, resId -> ScheduleDetailSnackbarMessage.Error(appError, resId) },
                        ),
                    ),
                )
            },
        ) { request() }
    }

    private fun delete() {
        val circleId = currentCircleId ?: return
        val scheduleId = scheduleId ?: return
        launchSafely(
            onStart = { update { copy(isSubmitting = true, isDeleteDialogVisible = false) } },
            onSuccess = {
                update { copy(isSubmitting = false) }
                emitEffect(ScheduleDetailEffect.Deleted)
            },
            onFailure = { error ->
                update { copy(isSubmitting = false) }
                emitEffect(
                    ScheduleDetailEffect.ShowSnackbar(
                        error.toSnackbarMessage(
                            defaultMessageResId = R.string.schedule_detail_error_delete,
                            textMessage = { value, resId -> ScheduleDetailSnackbarMessage.Text(value, resId) },
                            errorMessage = { appError, resId -> ScheduleDetailSnackbarMessage.Error(appError, resId) },
                        ),
                    ),
                )
            },
        ) {
            deleteScheduleUseCase(circleId = circleId, scheduleId = scheduleId)
        }
    }
}

private fun ScheduleDetail.toUiState(
    options: List<ConfirmationOption>,
    myMemberId: String?,
): ScheduleDetailState = ScheduleDetailState(
    isLoading = false,
    isSubmitting = false,
    detail = this,
    confirmationOptions = options.map { option ->
        ScheduleConfirmationUiModel(
            type = option.value,
            label = option.label,
            count = confirmations.firstOrNull { it.type == option.value }?.count ?: 0,
            isSelected = myConfirmation?.confirmationType == option.value,
        )
    }.toImmutableList(),
    canDelete = myMemberId != null && createdBy == myMemberId,
    isDeleteDialogVisible = false,
)

private fun AppResult<*>.toUnitResult(): AppResult<Unit> = when (this) {
    is AppResult.Success -> AppResult.Success(Unit)
    is AppResult.Failure -> AppResult.Failure(error)
}
