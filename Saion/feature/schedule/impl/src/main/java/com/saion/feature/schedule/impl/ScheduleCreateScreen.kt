package com.saion.feature.schedule.impl

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.saion.core.ui.component.SaionScaffold
import com.saion.core.ui.component.SystemBarInset
import com.saion.core.ui.error.resolveMessage
import com.saion.core.ui.ext.CollectWithLifecycle
import com.saion.ds.component.button.ButtonSize
import com.saion.ds.component.button.ButtonVariant
import com.saion.ds.component.button.SaionBottomCTA
import com.saion.ds.component.button.SaionButton
import com.saion.ds.component.feedback.SaionSpinner
import com.saion.ds.component.input.InputFieldVariant
import com.saion.ds.component.input.SaionTextArea
import com.saion.ds.component.input.SaionTextAreaVariant
import com.saion.ds.component.input.SaionTextField
import com.saion.ds.component.navigation.SaionTopBar
import com.saion.ds.component.navigation.TopBarVariant
import com.saion.ds.component.selection.SaionSwitch
import com.saion.ds.icon.SaionIcons
import com.saion.ds.theme.SaionTheme
import com.saion.ds.token.radius.toRoundedCornerShape
import com.saion.feature.schedule.impl.viewmodel.ScheduleCreateEffect
import com.saion.feature.schedule.impl.viewmodel.ScheduleCreateIntent
import com.saion.feature.schedule.impl.viewmodel.ScheduleCreateSnackbarMessage
import com.saion.feature.schedule.impl.viewmodel.ScheduleCreateState
import com.saion.feature.schedule.impl.viewmodel.ScheduleCreateViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

@Composable
internal fun ScheduleCreateScreen(
    onBack: () -> Unit,
    onCreated: (String) -> Unit,
    viewModel: ScheduleCreateViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    viewModel.uiEffect.CollectWithLifecycle { effect ->
        when (effect) {
            is ScheduleCreateEffect.NavigateToDetail -> onCreated(effect.scheduleId)
            is ScheduleCreateEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message.resolve(context))
        }
    }

    ScheduleCreateScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onIntent = viewModel::dispatch,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleCreateScreen(
    uiState: ScheduleCreateState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onIntent: (ScheduleCreateIntent) -> Unit,
) {
    var pickerState by remember { mutableStateOf<PickerState?>(null) }

    SaionScaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            SaionTopBar(
                modifier = Modifier.statusBarsPadding(),
                variant = TopBarVariant.Standard(
                    title = stringResource(R.string.schedule_create_title),
                    onBack = onBack,
                    navigationIcon = SaionIcons.Close,
                ),
                containerColor = SaionTheme.colors.background.default,
            )
        },
        bottomBar = {
            SaionBottomCTA(modifier = Modifier.navigationBarsPadding()) {
                SaionButton(
                    text = stringResource(R.string.schedule_create_submit),
                    onClick = { onIntent(ScheduleCreateIntent.SubmitClicked) },
                    size = ButtonSize.XLARGE,
                    enabled = uiState.isSubmitEnabled,
                    modifier = Modifier.fillMaxWidth(),
                    variant = ButtonVariant.PRIMARY,
                )
            }
        },
        systemBarInset = SystemBarInset.None,
        containerColor = SaionTheme.colors.background.default,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp),
            ) {
                SaionTextField(
                    value = uiState.title,
                    onValueChange = { onIntent(ScheduleCreateIntent.TitleChanged(it)) },
                    variant = InputFieldVariant.BOX,
                    placeholder = stringResource(R.string.schedule_create_name_placeholder),
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(20.dp))

                ScheduleDateTimeCard(
                    startText = formatDateTimeRow(uiState.startDate, uiState.startTime),
                    endText = formatDateTimeRow(uiState.endDate, uiState.endTime),
                    onStartClick = { pickerState = PickerState.StartDate },
                    onEndClick = { pickerState = PickerState.EndDate },
                )

                Spacer(modifier = Modifier.height(32.dp))

                ScheduleSwitchCard(
                    checked = uiState.needConfirm,
                    onCheckedChange = { onIntent(ScheduleCreateIntent.NeedConfirmChanged(it)) },
                )

                Spacer(modifier = Modifier.height(32.dp))

                SaionTextArea(
                    value = uiState.memo,
                    onValueChange = { onIntent(ScheduleCreateIntent.MemoChanged(it)) },
                    variant = SaionTextAreaVariant.BOX,
                    placeholder = stringResource(R.string.schedule_create_memo_placeholder),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    maxLines = 6,
                )
            }

            if (uiState.isSubmitting) SaionSpinner()
        }
    }

    when (pickerState) {
        PickerState.StartDate -> ScheduleDatePickerDialog(
            initialDate = uiState.startDate,
            onDismiss = { pickerState = null },
            onConfirm = { date ->
                onIntent(ScheduleCreateIntent.StartDateChanged(date))
                pickerState = PickerState.StartTime
            },
        )

        PickerState.EndDate -> ScheduleDatePickerDialog(
            initialDate = uiState.endDate,
            onDismiss = { pickerState = null },
            onConfirm = { date ->
                onIntent(ScheduleCreateIntent.EndDateChanged(date))
                pickerState = PickerState.EndTime
            },
        )

        PickerState.StartTime -> ScheduleTimePickerDialog(
            initialTime = uiState.startTime,
            onDismiss = { pickerState = null },
            onConfirm = { time ->
                onIntent(ScheduleCreateIntent.StartTimeChanged(time))
                pickerState = null
            },
        )

        PickerState.EndTime -> ScheduleTimePickerDialog(
            initialTime = uiState.endTime,
            onDismiss = { pickerState = null },
            onConfirm = { time ->
                onIntent(ScheduleCreateIntent.EndTimeChanged(time))
                pickerState = null
            },
        )

        null -> Unit
    }
}

