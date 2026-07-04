package com.saion.app.ui.splash

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.saion.core.ui.transition.LocalSharedElementAnimatedContentScope
import com.saion.core.ui.transition.LocalSharedElementTransitionScope
import com.saion.core.ui.transition.SPLASH_LOGIN_LOGO_SHARED_KEY
import com.saion.ds.brand.SaionBrandIntroDefaults
import com.saion.ds.icon.SaionIcons

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    val sharedTransitionScope = LocalSharedElementTransitionScope.current
    val animatedContentScope = LocalSharedElementAnimatedContentScope.current
    val logoModifier = Modifier
        .offset(y = SaionBrandIntroDefaults.LogoAnchorOffsetY)
        .size(
            width = SaionBrandIntroDefaults.LogoWidth,
            height = SaionBrandIntroDefaults.LogoHeight,
        )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
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
