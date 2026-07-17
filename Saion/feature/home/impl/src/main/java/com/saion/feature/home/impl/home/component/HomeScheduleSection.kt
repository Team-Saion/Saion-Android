package com.saion.feature.home.impl.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
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
import com.saion.core.ui.ext.dashBorder
import com.saion.core.ui.ext.noRippleClickable
import com.saion.ds.component.button.IconButtonSize
import com.saion.ds.component.button.SaionIconButton
import com.saion.ds.icon.SaionIcons
import com.saion.ds.theme.SaionTheme
import com.saion.ds.token.radius.toRoundedCornerShape
import com.saion.feature.home.impl.R
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
internal fun HomeScheduleSection(
    mainSchedule: ScheduleSummary?,
    totalScheduleCount: Long,
    onAddClick: () -> Unit,
    onViewAllClick: () -> Unit,
    onScheduleClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        HomeSectionHeader(
            title = stringResource(R.string.home_schedule_title),
            actionLabel = stringResource(R.string.home_view_all),
            onActionClick = onViewAllClick,
        )

        if (mainSchedule == null) {
            HomeEmptyScheduleCard(onClick = onAddClick)
            return
        }

        HomeFeaturedScheduleCard(
            schedule = mainSchedule,
            onClick = { onScheduleClick(mainSchedule.scheduleId) },
        )
    }
}

@Composable
private fun HomeEmptyScheduleCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = SaionTheme.radius.container.medium.toRoundedCornerShape()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SaionTheme.colors.background.subtle)
            .noRippleClickable(onClick = onClick)
            .dashBorder(
                width = 1.5.dp,
                color = SaionTheme.colors.line.default,
                shape = shape,
                on = 10.dp,
                off = 8.dp,
            )
            .padding(vertical = 16.dp)
            .padding(start = 16.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Icon(
                imageVector = SaionIcons.Plus,
                contentDescription = "",
                tint = SaionTheme.colors.background.subtle,
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        shape = CircleShape,
                        color = SaionTheme.colors.line.strong,
                    )
                    .padding(2.dp),
            )
            Text(
                text = stringResource(R.string.home_schedule_add),
                style = SaionTheme.typography.title2,
                color = SaionTheme.colors.label.subtle,
            )
        }
    }
}

@Composable
private fun HomeFeaturedScheduleCard(
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

        HomeDdayPill(schedule = schedule)

        Spacer(modifier = Modifier.width(8.dp))

        SaionIconButton(
            icon = SaionIcons.ChevronRight,
            contentDescription = stringResource(R.string.home_schedule_detail),
            onClick = {},
            size = IconButtonSize.MEDIUM,
            tint = SaionTheme.colors.primary.subtle,
        )
    }
}

@Composable
private fun HomeDdayPill(
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
        R.string.home_schedule_date,
        date.monthValue,
        date.dayOfMonth,
        date.dayOfWeek.getDisplayName(TextStyle.SHORT, locale),
    )
}

@Composable
private fun ScheduleSummary.ddayPresentation(): Triple<String, Color, Color> {
    val ddayValue = dday

    return when {
        ddayValue == null -> Triple(
            stringResource(R.string.home_schedule_dday_default),
            SaionTheme.colors.fill.subtle,
            SaionTheme.colors.label.subtle,
        )

        ddayValue < 0 -> Triple(
            stringResource(R.string.home_schedule_dday_offset, -ddayValue),
            SaionTheme.colors.fill.subtle,
            SaionTheme.colors.label.subtle,
        )

        ddayValue == 0 -> Triple(
            stringResource(R.string.home_schedule_dday_today),
            Color(0xFFFFF1F1),
            Color(0xFFFF5A5A),
        )

        ddayValue <= 7 -> Triple(
            stringResource(R.string.home_schedule_dday_offset, ddayValue),
            Color(0xFFFFF1F1),
            Color(0xFFFF5A5A),
        )

        else -> Triple(
            stringResource(R.string.home_schedule_dday_offset, ddayValue),
            SaionTheme.colors.fill.subtle,
            SaionTheme.colors.label.subtle,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScheduleSectionPreview() {
    SaionTheme {
        HomeScheduleSection(
            mainSchedule = ScheduleSummary(
                scheduleId = "schedule-1",
                title = "가족 식사",
                startDate = "2026-06-28",
                endDate = "2026-06-28",
                startTime = "18:00",
                endTime = "20:00",
                isAllDay = false,
                needConfirm = false,
                status = ScheduleStatus.UPCOMING,
                progressRate = 0,
                dday = 0,
            ),
            totalScheduleCount = 0,
            onAddClick = {},
            onViewAllClick = {},
            onScheduleClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScheduleSectionEmptyPreview() {
    SaionTheme {
        HomeScheduleSection(
            mainSchedule = null,
            totalScheduleCount = 0,
            onAddClick = {},
            onViewAllClick = {},
            onScheduleClick = {},
        )
    }
}
