package com.saion.feature.home.impl.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.saion.ds.component.button.SaionTextButton
import com.saion.ds.component.button.TextButtonSize
import com.saion.ds.component.button.TextButtonVariant
import com.saion.ds.theme.SaionTheme

@Composable
internal fun HomeSectionHeader(
    title: String,
    actionLabel: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = SaionTheme.typography.title1,
            color = SaionTheme.colors.label.strong,
        )
        SaionTextButton(
            text = actionLabel,
            variant = TextButtonVariant.NORMAL,
            size = TextButtonSize.SMALL,
            onClick = onActionClick,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeSectionHeaderPreview() {
    SaionTheme {
        HomeSectionHeader(
            title = "일정",
            actionLabel = "전체 보기",
            onActionClick = {},
        )
    }
}