@Composable
private fun ScheduleDateTimeCard(
    startText: String,
    endText: String,
    onStartClick: () -> Unit,
    onEndClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = SaionTheme.radius.component.xxLarge.toRoundedCornerShape())
            .background(color = SaionTheme.colors.fill.subtle)
            .border(
                width = 1.dp,
                color = SaionTheme.colors.line.subtle,
                shape = SaionTheme.radius.component.xxLarge.toRoundedCornerShape(),
            )
            .padding(vertical = 14.dp)
            .padding(start = 8.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ScheduleDateTimeRow(
            title = stringResource(R.string.schedule_create_start),
            value = startText,
            isStart = true,
            onClick = onStartClick,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(SaionTheme.colors.line.subtle),
        )
        ScheduleDateTimeRow(
            title = stringResource(R.string.schedule_create_end),
            value = endText,
            isStart = false,
            onClick = onEndClick,
        )
    }
}

@Composable
private fun ScheduleDateTimeRow(
    title: String,
    value: String,
    isStart: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = if (isStart) SaionIcons.Dot else SaionIcons.DotOutline,
            contentDescription = null,
            tint = SaionTheme.colors.label.muted,
        )
        Text(
            text = title,
            style = SaionTheme.typography.body1,
            color = SaionTheme.colors.label.muted,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            style = SaionTheme.typography.body1,
            color = SaionTheme.colors.label.strong,
        )
    }
}

