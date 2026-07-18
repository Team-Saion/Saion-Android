package com.saion.feature.home.impl.home.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.saion.ds.component.button.ButtonSize
import com.saion.ds.component.button.ButtonVariant
import com.saion.ds.component.button.SaionButton
import com.saion.ds.theme.SaionTheme
import com.saion.ds.token.radius.toRoundedCornerShape
import com.saion.feature.home.impl.R

@Composable
internal fun HomeFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SaionButton(
        text = stringResource(R.string.home_fab_add_schedule),
        variant = ButtonVariant.PRIMARY,
        size = ButtonSize.LARGE,
        onClick = onClick,
        modifier = modifier.dropShadow(
            shadow = SaionTheme.shadow.component,
            shape = SaionTheme.radius.component.full.toRoundedCornerShape(),
        ),
    )
}

@Preview
@Composable
private fun HomeFabPreview() {
    SaionTheme {
        HomeFab(onClick = {})
    }
}
