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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.saion.core.model.schedule.ScheduleSummary
import com.saion.core.ui.component.ScheduleSummaryCard
import com.saion.core.ui.ext.dashBorder
import com.saion.core.ui.ext.noRippleClickable
import com.saion.ds.icon.SaionIcons
import com.saion.ds.theme.SaionTheme
import com.saion.ds.token.radius.toRoundedCornerShape
import com.saion.feature.home.impl.R

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

        ScheduleSummaryCard(
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
