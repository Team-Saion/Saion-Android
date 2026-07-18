package com.saion.feature.mypage.impl.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.saion.core.ui.ext.noRippleClickable
import com.saion.ds.component.button.IconButtonSize
import com.saion.ds.component.button.SaionIconButton
import com.saion.ds.component.button.SaionTextButton
import com.saion.ds.component.button.TextButtonSize
import com.saion.ds.component.button.TextButtonVariant
import com.saion.ds.icon.SaionIcons
import com.saion.ds.theme.SaionTheme
import com.saion.ds.token.radius.toRoundedCornerShape
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun MyPageMenuCard(
    items: ImmutableList<MyPageMenuItem>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = SaionTheme.colors.background.default,
                shape = SaionTheme.radius.container.large.toRoundedCornerShape(),
            )
            .padding(vertical = 12.dp)
            .padding(start = 8.dp, end = 4.dp),
    ) {
        items.forEach { item ->
            MyPageMenuRow(
                title = item.title,
                onClick = item.onClick,
                trailingContent = item.trailingContent,
            )
        }
    }
}

@Composable
private fun MyPageMenuRow(
    title: String,
    onClick: () -> Unit,
    trailingContent: MyPageMenuTrailingContent,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .noRippleClickable(onClick = onClick)
            .padding(vertical = 4.dp)
            .padding(start = 16.dp, end = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = SaionTheme.typography.title3,
            color = SaionTheme.colors.label.default,
            modifier = Modifier.weight(1f),
        )
        when (trailingContent) {
            MyPageMenuTrailingContent.Chevron -> {
                SaionIconButton(
                    icon = SaionIcons.ChevronRight,
                    contentDescription = null,
                    size = IconButtonSize.MEDIUM,
                    tint = SaionTheme.colors.primary.subtle,
                    onClick = onClick,
                )
            }

            is MyPageMenuTrailingContent.TextButton -> {
                SaionTextButton(
                    text = trailingContent.text,
                    onClick = onClick,
                    variant = TextButtonVariant.NORMAL,
                    size = TextButtonSize.SMALL,
                    enabled = trailingContent.enabled,
                )
            }
        }
    }
}