@Composable
private fun ScheduleSwitchCard(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = SaionTheme.radius.component.xxLarge.toRoundedCornerShape())
            .background(color = SaionTheme.colors.fill.subtle)
            .border(
                width = 1.dp,
                color = SaionTheme.colors.line.subtle,
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.schedule_create_need_confirm),
            style = SaionTheme.typography.body1,
            color = SaionTheme.colors.label.strong,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(8.dp))
        SaionSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleDatePickerDialog(
    initialDate: LocalDate,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit,
) {
    val pickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli(),
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        colors = materialDatePickerColors(),
        confirmButton = {
            SaionButton(
                text = stringResource(R.string.schedule_picker_confirm),
                onClick = {
                    val millis = pickerState.selectedDateMillis ?: return@SaionButton
                    val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                    onConfirm(date)
                },
                size = ButtonSize.MEDIUM,
            )
        },
        dismissButton = {
            SaionButton(
                text = stringResource(R.string.schedule_picker_cancel),
                onClick = onDismiss,
                size = ButtonSize.MEDIUM,
                variant = ButtonVariant.NEUTRAL,
            )
        },
    ) {
        DatePicker(
            state = pickerState,
            colors = materialDatePickerColors(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleTimePickerDialog(
    initialTime: LocalTime,
    onDismiss: () -> Unit,
    onConfirm: (LocalTime) -> Unit,
) {
    val state = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = false,
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            SaionButton(
                text = stringResource(R.string.schedule_picker_confirm),
                onClick = { onConfirm(LocalTime.of(state.hour, state.minute)) },
                size = ButtonSize.MEDIUM,
            )
        },
        dismissButton = {
            SaionButton(
                text = stringResource(R.string.schedule_picker_cancel),
                onClick = onDismiss,
                size = ButtonSize.MEDIUM,
                variant = ButtonVariant.NEUTRAL,
            )
        },
        text = {
            TimePicker(
                state = state,
                colors = TimePickerDefaults.colors(
                    timeSelectorSelectedContainerColor = SaionTheme.colors.primary.default,
                    timeSelectorSelectedContentColor = SaionTheme.colors.label.inverse,
                    timeSelectorUnselectedContainerColor = SaionTheme.colors.fill.subtle,
                    timeSelectorUnselectedContentColor = SaionTheme.colors.label.default,
                    selectorColor = SaionTheme.colors.primary.default,
                    clockDialColor = SaionTheme.colors.background.subtle,
                    periodSelectorSelectedContainerColor = SaionTheme.colors.primary.default,
                    periodSelectorSelectedContentColor = SaionTheme.colors.label.inverse,
                    periodSelectorUnselectedContainerColor = SaionTheme.colors.fill.subtle,
                    periodSelectorUnselectedContentColor = SaionTheme.colors.label.default,
                ),
            )
        },
        containerColor = SaionTheme.colors.background.default,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun materialDatePickerColors(): DatePickerColors = DatePickerDefaults.colors(
    containerColor = SaionTheme.colors.background.default,
    titleContentColor = SaionTheme.colors.label.default,
    headlineContentColor = SaionTheme.colors.label.default,
    weekdayContentColor = SaionTheme.colors.label.subtle,
    subheadContentColor = SaionTheme.colors.label.subtle,
    navigationContentColor = SaionTheme.colors.label.default,
    yearContentColor = SaionTheme.colors.label.default,
    disabledYearContentColor = SaionTheme.colors.label.disabled,
    currentYearContentColor = SaionTheme.colors.primary.default,
    selectedYearContentColor = SaionTheme.colors.label.inverse,
    disabledSelectedYearContentColor = SaionTheme.colors.label.inverse,
    selectedYearContainerColor = SaionTheme.colors.primary.default,
    disabledSelectedYearContainerColor = SaionTheme.colors.fill.disabled,
    dayContentColor = SaionTheme.colors.label.default,
    disabledDayContentColor = SaionTheme.colors.label.disabled,
    selectedDayContentColor = SaionTheme.colors.label.inverse,
    disabledSelectedDayContentColor = SaionTheme.colors.label.inverse,
    selectedDayContainerColor = SaionTheme.colors.primary.default,
    disabledSelectedDayContainerColor = SaionTheme.colors.fill.disabled,
    todayContentColor = SaionTheme.colors.primary.default,
    todayDateBorderColor = SaionTheme.colors.primary.default,
)

private fun ScheduleCreateSnackbarMessage.resolve(context: Context): String = when (this) {
    is ScheduleCreateSnackbarMessage.Text -> value.ifBlank { context.getString(defaultMessageResId) }
    is ScheduleCreateSnackbarMessage.Error -> error.resolveMessage(context, defaultMessageResId)
}

private sealed interface PickerState {
    data object StartDate : PickerState
    data object StartTime : PickerState
    data object EndDate : PickerState
    data object EndTime : PickerState
}

private fun previewScheduleCreateState(title: String): ScheduleCreateState = initialScheduleDateTime().let { (start, end) ->
    ScheduleCreateState(
        title = title,
        startDate = start.toLocalDate(),
        endDate = end.toLocalDate(),
        startTime = start.toLocalTime(),
        endTime = end.toLocalTime(),
        needConfirm = true,
        memo = "케이크 사들고 인천 가야함",
    )
}

@Preview(name = "CTA Enabled", showBackground = true)
@Composable
private fun ScheduleCreateScreenEnabledPreview() {
    SaionTheme {
        ScheduleCreateScreen(
            uiState = previewScheduleCreateState(title = "동생 생일"),
            snackbarHostState = remember { SnackbarHostState() },
            onBack = {},
            onIntent = {},
        )
    }
}

@Preview(name = "CTA Disabled", showBackground = true)
@Composable
private fun ScheduleCreateScreenDisabledPreview() {
    SaionTheme {
        ScheduleCreateScreen(
            uiState = previewScheduleCreateState(title = ""),
            snackbarHostState = remember { SnackbarHostState() },
            onBack = {},
            onIntent = {},
        )
    }
}
