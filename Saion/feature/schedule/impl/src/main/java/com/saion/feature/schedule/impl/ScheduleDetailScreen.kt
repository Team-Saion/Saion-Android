package com.saion.feature.schedule.impl

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.saion.core.model.schedule.ConfirmationCount
import com.saion.core.model.schedule.ConfirmationType
import com.saion.core.model.schedule.MyConfirmation
import com.saion.core.model.schedule.ScheduleDetail
import com.saion.core.model.schedule.ScheduleStatus
import com.saion.core.ui.component.DdayBadge
import com.saion.core.ui.component.DdayBadgeSize
import com.saion.core.ui.component.SaionScaffold
import com.saion.core.ui.component.ScheduleProgressSection
import com.saion.core.ui.error.resolveMessage
import com.saion.core.ui.ext.CollectWithLifecycle
import com.saion.ds.component.button.ButtonSize
import com.saion.ds.component.button.ButtonVariant
import com.saion.ds.component.button.SaionBottomCTA
import com.saion.ds.component.button.SaionButton
import com.saion.ds.component.feedback.SaionConfirmDialog
import com.saion.ds.component.feedback.SaionSpinner
import com.saion.ds.component.input.SaionTextArea
import com.saion.ds.component.input.SaionTextAreaVariant
import com.saion.ds.component.navigation.SaionTopBar
import com.saion.ds.component.navigation.TopBarVariant
import com.saion.ds.component.selection.ChipShape
import com.saion.ds.component.selection.SaionChip
import com.saion.ds.icon.SaionIcons
import com.saion.ds.theme.SaionTheme
import com.saion.feature.schedule.impl.viewmodel.ScheduleConfirmationUiModel
import com.saion.feature.schedule.impl.viewmodel.ScheduleDetailEffect
import com.saion.feature.schedule.impl.viewmodel.ScheduleDetailIntent
import com.saion.feature.schedule.impl.viewmodel.ScheduleDetailSnackbarMessage
import com.saion.feature.schedule.impl.viewmodel.ScheduleDetailState
import com.saion.feature.schedule.impl.viewmodel.ScheduleDetailViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun ScheduleDetailScreen(
    scheduleId: String,
    onBack: () -> Unit,
    onDeleted: () -> Unit,
    viewModel: ScheduleDetailViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(scheduleId) {
        viewModel.dispatch(ScheduleDetailIntent.Load(scheduleId = scheduleId))
    }

    viewModel.uiEffect.CollectWithLifecycle { effect ->
        when (effect) {
            ScheduleDetailEffect.Deleted -> onDeleted()
            is ScheduleDetailEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message.resolve(context))
        }
    }

    ScheduleDetailScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onIntent = viewModel::dispatch,
    )
}

@Composable
private fun ScheduleDetailScreen(
    uiState: ScheduleDetailState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onIntent: (ScheduleDetailIntent) -> Unit,
) {
    SaionScaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            SaionTopBar(
                variant = TopBarVariant.Standard(onBack = onBack),
                containerColor = Color.Transparent,
            )
        },
        bottomBar = {
            if (uiState.canDelete && uiState.detail != null) {
                SaionBottomCTA {
                    SaionButton(
                        text = stringResource(R.string.schedule_detail_delete),
                        onClick = { onIntent(ScheduleDetailIntent.DeleteClicked) },
                        size = ButtonSize.LARGE,
                        modifier = Modifier,
                        variant = ButtonVariant.DANGER,
                        enabled = uiState.isSubmitting.not(),
                    )
                }
            }
        },
        containerColor = Color(0xFFF7F7F4),
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> SaionSpinner()

                uiState.detail == null -> EmptyDetailContent()

                else -> ScheduleDetailContent(
                    state = uiState,
                    onIntent = onIntent,
                )
            }

            if (uiState.isSubmitting && uiState.detail != null) {
                SaionSpinner()
            }
        }
    }

    if (uiState.isDeleteDialogVisible) {
        SaionConfirmDialog(
            title = stringResource(R.string.schedule_detail_delete_confirm_title),
            confirmButtonText = stringResource(R.string.schedule_detail_delete_confirm),
            onConfirm = { onIntent(ScheduleDetailIntent.DeleteConfirmed) },
            dismissButtonText = stringResource(R.string.schedule_detail_delete_cancel),
            onDismiss = { onIntent(ScheduleDetailIntent.DeleteDismissed) },
            isDanger = true,
        )
    }
}

@Composable
private fun EmptyDetailContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.schedule_detail_error_empty),
            style = SaionTheme.typography.body1,
            color = SaionTheme.colors.label.subtle,
        )
    }
}

