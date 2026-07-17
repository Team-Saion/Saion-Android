package com.saion.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.saion.core.model.schedule.ScheduleStatus
import com.saion.core.model.schedule.ScheduleSummary
import com.saion.core.model.schedule.ScheduleUrgencyLevel
import com.saion.core.ui.R
import com.saion.core.ui.ext.noRippleClickable
import com.saion.ds.component.button.IconButtonSize
import com.saion.ds.component.button.SaionIconButton
import com.saion.ds.icon.SaionIcons
import com.saion.ds.theme.SaionTheme
import com.saion.ds.token.radius.toRoundedCornerShape
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ScheduleSummaryCard(
    schedule: ScheduleSummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = SaionTheme.radius.container.medium.toRoundedCornerShape()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SaionTheme.colors.background.default)
            .noRippleClickable(onClick = onClick)
            .padding(vertical = 16.dp)
            .padding(start = 16.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = schedule.title,
                style = SaionTheme.typography.title3Strong,
                color = SaionTheme.colors.label.default,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = schedule.formatScheduleDate(),
                style = SaionTheme.typography.caption1,
                color = SaionTheme.colors.label.subtle,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        ScheduleDdayPill(schedule = schedule)

        Spacer(modifier = Modifier.width(8.dp))

        SaionIconButton(
            icon = SaionIcons.ChevronRight,
            contentDescription = stringResource(R.string.schedule_summary_detail),
            onClick = onClick,
            size = IconButtonSize.MEDIUM,
            tint = SaionTheme.colors.primary.subtle,
        )
    }
}

@Composable
private fun ScheduleDdayPill(
    schedule: ScheduleSummary,
    modifier: Modifier = Modifier,
) {
    val (label, backgroundColor, contentColor) = schedule.ddayPresentation()

    Box(
        modifier = modifier
            .clip(SaionTheme.radius.component.full.toRoundedCornerShape())
            .background(backgroundColor)
            .padding(horizontal = 6.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = SaionTheme.typography.label2,
            color = contentColor,
        )
    }
}

@Composable
private fun ScheduleSummary.formatScheduleDate(): String {
    val locale = Locale.KOREAN
    val date = runCatching { LocalDate.parse(startDate) }.getOrNull() ?: return startDate
    return stringResource(
        R.string.schedule_summary_date,
        date.monthValue,
        date.dayOfMonth,
        date.dayOfWeek.getDisplayName(TextStyle.SHORT, locale),
    )
}

@Composable
private fun ScheduleSummary.ddayPresentation(): Triple<String, Color, Color> {
    val ddayValue = dday
    val (backgroundColor, contentColor) = urgencyChipColors()

    return when {
        ddayValue == null -> Triple(
            stringResource(R.string.schedule_summary_dday_default),
            backgroundColor,
            contentColor,
        )

        ddayValue < 0 -> Triple(
            stringResource(R.string.schedule_summary_dday_offset, -ddayValue),
            backgroundColor,
            contentColor,
        )

        ddayValue == 0 -> Triple(
            stringResource(R.string.schedule_summary_dday_today),
            backgroundColor,
            contentColor,
        )

        ddayValue <= 7 -> Triple(
            stringResource(R.string.schedule_summary_dday_offset, ddayValue),
            backgroundColor,
            contentColor,
        )

        else -> Triple(
            stringResource(R.string.schedule_summary_dday_offset, ddayValue),
            backgroundColor,
            contentColor,
        )
    }
}

@Composable
private fun ScheduleSummary.urgencyChipColors(): Pair<Color, Color> = when (urgencyLevel) {
    ScheduleUrgencyLevel.URGENT -> Color(0xFFFFF1F1) to Color(0xFFFF5A5A)
    ScheduleUrgencyLevel.NORMAL -> SaionTheme.colors.fill.subtle to SaionTheme.colors.label.subtle
}

@Preview(showBackground = true)
@Composable
private fun ScheduleSummaryCardPreview() {
    SaionTheme {
        ScheduleSummaryCard(
            schedule = ScheduleSummary(
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
            onClick = {},
        )
    }
}
