package com.saion.feature.schedule.impl.viewmodel

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.saion.core.domain.usecase.circle.ObserveResolvedCurrentCircleUseCase
import com.saion.core.domain.usecase.circle.ResolvedCurrentCircle
import com.saion.core.domain.usecase.schedule.GetScheduleListUseCase
import com.saion.core.model.schedule.ScheduleListPage
import com.saion.core.model.schedule.ScheduleSummary
import com.saion.core.ui.error.toSnackbarMessage
import com.saion.core.ui.viewmodel.BaseViewModel
import com.saion.feature.schedule.impl.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

private const val DEFAULT_PAGE_SIZE: Int = 20

@HiltViewModel
@Stable
internal class ScheduleViewModel @Inject constructor(
    private val observeResolvedCurrentCircleUseCase: ObserveResolvedCurrentCircleUseCase,
    private val getScheduleListUseCase: GetScheduleListUseCase,
) : BaseViewModel<ScheduleState, ScheduleEffect, ScheduleIntent>(ScheduleState.Loading) {
    private var currentCircleId: String? = null

    init {
        viewModelScope.launch {
            observeResolvedCurrentCircleUseCase()
                .collect(::handleResolvedCurrentCircle)
        }
    }

    override fun handleIntent(intent: ScheduleIntent) {
        when (intent) {
            ScheduleIntent.RefreshRequested -> refresh()
            ScheduleIntent.LoadNextPageRequested -> loadNextPage()
        }
    }

    private fun handleResolvedCurrentCircle(resolvedCurrentCircle: ResolvedCurrentCircle) {
        when (resolvedCurrentCircle) {
            is ResolvedCurrentCircle.Available -> {
                if (currentCircleId != resolvedCurrentCircle.circleId) {
                    currentCircleId = resolvedCurrentCircle.circleId
                    loadInitial(circleId = resolvedCurrentCircle.circleId)
                }
            }

            ResolvedCurrentCircle.Missing -> {
                currentCircleId = null
                update {
                    ScheduleState.Content(
                        schedules = emptyList<ScheduleSummary>().toImmutableList(),
                        isRefreshing = false,
                        isAppending = false,
                        nextCursor = null,
                        hasNext = false,
                    )
                }
            }
        }
    }

    private fun loadInitial(circleId: String) {
        launchSafely(
            onStart = { update { ScheduleState.Loading } },
            onSuccess = { page ->
                update { page.toContentState() }
            },
            onFailure = { error ->
                update { ScheduleState.Error }
                emitEffect(
                    ScheduleEffect.ShowSnackbar(
                        error.toSnackbarMessage(
                            defaultMessageResId = R.string.schedule_error_load,
                            textMessage = { value, resId -> ScheduleSnackbarMessage.Text(value, resId) },
                            errorMessage = { appError, resId -> ScheduleSnackbarMessage.Error(appError, resId) },
                        ),
                    ),
                )
            },
        ) {
            getScheduleListUseCase(
                circleId = circleId,
                size = DEFAULT_PAGE_SIZE,
            )
        }
    }

    private fun refresh() {
        val circleId = currentCircleId ?: run {
            update {
                ScheduleState.Content(
                    schedules = emptyList<ScheduleSummary>().toImmutableList(),
                    isRefreshing = false,
                    isAppending = false,
                    nextCursor = null,
                    hasNext = false,
                )
            }
            return
        }
        val previousState = currentState

        launchSafely(
            onStart = {
                if (previousState is ScheduleState.Content) {
                    update {
                        previousState.copy(
                            isRefreshing = true,
                            isAppending = false,
                        )
                    }
                } else {
                    update { ScheduleState.Loading }
                }
            },
            onSuccess = { page ->
                update { page.toContentState() }
            },
            onFailure = { error ->
                update {
                    when (previousState) {
                        is ScheduleState.Content -> previousState.copy(
                            isRefreshing = false,
                            isAppending = false,
                        )

                        else -> ScheduleState.Error
                    }
                }
                emitEffect(
                    ScheduleEffect.ShowSnackbar(
                        error.toSnackbarMessage(
                            defaultMessageResId = R.string.schedule_error_refresh,
                            textMessage = { value, resId -> ScheduleSnackbarMessage.Text(value, resId) },
                            errorMessage = { appError, resId -> ScheduleSnackbarMessage.Error(appError, resId) },
                        ),
                    ),
                )
            },
        ) {
            getScheduleListUseCase(
                circleId = circleId,
                size = DEFAULT_PAGE_SIZE,
            )
        }
    }

    private fun loadNextPage() {
        val circleId = currentCircleId ?: return
        val state = currentState as? ScheduleState.Content ?: return
        if (state.isRefreshing || state.isAppending || state.hasNext.not()) return

        launchSafely(
            onStart = {
                updateCurrentContent { copy(isAppending = true) }
            },
            onSuccess = { page ->
                appendPage(page = page)
            },
            onFailure = { error ->
                updateCurrentContent { copy(isAppending = false) }
                emitEffect(
                    ScheduleEffect.ShowSnackbar(
                        error.toSnackbarMessage(
                            defaultMessageResId = R.string.schedule_error_load_more,
                            textMessage = { value, resId -> ScheduleSnackbarMessage.Text(value, resId) },
                            errorMessage = { appError, resId -> ScheduleSnackbarMessage.Error(appError, resId) },
                        ),
                    ),
                )
            },
        ) {
            getScheduleListUseCase(
                circleId = circleId,
                cursor = state.nextCursor,
                size = DEFAULT_PAGE_SIZE,
            )
        }
    }

    private fun appendPage(page: ScheduleListPage) {
        val state = currentState as? ScheduleState.Content ?: return
        val mergedSchedules: List<ScheduleSummary> = state.schedules + page.schedules

        update {
            state.copy(
                schedules = mergedSchedules.toImmutableList(),
                isRefreshing = false,
                isAppending = false,
                nextCursor = page.nextCursor,
                hasNext = page.hasNext,
            )
        }
    }

    private fun updateCurrentContent(block: ScheduleState.Content.() -> ScheduleState.Content) {
        val state = currentState as? ScheduleState.Content ?: return
        update { if (this is ScheduleState.Content) block(this) else state }
    }
}
