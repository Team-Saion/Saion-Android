package com.saion.app.navigation.startup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.saion.app.navigation.key.SplashNavKey
import com.saion.app.viewmodel.AppUiState
import com.saion.core.navigation.key.AppNavKey
import com.saion.core.navigation.state.NavigationState
import com.saion.feature.auth.api.key.AuthNavKey
import com.saion.feature.auth.api.key.AuthStartStep
import com.saion.feature.main.api.key.MainNavKey
import kotlinx.coroutines.delay

@Composable
fun StartupNavigationCoordinator(
    uiState: AppUiState,
    navigationState: NavigationState<AppNavKey>,
) {
    LaunchedEffect(uiState.isLoading, uiState.startDestination) {
        if (uiState.isLoading) return@LaunchedEffect

        when (val destination = uiState.startDestination) {
            AppStartDestination.SplashThenLogin -> navigateSplashToLogin(navigationState)
            is AppStartDestination.Auth -> navigationState.replaceAll(AuthNavKey(startStep = destination.startStep))
            AppStartDestination.Main -> navigationState.replaceAll(MainNavKey)
            null -> Unit
        }
    }
}

private suspend fun navigateSplashToLogin(navigationState: NavigationState<AppNavKey>) {
    if (navigationState.current == SplashNavKey) {
        navigationState.push(
            AuthNavKey(
                startStep = AuthStartStep.LOGIN,
                showIntroTransition = true,
            ),
        )
        delay(SPLASH_SHARED_ELEMENT_SETTLE_MILLIS)
        val current = navigationState.current
        if (current is AuthNavKey && current.startStep == AuthStartStep.LOGIN) {
            navigationState.replaceAll(current.copy(showIntroTransition = false))
        }
    } else {
        navigationState.replaceAll(
            AuthNavKey(
                startStep = AuthStartStep.LOGIN,
                showIntroTransition = false,
            ),
        )
    }
}

private const val SPLASH_SHARED_ELEMENT_SETTLE_MILLIS = 420L
