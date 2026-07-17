package com.saion.feature.home.impl.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.saion.core.model.schedule.ScheduleSummary
import com.saion.core.ui.component.ScheduleAddCard
import com.saion.core.ui.component.ScheduleSummaryCard
import com.saion.ds.theme.SaionTheme
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
            ScheduleAddCard(onClick = onAddClick)
            return
        }

        ScheduleSummaryCard(
            schedule = mainSchedule,
            onClick = { onScheduleClick(mainSchedule.scheduleId) },
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
