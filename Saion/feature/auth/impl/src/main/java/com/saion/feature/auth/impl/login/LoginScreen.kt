package com.saion.feature.auth.impl.login

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.saion.core.ui.component.SaionScaffold
import com.saion.ds.brand.SaionBrandIntroDefaults
import com.saion.ds.theme.SaionTheme
import com.saion.feature.auth.api.key.AuthStartStep
import com.saion.feature.auth.impl.login.component.LoginBottomContent
import com.saion.feature.auth.impl.login.component.LoginBrandHeader
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    onNavigateNext: (AuthStartStep) -> Unit,
    onNavigateMain: () -> Unit,
    modifier: Modifier = Modifier,
    showIntroTransition: Boolean = false,
    loginViewModel: LoginViewModel = viewModel(),
) {
    val uiState by loginViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(loginViewModel, snackbarHostState) {
        loginViewModel.uiEffect.collect { effect ->
            when (effect) {
                is LoginEffect.NavigateNext -> onNavigateNext(effect.startStep)
                LoginEffect.NavigateMain -> onNavigateMain()
                is LoginEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    LoginScreen(
        uiState = uiState,
        showIntroTransition = showIntroTransition,
        onLoginClick = {
            loginViewModel.login(context)
        },
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@Composable
private fun LoginScreen(
    uiState: LoginUiState,
    showIntroTransition: Boolean,
    onLoginClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val introAlpha = rememberLoginIntroAlpha(showIntroTransition = showIntroTransition)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SaionTheme.colors.background.default),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = loginBackgroundBrush())
                .graphicsLayer(alpha = introAlpha),
        )

        SaionScaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                LoginBottomContent(
                    alpha = introAlpha,
                    onLoginClick = onLoginClick,
                    modifier = Modifier.navigationBarsPadding(),
                )
            },
            contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
            containerColor = Color.Transparent,
        ) {}

        LoginBrandHeader(
            symbolAlpha = introAlpha,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = SaionBrandIntroDefaults.LogoAnchorOffsetY),
        )
    }
}

@Composable
private fun loginBackgroundBrush(base: Color = Color(0xFFFFF9E6)): Brush = Brush.verticalGradient(
    colors = listOf(
        base.copy(alpha = 0.0f),
        base.copy(alpha = 0.025f),
        base.copy(alpha = 0.05f),
        base.copy(alpha = 0.1f),
        base.copy(alpha = 0.3f),
        base.copy(alpha = 0.5f),
        base.copy(alpha = 1f),
    ),
)

@Composable
private fun rememberLoginIntroAlpha(showIntroTransition: Boolean): Float {
    var showContent by remember(showIntroTransition) { mutableStateOf(!showIntroTransition) }

    LaunchedEffect(showIntroTransition) {
        if (!showIntroTransition) {
            showContent = true
            return@LaunchedEffect
        }

        showContent = false
        delay(LOGIN_INTRO_DELAY_MILLIS)
        showContent = true
    }

    return animateFloatAsState(
        targetValue = if (showContent) 1f else 0f,
        animationSpec = tween(durationMillis = LOGIN_INTRO_DURATION_MILLIS),
        label = "login_intro_alpha",
    ).value
}

private const val LOGIN_INTRO_DELAY_MILLIS = 1000L
private const val LOGIN_INTRO_DURATION_MILLIS = 1000

@Preview
@Composable
private fun LoginScreenPreview() {
    SaionTheme {
        LoginScreen(
            uiState = LoginUiState(),
            showIntroTransition = false,
            onLoginClick = {},
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}
