package com.saion.feature.home.impl.notificationhistory

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.saion.core.model.notification.NotificationInboxItem
import com.saion.core.model.notification.NotificationRoute
import com.saion.core.model.notification.NotificationRouteType
import com.saion.core.model.notification.NotificationType
import com.saion.core.ui.component.SaionPullToRefreshBox
import com.saion.core.ui.component.SaionScaffold
import com.saion.core.ui.error.resolveMessage
import com.saion.core.ui.ext.CollectWithLifecycle
import com.saion.core.ui.ext.noRippleClickable
import com.saion.ds.component.button.IconButtonSize
import com.saion.ds.component.button.SaionIconButton
import com.saion.ds.component.feedback.SaionSpinner
import com.saion.ds.component.navigation.SaionTopBar
import com.saion.ds.component.navigation.TopBarVariant
import com.saion.ds.icon.SaionIcons
import com.saion.ds.theme.SaionTheme
import com.saion.feature.home.impl.R
import com.saion.feature.home.impl.notificationhistory.viewmodel.NotificationHistoryEffect
import com.saion.feature.home.impl.notificationhistory.viewmodel.NotificationHistoryIntent
import com.saion.feature.home.impl.notificationhistory.viewmodel.NotificationHistorySnackbarMessage
import com.saion.feature.home.impl.notificationhistory.viewmodel.NotificationHistoryState
import com.saion.feature.home.impl.notificationhistory.viewmodel.NotificationHistoryViewModel
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeParseException
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun NotificationHistoryScreen(
    onBack: () -> Unit,
    onSettingsClick: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToSchedule: () -> Unit,
    onNavigateToScheduleDetail: (String) -> Unit,
    viewModel: NotificationHistoryViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    viewModel.uiEffect.CollectWithLifecycle { effect ->
        when (effect) {
            is NotificationHistoryEffect.ShowSnackbar -> {
                snackbarHostState.showSnackbar(effect.message.resolve(context))
            }

            NotificationHistoryEffect.NavigateToHome -> onNavigateToHome()

            NotificationHistoryEffect.NavigateToSchedule -> onNavigateToSchedule()

            is NotificationHistoryEffect.NavigateToScheduleDetail -> onNavigateToScheduleDetail(effect.scheduleId)
        }
    }

    NotificationHistoryScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onSettingsClick = onSettingsClick,
        onRefresh = { viewModel.dispatch(NotificationHistoryIntent.RefreshRequested) },
        onNotificationClick = { item ->
            viewModel.dispatch(NotificationHistoryIntent.NotificationClicked(item))
        },
    )
}