@Composable
private fun ScheduleDetailContent(
    state: ScheduleDetailState,
    onIntent: (ScheduleDetailIntent) -> Unit,
) {
    val detail = requireNotNull(state.detail)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        DdayBadge(
            dday = detail.dday,
            size = DdayBadgeSize.LARGE,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = detail.title,
            style = SaionTheme.typography.heading1,
            color = SaionTheme.colors.label.strong,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = formatScheduleDateText(detail.startDate),
            style = SaionTheme.typography.body1,
            color = SaionTheme.colors.label.strong,
        )

        val timeText = formatScheduleTimeRange(
            isAllDay = detail.isAllDay,
            startTime = detail.startTime,
            endTime = detail.endTime,
        )
        if (timeText.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = timeText,
                style = SaionTheme.typography.body1,
                color = SaionTheme.colors.label.strong,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        ProgressCard(progress = detail.progressRate / 100f)

        if (detail.memo.isNullOrBlank().not()) {
            Spacer(modifier = Modifier.height(24.dp))

            SaionTextArea(
                value = detail.memo.orEmpty(),
                onValueChange = {},
                variant = SaionTextAreaVariant.BOX,
                readOnly = true,
            )
        }

        if (detail.needConfirm) {
            Spacer(modifier = Modifier.height(24.dp))
            ConfirmationSection(
                options = state.confirmationOptions,
                onClick = { type -> onIntent(ScheduleDetailIntent.ConfirmationClicked(type)) },
            )
        }

        Spacer(modifier = Modifier.height(if (state.canDelete) 80.dp else 24.dp))
    }
}

@Composable
private fun ProgressCard(progress: Float) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SaionTheme.colors.background.default, RoundedCornerShape(24.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        ScheduleProgressSection(progressRate = (progress.coerceIn(0f, 1f) * 100).toInt())
    }
}

@Composable
private fun ConfirmationSection(
    options: ImmutableList<ScheduleConfirmationUiModel>,
    onClick: (ConfirmationType) -> Unit,
) {
    val confirmedOption = options.firstOrNull { it.type == ConfirmationType.CONFIRMED } ?: return
    ConfirmationChip(
        option = confirmedOption,
        onClick = { onClick(confirmedOption.type) },
    )
}

@Composable
private fun ConfirmationChip(
    option: ScheduleConfirmationUiModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    SaionChip(
        text = "${option.label} ${option.count}",
        shape = ChipShape.SQUARE,
        modifier = modifier,
        isSelected = option.isSelected,
        leadingIcon = {
            Icon(
                imageVector = SaionIcons.CheckFilled,
                contentDescription = null,
                tint = SaionTheme.colors.status.positive.default,
            )
        },
        onClick = onClick,
    )
}

private fun ScheduleDetailSnackbarMessage.resolve(context: Context): String = when (this) {
    is ScheduleDetailSnackbarMessage.Text -> value.ifBlank { context.getString(defaultMessageResId) }
    is ScheduleDetailSnackbarMessage.Error -> error.resolveMessage(context, defaultMessageResId)
}

@Preview(showBackground = true)
@Composable
private fun ScheduleDetailScreenPreview() {
    SaionTheme {
        ScheduleDetailScreen(
            uiState = ScheduleDetailState(
                isLoading = false,
                detail = ScheduleDetail(
                    scheduleId = "schedule-1",
                    title = "동생 생일",
                    startDate = "2026-07-25",
                    endDate = "2026-07-25",
                    startTime = "12:00",
                    endTime = "23:00",
                    isAllDay = false,
                    needConfirm = true,
                    status = ScheduleStatus.UPCOMING,
                    progressRate = 40,
                    dday = 7,
                    memo = "케이크 사들고 인천 가야함",
                    confirmations = listOf(
                        ConfirmationCount(type = ConfirmationType.CONFIRMED, count = 1),
                        ConfirmationCount(type = ConfirmationType.ETC, count = 3),
                    ),
                    myConfirmation = MyConfirmation(confirmationId = 1L, confirmationType = ConfirmationType.CONFIRMED),
                    createdBy = "member-1",
                    createdAt = "2026-07-18T09:00:00",
                ),
                confirmationOptions = persistentListOf(
                    ScheduleConfirmationUiModel(
                        type = ConfirmationType.CONFIRMED,
                        label = "확인했어요",
                        count = 1,
                        isSelected = true,
                    ),
                    ScheduleConfirmationUiModel(
                        type = ConfirmationType.ETC,
                        label = "기타",
                        count = 3,
                        isSelected = false,
                    ),
                ),
                canDelete = true,
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onBack = {},
            onIntent = {},
        )
    }
}
