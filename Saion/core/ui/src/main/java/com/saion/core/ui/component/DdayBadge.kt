package com.saion.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.saion.core.ui.R
import com.saion.ds.theme.SaionTheme
import com.saion.ds.token.radius.toRoundedCornerShape

enum class DdayBadgeSize {
    LARGE,
    MEDIUM,
}

@Composable
fun DdayBadge(
    dday: Int?,
    size: DdayBadgeSize,
    modifier: Modifier = Modifier,
) {
    val style = ddayBadgeStyle(dday = dday, size = size)

    Box(
        modifier = modifier
            .background(
                color = style.backgroundColor,
                shape = SaionTheme.radius.component.full.toRoundedCornerShape(),
            )
            .padding(style.padding),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = style.label,
            style = style.textStyle(),
            color = style.contentColor,
        )
    }
}

private data class DdayBadgeStyle(
    val label: String,
    val backgroundColor: Color,
    val contentColor: Color,
    val padding: PaddingValues,
    val textStyle: @Composable () -> androidx.compose.ui.text.TextStyle,
)

@Composable
private fun ddayBadgeStyle(
    dday: Int?,
    size: DdayBadgeSize,
): DdayBadgeStyle {
    val (backgroundColor, contentColor) = ddayBadgeColors(dday)
    return DdayBadgeStyle(
        label = ddayBadgeLabel(dday),
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        padding = when (size) {
            DdayBadgeSize.LARGE -> PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            DdayBadgeSize.MEDIUM -> PaddingValues(horizontal = 6.dp, vertical = 3.dp)
        },
        textStyle = {
            when (size) {
                DdayBadgeSize.LARGE -> SaionTheme.typography.label1
                DdayBadgeSize.MEDIUM -> SaionTheme.typography.label2
            }
        },
    )
}

@Composable
private fun ddayBadgeLabel(dday: Int?): String = when {
    dday == null -> stringResource(R.string.dday_badge_default)
    dday == 0 -> stringResource(R.string.dday_badge_today)
    dday < 0 -> stringResource(R.string.dday_badge_offset, -dday)
    else -> stringResource(R.string.dday_badge_offset, dday)
}

@Composable
private fun ddayBadgeColors(dday: Int?): Pair<Color, Color> = if (dday != null && dday <= 7) {
    SaionTheme.colors.status.negative.subtle to SaionTheme.colors.status.negative.default
} else {
    SaionTheme.colors.fill.subtle to SaionTheme.colors.label.subtle
}

@Preview(showBackground = true)
@Composable
private fun DdayBadgeLargePreview() {
    SaionTheme {
        DdayBadge(
            dday = 3,
            size = DdayBadgeSize.LARGE,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DdayBadgeMediumPreview() {
    SaionTheme {
        DdayBadge(
            dday = -2,
            size = DdayBadgeSize.MEDIUM,
        )
    }
}
