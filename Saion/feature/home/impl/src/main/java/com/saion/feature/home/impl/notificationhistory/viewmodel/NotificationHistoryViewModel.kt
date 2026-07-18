package com.saion.feature.home.impl.notificationhistory.viewmodel

import androidx.compose.runtime.Stable
import com.saion.core.domain.usecase.notification.GetNotificationInboxUseCase
import com.saion.core.domain.usecase.notification.MarkNotificationReadUseCase
import com.saion.core.model.notification.NotificationInboxItem
import com.saion.core.model.notification.NotificationRouteType
import com.saion.core.model.result.AppResult
import com.saion.core.ui.error.toSnackbarMessage
import com.saion.core.ui.viewmodel.BaseViewModel
import com.saion.feature.home.impl.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.toImmutableList

@HiltViewModel
@Stable
internal class NotificationHistoryViewModel @Inject constructor(
    private val getNotificationInboxUseCase: GetNotificationInboxUseCase,
    private val markNotificationReadUseCase: MarkNotificationReadUseCase,
) : BaseViewModel<NotificationHistoryState, NotificationHistoryEffect, NotificationHistoryIntent>(NotificationHistoryState()) {
    init {
        loadNotifications()
    }

    override fun handleIntent(intent: NotificationHistoryIntent) {
        when (intent) {
            is NotificationHistoryIntent.NotificationClicked -> handleNotificationClick(intent.item)
        }
    }

    private fun loadNotifications() {
        launchSafely(
            onStart = {
                update {
                    copy(
                        isLoading = true,
                        isLoadFailed = false,
                    )
                }
            },
            onSuccess = { page ->
                update {
                    copy(
                        items = page.items.toImmutableList(),
                        isLoading = false,
                        isLoadFailed = false,
                    )
                }
            },
            onFailure = { error ->
                update {
                    copy(
                        isLoading = false,
                        isLoadFailed = items.isEmpty(),
                    )
                }
                emitEffect(
                    NotificationHistoryEffect.ShowSnackbar(
                        error.toSnackbarMessage(
                            defaultMessageResId = R.string.notification_history_error_load,
                            textMessage = { value, resId ->
                                NotificationHistorySnackbarMessage.Text(value, resId)
                            },
                            errorMessage = { appError, resId ->
                                NotificationHistorySnackbarMessage.Error(appError, resId)
                            },
                        ),
                    ),
                )
            },
        ) {
            getNotificationInboxUseCase(size = DEFAULT_PAGE_SIZE)
        }
    }

    private fun handleNotificationClick(item: NotificationInboxItem) {
        if (item.readAt != null) {
            navigateByRoute(item)
            return
        }

        launchSafely(
            onSuccess = { updatedItem ->
                update {
                    copy(
                        items = items.map { current ->
                            if (current.id == updatedItem.id) updatedItem else current
                        }.toImmutableList(),
                    )
                }
                navigateByRoute(updatedItem)
            },
            onFailure = { error ->
                emitEffect(
                    NotificationHistoryEffect.ShowSnackbar(
                        error.toSnackbarMessage(
                            defaultMessageResId = R.string.notification_history_error_read,
                            textMessage = { value, resId ->
                                NotificationHistorySnackbarMessage.Text(value, resId)
                            },
                            errorMessage = { appError, resId ->
                                NotificationHistorySnackbarMessage.Error(appError, resId)
                            },
                        ),
                    ),
                )
            },
        ) {
            markNotificationReadUseCase(item.id)
        }
    }

    private fun navigateByRoute(item: NotificationInboxItem) {
        when (item.route.type) {
            NotificationRouteType.CIRCLE_HOME,
            NotificationRouteType.HOME,
            -> launchNavigation(NotificationHistoryEffect.NavigateToHome)

            NotificationRouteType.SCHEDULE_LIST,
            -> launchNavigation(NotificationHistoryEffect.NavigateToSchedule)

            NotificationRouteType.SCHEDULE_DETAIL -> {
                val scheduleId = item.route.scheduleId
                if (scheduleId.isNullOrBlank()) {
                    launchInvalidScheduleRouteSnackbar()
                } else {
                    launchNavigation(NotificationHistoryEffect.NavigateToScheduleDetail(scheduleId))
                }
            }
        }
    }

    private fun launchNavigation(effect: NotificationHistoryEffect) {
        launchSafely(
            onSuccess = {},
        ) {
            emitEffect(effect)
            AppResult.Success(Unit)
        }
    }

    private fun launchInvalidScheduleRouteSnackbar() {
        launchSafely(
            onSuccess = {},
        ) {
            emitEffect(
                NotificationHistoryEffect.ShowSnackbar(
                    NotificationHistorySnackbarMessage.Text(
                        value = "",
                        defaultMessageResId = R.string.notification_history_error_invalid_schedule_route,
                    ),
                ),
            )
            AppResult.Success(Unit)
        }
    }

    private companion object {
        const val DEFAULT_PAGE_SIZE: Int = 30
    }
}
