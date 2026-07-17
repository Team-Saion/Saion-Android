package com.saion.feature.home.impl.home.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.saion.ds.theme.SaionTheme
import com.saion.feature.home.impl.R
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
internal fun HomeDateHeader(
    modifier: Modifier = Modifier,
    date: LocalDate = LocalDate.now(),
) {
    Text(
        text = date.toKoreanHeader(),
        modifier = modifier.fillMaxWidth(),
        style = SaionTheme.typography.title1,
        color = SaionTheme.colors.label.strong,
    )
}

@Composable
private fun LocalDate.toKoreanHeader(): String {
    val locale = Locale.KOREAN
    val dayOfWeekLabel = dayOfWeek.getDisplayName(TextStyle.SHORT, locale)
    return stringResource(R.string.home_date_header, monthValue, dayOfMonth, dayOfWeekLabel)
}

@Preview(showBackground = true)
@Composable
private fun HomeDateHeaderPreview() {
    SaionTheme {
        HomeDateHeader(date = LocalDate.of(2026, 6, 28))
    }
}
