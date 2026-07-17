package com.saion.feature.home.impl.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.saion.ds.component.button.ButtonSize
import com.saion.ds.component.button.ButtonVariant
import com.saion.ds.component.button.SaionButton
import com.saion.ds.theme.SaionTheme
import com.saion.feature.home.impl.R
import com.saion.feature.home.impl.viewmodel.HomeState

@Composable
internal fun HomeHeroCard(
    state: HomeState,
    onInviteClick: () -> Unit,
    onCreateCircleClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val content = when (state) {
        HomeState.Loading -> HeroCardContent(
            title = stringResource(R.string.home_hero_loading_title),
            buttonText = null,
            onClick = {},
        )

        HomeState.None -> HeroCardContent(
            title = stringResource(R.string.home_hero_empty_title),
            buttonText = stringResource(R.string.home_hero_create_circle),
            onClick = onCreateCircleClick,
        )

        is HomeState.Content -> HeroCardContent(
            title = stringResource(R.string.home_hero_invite_title),
            buttonText = if (state.canInvite) stringResource(R.string.home_hero_send_invitation) else null,
            onClick = onInviteClick,
            isEnabled = state.isInviting.not(),
        )
    }
    val shape = RoundedCornerShape(SaionTheme.radius.container.xLarge)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = shape)
            .dropShadow(shadow = SaionTheme.shadow.container, shape = shape)
            .background(SaionTheme.colors.background.default)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_letter),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .aspectRatio(120 / 95f),
                contentScale = ContentScale.Fit,
            )
        }

        Text(
            text = content.title,
            style = SaionTheme.typography.title1,
            color = SaionTheme.colors.label.default,
        )

        content.buttonText?.let { buttonText ->
            SaionButton(
                text = buttonText,
                onClick = content.onClick,
                modifier = Modifier.fillMaxWidth(),
                variant = ButtonVariant.PRIMARY,
                size = ButtonSize.LARGE,
                enabled = content.isEnabled,
            )
        }
    }
}

private data class HeroCardContent(
    val title: String,
    val buttonText: String?,
    val onClick: () -> Unit,
    val isEnabled: Boolean = true,
)

@Preview(showBackground = true)
@Composable
private fun HomeHeroCardNonePreview() {
    SaionTheme {
        HomeHeroCard(
            state = HomeState.None,
            onInviteClick = {},
            onCreateCircleClick = {},
            modifier = Modifier.padding(24.dp),
        )
    }
}
