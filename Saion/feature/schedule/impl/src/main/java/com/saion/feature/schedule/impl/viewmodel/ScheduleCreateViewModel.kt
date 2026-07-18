package com.saion.feature.schedule.impl.viewmodel

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.saion.core.domain.usecase.circle.ObserveResolvedCurrentCircleUseCase
import com.saion.core.domain.usecase.circle.ResolvedCurrentCircle
import com.saion.core.domain.usecase.schedule.CreateScheduleUseCase
import com.saion.core.model.schedule.CreateScheduleCommand
import com.saion.core.ui.error.toSnackbarMessage
import com.saion.core.ui.viewmodel.BaseViewModel
import com.saion.feature.schedule.impl.R
import com.saion.feature.schedule.impl.initialScheduleDateTime
import com.saion.feature.schedule.impl.toScheduleApiText
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
@Stable
internal class ScheduleCreateViewModel @Inject constructor(
    private val observeResolvedCurrentCircleUseCase: ObserveResolvedCurrentCircleUseCase,
    private val createScheduleUseCase: CreateScheduleUseCase,
) : BaseViewModel<ScheduleCreateState, ScheduleCreateEffect, ScheduleCreateIntent>(
    initialState = initialScheduleDateTime().let { (start, end) ->
        ScheduleCreateState(
            startDate = start.toLocalDate(),
            endDate = end.toLocalDate(),
            startTime = start.toLocalTime(),
            endTime = end.toLocalTime(),
        )
    },
) {
    private var currentCircleId: String? = null

    init {
        viewModelScope.launch {
            observeResolvedCurrentCircleUseCase().collect { resolved ->
                currentCircleId = (resolved as? ResolvedCurrentCircle.Available)?.circleId
            }
        }
    }

    override fun handleIntent(intent: ScheduleCreateIntent) {
        when (intent) {
            is ScheduleCreateIntent.TitleChanged -> update { copy(title = intent.value) }
            is ScheduleCreateIntent.StartDateChanged -> update { copy(startDate = intent.value) }
            is ScheduleCreateIntent.EndDateChanged -> update { copy(endDate = intent.value) }
            is ScheduleCreateIntent.StartTimeChanged -> update { copy(startTime = intent.value) }
            is ScheduleCreateIntent.EndTimeChanged -> update { copy(endTime = intent.value) }
            is ScheduleCreateIntent.NeedConfirmChanged -> update { copy(needConfirm = intent.value) }
            is ScheduleCreateIntent.MemoChanged -> update { copy(memo = intent.value) }
            ScheduleCreateIntent.SubmitClicked -> submit()
        }
    }

    private fun submit() {
        val circleId = currentCircleId
        if (circleId.isNullOrBlank()) {
            launchSafely(
                onSuccess = {},
            ) {
                emitEffect(
                    ScheduleCreateEffect.ShowSnackbar(
                        ScheduleCreateSnackbarMessage.Text(
                            value = "",
                            defaultMessageResId = R.string.schedule_create_error_circle,
                        ),
                    ),
                )
                com.saion.core.model.result.AppResult.Success(Unit)
            }
            return
        }

        val state = currentState
        if (state.isSubmitEnabled.not()) return

        launchSafely(
            onStart = { update { copy(isSubmitting = true) } },
            onSuccess = { created ->
                update { copy(isSubmitting = false) }
                emitEffect(ScheduleCreateEffect.NavigateToDetail(scheduleId = created.scheduleId))
            },
            onFailure = { error ->
                update { copy(isSubmitting = false) }
                emitEffect(
                    ScheduleCreateEffect.ShowSnackbar(
                        error.toSnackbarMessage(
                            defaultMessageResId = R.string.schedule_create_error_submit,
                            textMessage = { value, resId -> ScheduleCreateSnackbarMessage.Text(value, resId) },
                            errorMessage = { appError, resId -> ScheduleCreateSnackbarMessage.Error(appError, resId) },
                        ),
                    ),
                )
            },
        ) {
            createScheduleUseCase(
                circleId = circleId,
                command = CreateScheduleCommand(
                    title = state.title.trim(),
                    startDate = state.startDate.toScheduleApiText(),
                    endDate = state.endDate.toScheduleApiText(),
                    startTime = state.startTime.toScheduleApiText(),
                    endTime = state.endTime.toScheduleApiText(),
                    needConfirm = state.needConfirm,
                    memo = state.memo.trim().ifBlank { null },
                ),
            )
        }
    }
}
