package com.saion.feature.home.impl.home.component

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.saion.ds.component.button.IconButtonSize
import com.saion.ds.component.button.SaionIconButton
import com.saion.ds.component.navigation.SaionTopBar
import com.saion.ds.component.navigation.TopBarVariant
import com.saion.ds.icon.SaionIcons
import com.saion.ds.theme.SaionTheme

@Composable
internal fun HomeTopBar(onNotificationClick: () -> Unit) {
    SaionTopBar(
        modifier = Modifier.statusBarsPadding(),
        variant = TopBarVariant.Main,
        titleContentColor = SaionTheme.colors.label.subtle,
        actions = {
            SaionIconButton(
                icon = SaionIcons.Bell,
                tint = SaionTheme.colors.label.subtle,
                size = IconButtonSize.LARGE,
                onClick = onNotificationClick,
            )
        },
    )
}

@Preview
@Composable
private fun HomeTopBarPreview() {
    SaionTheme {
        HomeTopBar {}
    }
}
