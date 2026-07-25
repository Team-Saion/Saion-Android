package com.saion.feature.schedule.impl.viewmodel

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.saion.core.domain.usecase.circle.ObserveResolvedCurrentCircleUseCase
import com.saion.core.domain.usecase.circle.ResolvedCurrentCircle
import com.saion.core.domain.usecase.member.GetMyInfoUseCase
import com.saion.core.domain.usecase.schedule.CancelConfirmationUseCase
import com.saion.core.domain.usecase.schedule.DeleteScheduleUseCase
import com.saion.core.domain.usecase.schedule.GetConfirmationTypesUseCase
import com.saion.core.domain.usecase.schedule.ObserveScheduleDetailUseCase
import com.saion.core.domain.usecase.schedule.RefreshScheduleDetailUseCase
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
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
@HiltViewModel
@Stable
internal class ScheduleDetailViewModel @Inject constructor(
    private val observeResolvedCurrentCircleUseCase: ObserveResolvedCurrentCircleUseCase,
    private val observeScheduleDetailUseCase: ObserveScheduleDetailUseCase,
    private val refreshScheduleDetailUseCase: RefreshScheduleDetailUseCase,
    private val getConfirmationTypesUseCase: GetConfirmationTypesUseCase,
    private val registerConfirmationUseCase: RegisterConfirmationUseCase,
    private val cancelConfirmationUseCase: CancelConfirmationUseCase,
    private val deleteScheduleUseCase: DeleteScheduleUseCase,
    private val getMyInfoUseCase: GetMyInfoUseCase,
) : BaseViewModel<ScheduleDetailState, ScheduleDetailEffect, ScheduleDetailIntent>(ScheduleDetailState()) {
    private var scheduleId: String? = null
    private var currentCircleId: String? = null
    private var latestDetail: ScheduleDetail? = null
    private var optimisticConfirmation: ConfirmationSelection? = null
    private var latestDebouncedConfirmation: ConfirmationSelection? = null
    private var myMemberId: String? = null
    private var confirmationOptions: List<ConfirmationOption> = emptyList()
    private var scheduleDetailJob: Job? = null
    private var confirmationUpdateJob: Job? = null
    private val pendingConfirmationUpdates = MutableSharedFlow<ConfirmationSelection>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    init {
        observeConfirmationUpdates()
        viewModelScope.launch {
            observeResolvedCurrentCircleUseCase().collect { resolved ->
                currentCircleId = (resolved as? ResolvedCurrentCircle.Available)?.circleId
                if (resolved is ResolvedCurrentCircle.Available && scheduleId != null) {
                    observeScheduleDetail(circleId = resolved.circleId, scheduleId = scheduleId.orEmpty())
                    load()
                } else if (resolved is ResolvedCurrentCircle.Missing) {
                    latestDetail = null
                    optimisticConfirmation = null
                    latestDebouncedConfirmation = null
                    rebuildUiState()
                }
            }
        }
    }

    override fun handleIntent(intent: ScheduleDetailIntent) {
        when (intent) {
            is ScheduleDetailIntent.Load -> {
                scheduleId = intent.scheduleId
                currentCircleId?.let { observeScheduleDetail(circleId = it, scheduleId = intent.scheduleId) }
                load()
            }
            is ScheduleDetailIntent.ConfirmationClicked -> handleConfirmationClick(intent.type)
            ScheduleDetailIntent.DeleteClicked -> update { copy(isDeleteDialogVisible = true) }
            ScheduleDetailIntent.DeleteDismissed -> update { copy(isDeleteDialogVisible = false) }
            ScheduleDetailIntent.DeleteConfirmed -> delete()
        }
    }

    private fun observeConfirmationUpdates() {
        viewModelScope.launch {
            pendingConfirmationUpdates
                .debounce(CONFIRMATION_DEBOUNCE_MILLIS)
                .collect { confirmation ->
                    latestDebouncedConfirmation = confirmation
                    syncConfirmationUpdate()
                }
        }
    }

    private fun handleConfirmationClick(type: ConfirmationType) {
        if (currentState.isSubmitting) return
        val currentSelectedType = currentState.confirmationOptions.firstOrNull { it.isSelected }?.type
        val nextSelection = ConfirmationSelection(
            type = if (currentSelectedType == type) null else type,
        )
        optimisticConfirmation = nextSelection
        rebuildUiState()
        pendingConfirmationUpdates.tryEmit(nextSelection)
    }

    private fun observeScheduleDetail(
        circleId: String,
        scheduleId: String,
    ) {
        scheduleDetailJob?.cancel()
        scheduleDetailJob = viewModelScope.launch {
            observeScheduleDetailUseCase(circleId = circleId, scheduleId = scheduleId)
                .filterNotNull()
                .collect { detail ->
                    latestDetail = detail
                    if (optimisticConfirmation?.type == detail.myConfirmation?.confirmationType) {
                        optimisticConfirmation = null
                    }
                    rebuildUiState()
                    syncConfirmationUpdate()
                }
        }
    }

    private fun load() {
        val circleId = currentCircleId ?: return
        val scheduleId = scheduleId ?: return
        launchSafely(
            onStart = { update { copy(isLoading = true) } },
            onSuccess = { detail ->
                latestDetail = detail
                rebuildUiState(isLoading = false, isSubmitting = false)
            },
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
            val detailDeferred = async { refreshScheduleDetailUseCase(circleId = circleId, scheduleId = scheduleId) }
            val optionDeferred = async { getConfirmationTypesUseCase(circleId = circleId) }
            val myInfoDeferred = async { getMyInfoUseCase() }

            when (val detailResult = detailDeferred.await()) {
                is AppResult.Failure -> detailResult
                is AppResult.Success -> {
                    val optionsResult = optionDeferred.await()
                    val myInfoResult = myInfoDeferred.await()
                    myMemberId = (myInfoResult as? AppResult.Success)?.data?.memberId
                    confirmationOptions = (optionsResult as? AppResult.Success)?.data.orEmpty()
                    rebuildUiState()
                    AppResult.Success(detailResult.data)
                }
            }
        }
    }

    private fun syncConfirmationUpdate() {
        val circleId = currentCircleId ?: return
        val scheduleId = scheduleId ?: return
        val detail = latestDetail ?: return
        val desiredConfirmation = latestDebouncedConfirmation ?: return
        if (currentState.isSubmitting || confirmationUpdateJob?.isActive == true) return

        val serverConfirmation = detail.myConfirmation
        if (serverConfirmation?.confirmationType == desiredConfirmation.type) {
            latestDebouncedConfirmation = null
            if (optimisticConfirmation == desiredConfirmation) {
                optimisticConfirmation = null
                rebuildUiState()
            }
            return
        }

        val request = if (desiredConfirmation.type == null) {
            val confirmationId = serverConfirmation?.confirmationId ?: return
            suspend { cancelConfirmationUseCase(circleId = circleId, scheduleId = scheduleId, confirmationId = confirmationId) }
        } else {
            suspend { registerConfirmationUseCase(circleId = circleId, scheduleId = scheduleId, confirmationType = desiredConfirmation.type).toUnitResult() }
        }

        confirmationUpdateJob = launchSafely(
            onSuccess = {
                confirmationUpdateJob = null
            },
            onFailure = { error ->
                confirmationUpdateJob = null
                latestDebouncedConfirmation = null
                optimisticConfirmation = null
                rebuildUiState()
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
            onStart = {
                optimisticConfirmation = null
                latestDebouncedConfirmation = null
                update { copy(isSubmitting = true, isDeleteDialogVisible = false) }
            },
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

    private fun updateCurrentDetailContent(block: ScheduleDetailState.() -> ScheduleDetailState) {
        val state = currentState
        update { block(state) }
    }

    private fun rebuildUiState(
        isLoading: Boolean = currentState.isLoading,
        isSubmitting: Boolean = currentState.isSubmitting,
    ) {
        val detail = latestDetail
        val deleteDialogVisible = currentState.isDeleteDialogVisible
        update {
            if (detail == null) {
                ScheduleDetailState(
                    isLoading = isLoading,
                    isSubmitting = isSubmitting,
                    detail = null,
                    confirmationOptions = persistentListOf(),
                    canDelete = false,
                    isDeleteDialogVisible = deleteDialogVisible,
                )
            } else {
                detail.toUiState(
                    options = this@ScheduleDetailViewModel.confirmationOptions,
                    myMemberId = myMemberId,
                    optimisticConfirmation = optimisticConfirmation,
                    isLoading = isLoading,
                    isSubmitting = isSubmitting,
                    isDeleteDialogVisible = deleteDialogVisible,
                )
            }
        }
    }
}

private fun ScheduleDetail.toUiState(
    options: List<ConfirmationOption>,
    myMemberId: String?,
    optimisticConfirmation: ConfirmationSelection? = null,
    isLoading: Boolean = false,
    isSubmitting: Boolean = false,
    isDeleteDialogVisible: Boolean = false,
): ScheduleDetailState = ScheduleDetailState(
    isLoading = isLoading,
    isSubmitting = isSubmitting,
    detail = this,
    confirmationOptions = options.map { option ->
        val serverConfirmation = myConfirmation?.confirmationType
        val effectiveConfirmation = if (optimisticConfirmation != null) {
            optimisticConfirmation.type
        } else {
            serverConfirmation
        }
        val countAdjustment = when {
            serverConfirmation == option.value && effectiveConfirmation != option.value -> -1
            serverConfirmation != option.value && effectiveConfirmation == option.value -> 1
            else -> 0
        }
        ScheduleConfirmationUiModel(
            type = option.value,
            label = option.label,
            count = (confirmations.firstOrNull { it.type == option.value }?.count ?: 0) + countAdjustment,
            isSelected = effectiveConfirmation == option.value,
        )
    }.toImmutableList(),
    canDelete = myMemberId != null && createdBy == myMemberId,
    isDeleteDialogVisible = isDeleteDialogVisible,
)

private fun AppResult<*>.toUnitResult(): AppResult<Unit> = when (this) {
    is AppResult.Success -> AppResult.Success(Unit)
    is AppResult.Failure -> AppResult.Failure(error)
}

private data class ConfirmationSelection(
    val type: ConfirmationType?,
)

private const val CONFIRMATION_DEBOUNCE_MILLIS = 300L
