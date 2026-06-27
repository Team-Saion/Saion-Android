package com.saion.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.saion.app.navigation.state.rememberSaionAppState
import com.saion.app.ui.splash.SplashScreen
import com.saion.app.viewmodel.AppViewModel
import com.saion.app.viewmodel.RootNavigationTarget
import com.saion.core.navigation.entry.NavEntryBuilder
import com.saion.core.navigation.key.AppNavKey
import com.saion.core.navigation.ui.AppNavigationHost
import com.saion.core.ui.component.SaionScaffold
import com.saion.core.ui.component.SystemBarInset
import com.saion.core.ui.event.GlobalUiEvent
import com.saion.core.ui.event.GlobalUiEventBus
import com.saion.ds.theme.SaionTheme
import com.saion.feature.auth.api.key.AuthNavKey
import com.saion.feature.main.api.key.MainNavKey

@Composable
fun SaionApp(
    appViewModel: AppViewModel,
    rootEntryBuilders: Set<NavEntryBuilder<AppNavKey>>,
    modifier: Modifier = Modifier,
) {
    val appState = rememberSaionAppState()
    val uiState by appViewModel.uiState.collectAsState()

    SaionTheme {
        LaunchedEffect(Unit) {
            GlobalUiEventBus.events.collect { event ->
                when (event) {
                    GlobalUiEvent.SessionExpired -> {
                        appState.navigationState.replaceAll(AuthNavKey())
                    }
                }
            }
        }

        LaunchedEffect(uiState.isLoading, uiState.rootTarget) {
            if (uiState.isLoading) return@LaunchedEffect

            when (val target = uiState.rootTarget) {
                is RootNavigationTarget.Auth -> appState.navigationState.replaceAll(AuthNavKey(startStep = target.startStep))
                RootNavigationTarget.Main -> appState.navigationState.replaceAll(MainNavKey)
                null -> Unit
            }
        }

        SaionScaffold(
            modifier = modifier,
            systemBarInset = SystemBarInset.None,
        ) {
            if (uiState.isLoading) {
                SplashScreen()
            } else {
                AppNavigationHost(
                    navigationState = appState.navigationState,
                    entryBuilders = rootEntryBuilders,
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun SaionAppPreview() {
    SaionTheme {
        SplashScreen()
    }
}
