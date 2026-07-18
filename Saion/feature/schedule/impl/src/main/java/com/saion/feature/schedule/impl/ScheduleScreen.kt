package com.saion.feature.schedule.impl

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.saion.core.model.schedule.ScheduleStatus
import com.saion.core.model.schedule.ScheduleSummary
import com.saion.core.model.schedule.ScheduleUrgencyLevel
import com.saion.core.ui.component.ScheduleAddCard
import com.saion.core.ui.component.SaionScaffold
import com.saion.core.ui.component.ScheduleSummaryCard
import com.saion.core.ui.component.SystemBarInset
import com.saion.core.ui.error.resolveMessage
import com.saion.core.ui.ext.CollectWithLifecycle
import com.saion.ds.component.feedback.SaionSpinner
import com.saion.ds.theme.SaionTheme
import com.saion.feature.schedule.impl.viewmodel.ScheduleEffect
import com.saion.feature.schedule.impl.viewmodel.ScheduleIntent
import com.saion.feature.schedule.impl.viewmodel.ScheduleSnackbarMessage
import com.saion.feature.schedule.impl.viewmodel.ScheduleState
import com.saion.feature.schedule.impl.viewmodel.ScheduleViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ScheduleScreen(
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit = {},
    onScheduleClick: (String) -> Unit = {},
    viewModel: ScheduleViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    viewModel.uiEffect.CollectWithLifecycle { effect ->
        when (effect) {
            is ScheduleEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message.resolve(context))
        }
    }

    ScheduleScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onRefresh = { viewModel.dispatch(ScheduleIntent.RefreshRequested) },
        onLoadNextPage = { viewModel.dispatch(ScheduleIntent.LoadNextPageRequested) },
        onAddClick = onAddClick,
        onScheduleClick = onScheduleClick,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleScreen(
    uiState: ScheduleState,
    snackbarHostState: SnackbarHostState,
    onRefresh: () -> Unit,
    onLoadNextPage: () -> Unit,
    onAddClick: () -> Unit,
    onScheduleClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    SaionScaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        systemBarInset = SystemBarInset.None,
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
        containerColor = SaionTheme.colors.background.muted,
    ) {
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize(),
        ) {
            when (uiState) {
                ScheduleState.Loading -> SaionSpinner()
                ScheduleState.Error -> PlaceholderContent(text = stringResource(R.string.schedule_error_fallback))
                is ScheduleState.Content -> ScheduleContent(
                    schedules = uiState.schedules,
                    hasNext = uiState.hasNext,
                    isAppending = uiState.isAppending,
                    onLoadNextPage = onLoadNextPage,
                    onAddClick = onAddClick,
                    onScheduleClick = onScheduleClick,
                )
            }
        }
    }
}

@Composable
private fun PlaceholderContent(
    text: String,
    modifier: Modifier = Modifier,
    action: @Composable (() -> Unit)? = null,
) {
    androidx.compose.foundation.layout.Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            androidx.compose.material3.Text(
                text = text,
                style = SaionTheme.typography.body1,
                color = SaionTheme.colors.label.subtle,
            )
            action?.invoke()
        }
    }
}

@Composable
private fun ScheduleContent(
    schedules: ImmutableList<ScheduleSummary>,
    hasNext: Boolean,
    isAppending: Boolean,
    onLoadNextPage: () -> Unit,
    onAddClick: () -> Unit,
    onScheduleClick: (String) -> Unit,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState, schedules.size, hasNext, isAppending) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }
            .filterNotNull()
            .map { lastVisibleIndex ->
                val footerCount = 1 + if (isAppending) 1 else 0
                val thresholdIndex = (schedules.lastIndex + footerCount - 2).coerceAtLeast(0)
                lastVisibleIndex >= thresholdIndex
            }
            .distinctUntilChanged()
            .collect { shouldLoadNext ->
                if (shouldLoadNext && hasNext && isAppending.not()) {
                    onLoadNextPage()
                }
            }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(items = schedules, key = ScheduleSummary::scheduleId) { schedule ->
            ScheduleSummaryCard(
                schedule = schedule,
                onClick = { onScheduleClick(schedule.scheduleId) },
            )
        }

        if (isAppending) {
            item(key = "append_loading") {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    SaionSpinner()
                }
            }
        }

        item(key = "schedule_add") {
            ScheduleAddCard(onClick = onAddClick)
        }
    }
}

private val ScheduleState.isRefreshing: Boolean
    get() = (this as? ScheduleState.Content)?.isRefreshing == true

private fun ScheduleSnackbarMessage.resolve(context: Context): String = when (this) {
    is ScheduleSnackbarMessage.Text -> value.ifBlank { context.getString(defaultMessageResId) }
    is ScheduleSnackbarMessage.Error -> error.resolveMessage(context, defaultMessageResId)
}

@Preview(showBackground = true)
@Composable
private fun ScheduleScreenContentPreview() {
    SaionTheme {
        ScheduleScreen(
            uiState = ScheduleState.Content(
                schedules = listOf(
                    ScheduleSummary(
                        scheduleId = "schedule-1",
                        title = "가족 식사",
                        startDate = "2026-07-18",
                        endDate = "2026-07-18",
                        startTime = "18:00",
                        endTime = "20:00",
                        isAllDay = false,
                        needConfirm = false,
                        status = ScheduleStatus.UPCOMING,
                        urgencyLevel = ScheduleUrgencyLevel.URGENT,
                        progressRate = 0,
                        dday = 1,
                    ),
                    ScheduleSummary(
                        scheduleId = "schedule-2",
                        title = "영화 보기",
                        startDate = "2026-07-20",
                        endDate = "2026-07-20",
                        startTime = "20:00",
                        endTime = "22:00",
                        isAllDay = false,
                        needConfirm = false,
                        status = ScheduleStatus.UPCOMING,
                        urgencyLevel = ScheduleUrgencyLevel.NORMAL,
                        progressRate = 0,
                        dday = 10,
                    ),
                ).toImmutableList(),
                isRefreshing = false,
                isAppending = false,
                nextCursor = "next-1",
                hasNext = true,
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onRefresh = {},
            onLoadNextPage = {},
            onAddClick = {},
            onScheduleClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ScheduleScreenEmptyPreview() {
    SaionTheme {
        ScheduleScreen(
            uiState = ScheduleState.Content(
                schedules = persistentListOf(),
                isRefreshing = false,
                isAppending = false,
                nextCursor = null,
                hasNext = false,
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onRefresh = {},
            onLoadNextPage = {},
            onAddClick = {},
            onScheduleClick = {},
        )
    }
}
