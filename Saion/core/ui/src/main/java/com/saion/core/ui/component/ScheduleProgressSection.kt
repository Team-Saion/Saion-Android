package com.saion.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.saion.core.ui.R
import com.saion.ds.theme.SaionTheme
import com.saion.ds.token.radius.toRoundedCornerShape

private val SCHEDULE_PROGRESS_GRADIENT: Brush = Brush.horizontalGradient(
    0f to Color(0xFFFD9A04),
    1f to Color(0xFFFFE2B3),
)

@Composable
fun ScheduleProgressSection(
    progressRate: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ScheduleProgressBar(progressRate = progressRate)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.schedule_progress_relaxed),
                style = SaionTheme.typography.caption1,
                color = SaionTheme.colors.label.strong,
            )
            Text(
                text = stringResource(R.string.schedule_progress_hurry),
                style = SaionTheme.typography.caption1,
                color = SaionTheme.colors.label.muted,
            )
        }
    }
}

@Composable
private fun ScheduleProgressBar(
    progressRate: Int,
    modifier: Modifier = Modifier,
) {
    val normalizedProgress = (progressRate.coerceIn(0, 100)) / 100f
    val filledProgress = if (normalizedProgress == 0f) 0f else normalizedProgress.coerceAtLeast(0.08f)
    val shape = SaionTheme.radius.component.full.toRoundedCornerShape()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(shape)
            .background(SaionTheme.colors.background.muted),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(filledProgress)
                .fillMaxHeight()
                .clip(shape)
                .background(SCHEDULE_PROGRESS_GRADIENT),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ScheduleProgressSectionPreview() {
    SaionTheme {
        ScheduleProgressSection(progressRate = 35)
    }
}
