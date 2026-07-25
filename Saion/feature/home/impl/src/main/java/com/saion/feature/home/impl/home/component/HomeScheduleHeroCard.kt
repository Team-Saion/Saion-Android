package com.saion.feature.home.impl.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.saion.core.model.schedule.ScheduleStatus
import com.saion.core.model.schedule.ScheduleSummary
import com.saion.core.model.schedule.ScheduleUrgencyLevel
import com.saion.core.ui.component.DdayBadge
import com.saion.core.ui.component.DdayBadgeSize
import com.saion.core.ui.component.ScheduleProgressSection
import com.saion.core.ui.ext.noRippleClickable
import com.saion.ds.component.button.ButtonSize
import com.saion.ds.component.button.ButtonVariant
import com.saion.ds.component.button.SaionButton
import com.saion.ds.theme.SaionTheme
import com.saion.feature.home.impl.R
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale

@Composable
internal fun HomeScheduleHeroCard(
    schedule: ScheduleSummary,
    onScheduleClick: () -> Unit,
    onShareClick: () -> Unit,
    isShareEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    HeroCardSurface(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .noRippleClickable(onClick = onScheduleClick),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = schedule.title,
                            style = SaionTheme.typography.body1,
                            color = SaionTheme.colors.label.default,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = schedule.heroDateTimeText(),
                            style = SaionTheme.typography.caption1,
                            color = SaionTheme.colors.label.muted,
                        )
                    }

                    DdayBadge(
                        dday = schedule.dday,
                        size = DdayBadgeSize.LARGE,
                    )
                }

                ScheduleProgressSection(progressRate = schedule.progressRate)
            }

            SaionButton(
                text = stringResource(R.string.home_hero_schedule_cta),
                onClick = onShareClick,
                modifier = Modifier.fillMaxWidth(),
                variant = ButtonVariant.PRIMARY,
                size = ButtonSize.LARGE,
                enabled = isShareEnabled,
            )
        }
    }
}

@Composable
private fun ScheduleSummary.heroDateTimeText(): String {
    val date = runCatching { LocalDate.parse(startDate) }.getOrNull()
    val locale = Locale.KOREAN
    val startTimeValue = startTime
    val endTimeValue = endTime
    val dateText = if (date == null) {
        startDate
    } else {
        stringResource(
            R.string.home_hero_schedule_date,
            date.monthValue,
            date.dayOfMonth,
            date.dayOfWeek.getDisplayName(TextStyle.SHORT, locale),
        )
    }

    val timeText = when {
        isAllDay -> stringResource(R.string.home_hero_schedule_all_day)
        startTimeValue != null && endTimeValue != null -> "${startTimeValue.toMeridiemText()}~${endTimeValue.toMeridiemText()}"
        startTimeValue != null -> startTimeValue.toMeridiemText()
        endTimeValue != null -> endTimeValue.toMeridiemText()
        else -> null
    }

    return if (timeText.isNullOrBlank()) dateText else "$dateText · $timeText"
}

private fun String.toMeridiemText(): String {
    val parsed = runCatching { LocalTime.parse(this) }.getOrNull() ?: return this
    val meridiem = if (parsed.hour < 12) "오전" else "오후"
    val hour = when (val normalized = parsed.hour % 12) {
        0 -> 12
        else -> normalized
    }
    return "%s %d:%02d".format(Locale.KOREAN, meridiem, hour, parsed.minute)
}

@Preview(showBackground = true)
@Composable
private fun HomeScheduleHeroCardPreview() {
    SaionTheme {
        HomeScheduleHeroCard(
            schedule = ScheduleSummary(
                scheduleId = "schedule-1",
                title = "가족 저녁 식사",
                startDate = "2026-07-20",
                endDate = "2026-07-20",
                startTime = "18:00",
                endTime = "20:00",
                isAllDay = false,
                needConfirm = false,
                status = ScheduleStatus.UPCOMING,
                urgencyLevel = ScheduleUrgencyLevel.URGENT,
                progressRate = 35,
                dday = 3,
            ),
            onScheduleClick = {},
            onShareClick = {},
            isShareEnabled = true,
            modifier = Modifier.padding(24.dp),
        )
    }
}