@Composable
private fun NotificationHistoryScreen(
    uiState: NotificationHistoryState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onSettingsClick: () -> Unit,
    onRefresh: () -> Unit,
    onNotificationClick: (NotificationInboxItem) -> Unit,
) {
    SaionScaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            SaionTopBar(
                modifier = Modifier.statusBarsPadding(),
                variant = TopBarVariant.Standard(
                    title = stringResource(R.string.notification_history_title),
                    onBack = onBack,
                ),
                actions = {
                    SaionIconButton(
                        icon = SaionIcons.Settings,
                        size = IconButtonSize.LARGE,
                        tint = SaionTheme.colors.label.subtle,
                        contentDescription = stringResource(R.string.notification_history_settings),
                        onClick = onSettingsClick,
                    )
                },
            )
        },
    ) {
        SaionPullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize(),
        ) {
            when {
                uiState.isLoading && uiState.items.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        SaionSpinner()
                    }
                }

                uiState.items.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(
                                if (uiState.isLoadFailed) {
                                    R.string.notification_history_error_load
                                } else {
                                    R.string.notification_history_empty
                                },
                            ),
                            style = SaionTheme.typography.body1,
                            color = SaionTheme.colors.label.subtle,
                        )
                    }
                }

                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(
                            items = uiState.items,
                            key = NotificationInboxItem::id,
                        ) { item ->
                            NotificationHistoryItem(
                                item = item,
                                onClick = { onNotificationClick(item) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationHistoryItem(
    item: NotificationInboxItem,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable(onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = item.title,
                modifier = Modifier.weight(1f),
                style = SaionTheme.typography.title3Strong,
                color = SaionTheme.colors.label.default,
            )
            Text(
                text = item.occurredAt.formatAsNotificationTime(),
                style = SaionTheme.typography.caption2,
                color = SaionTheme.colors.label.subtle,
            )
        }

        Text(
            text = item.body,
            style = SaionTheme.typography.caption1,
            color = SaionTheme.colors.label.subtle,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private fun NotificationHistorySnackbarMessage.resolve(context: Context): String = when (this) {
    is NotificationHistorySnackbarMessage.Text -> value.ifBlank { context.getString(defaultMessageResId) }
    is NotificationHistorySnackbarMessage.Error -> error.resolveMessage(context, defaultMessageResId)
}

@Composable
private fun String.formatAsNotificationTime(clock: Clock = Clock.systemDefaultZone()): String {
    val context = androidx.compose.ui.platform.LocalContext.current
    return formatNotificationTime(
        occurredAt = this,
        context = context,
        clock = clock,
    )
}

internal fun formatNotificationTime(
    occurredAt: String,
    context: Context,
    clock: Clock = Clock.systemDefaultZone(),
): String {
    val zoneId = clock.zone
    val now = ZonedDateTime.now(clock)
    val occurredDateTime = occurredAt.parseNotificationDateTime(zoneId) ?: return occurredAt
    val duration = java.time.Duration.between(occurredDateTime, now)
    val minutes = duration.toMinutes()
    val hours = duration.toHours()

    return when {
        occurredDateTime.toLocalDate() == now.toLocalDate() && minutes < 1 -> {
            context.getString(R.string.notification_history_time_now)
        }

        occurredDateTime.toLocalDate() == now.toLocalDate() && minutes < 60 -> {
            context.getString(R.string.notification_history_time_minutes, minutes.toInt())
        }

        occurredDateTime.toLocalDate() == now.toLocalDate() && hours < 24 -> {
            context.getString(R.string.notification_history_time_hours, hours.toInt())
        }

        occurredDateTime.year == now.year -> {
            context.getString(
                R.string.notification_history_time_date_this_year,
                occurredDateTime.monthValue,
                occurredDateTime.dayOfMonth,
            )
        }

        else -> {
            context.getString(
                R.string.notification_history_time_date_full,
                occurredDateTime.year,
                occurredDateTime.monthValue,
                occurredDateTime.dayOfMonth,
            )
        }
    }
}

private fun String.parseNotificationDateTime(zoneId: ZoneId): ZonedDateTime? = sequenceOf(
    { Instant.parse(this).atZone(zoneId) },
    { OffsetDateTime.parse(this).toZonedDateTime().withZoneSameInstant(zoneId) },
    { LocalDateTime.parse(this).atZone(zoneId) },
    { LocalDate.parse(this).atStartOfDay(zoneId) },
).firstNotNullOfOrNull { parser ->
    try {
        parser()
    } catch (_: DateTimeParseException) {
        null
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationHistoryScreenPreview() {
    SaionTheme {
        NotificationHistoryScreen(
            uiState = NotificationHistoryState(
                items = persistentListOf(
                    NotificationInboxItem(
                        id = 1L,
                        type = NotificationType.SCHEDULE_CONFIRMATION_REQUESTED,
                        title = "가족 일정 확인했나요?",
                        body = "'{일정 제목}', 아직 확인 전이에요. 확인 응답을 눌러주세요.",
                        occurredAt = "2026-07-17T08:41:00",
                        readAt = null,
                        route = NotificationRoute(
                            type = NotificationRouteType.HOME,
                            circleId = null,
                            scheduleId = null,
                        ),
                    ),
                    NotificationInboxItem(
                        id = 2L,
                        type = NotificationType.SCHEDULE_REMINDER_DDAY_ALL_DAY,
                        title = "오늘 가족 일정이 있어요",
                        body = "'{일정 제목}', 확인하고 알려주세요.",
                        occurredAt = "2026-07-17T07:41:00",
                        readAt = null,
                        route = NotificationRoute(
                            type = NotificationRouteType.SCHEDULE_LIST,
                            circleId = null,
                            scheduleId = null,
                        ),
                    ),
                ),
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onBack = {},
            onSettingsClick = {},
            onRefresh = {},
            onNotificationClick = {},
        )
    }
}
