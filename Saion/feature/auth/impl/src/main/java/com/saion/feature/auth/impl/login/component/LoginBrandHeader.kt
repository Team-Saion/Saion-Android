package com.saion.feature.auth.impl.login.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import com.saion.core.ui.transition.LocalSharedElementAnimatedContentScope
import com.saion.core.ui.transition.LocalSharedElementTransitionScope
import com.saion.core.ui.transition.SPLASH_LOGIN_LOGO_SHARED_KEY
import com.saion.ds.brand.SaionBrandIntroDefaults
import com.saion.ds.icon.SaionIcons
import com.saion.ds.theme.SaionTheme

@Composable
internal fun LoginBrandHeader(
    symbolAlpha: Float,
    modifier: Modifier = Modifier,
) {
    val sharedTransitionScope = LocalSharedElementTransitionScope.current
    val animatedContentScope = LocalSharedElementAnimatedContentScope.current
    val logoModifier = Modifier.size(
        width = SaionBrandIntroDefaults.LogoWidth,
        height = SaionBrandIntroDefaults.LogoHeight,
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = SaionIcons.Symbol,
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .offset(y = SaionBrandIntroDefaults.SymbolOffsetY)
                .size(SaionBrandIntroDefaults.SymbolSize)
                .graphicsLayer(alpha = symbolAlpha),
        )

        with(sharedTransitionScope) {
            Icon(
                imageVector = SaionIcons.Logo,
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = if (this != null && animatedContentScope != null) {
                    logoModifier.sharedElement(
                        sharedContentState = rememberSharedContentState(SPLASH_LOGIN_LOGO_SHARED_KEY),
                        animatedVisibilityScope = animatedContentScope,
                    )
                } else {
                    logoModifier
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginBrandHeaderHiddenPreview() {
    SaionTheme {
        LoginBrandHeader(symbolAlpha = 0f)
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginBrandHeaderShownPreview() {
    SaionTheme {
        LoginBrandHeader(symbolAlpha = 1f)
    }
}
