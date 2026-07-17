package com.saion.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.saion.app.invitation.PendingInvitationLinkStore
import com.saion.app.navigation.shared.AppRootNavigationHost
import com.saion.app.navigation.startup.StartupNavigationCoordinator
import com.saion.app.navigation.state.rememberSaionAppState
import com.saion.app.ui.splash.SplashScreen
import com.saion.app.viewmodel.AppViewModel
import com.saion.core.navigation.entry.NavEntryBuilder
import com.saion.core.navigation.key.AppNavKey
import com.saion.core.ui.component.SaionScaffold
import com.saion.core.ui.component.SystemBarInset
import com.saion.core.ui.event.GlobalUiEvent
import com.saion.core.ui.event.GlobalUiEventBus
import com.saion.ds.theme.SaionTheme
import com.saion.feature.auth.api.key.AuthNavKey
import com.saion.feature.invitation.api.key.InvitationAcceptNavKey
import com.saion.feature.main.api.key.MainNavKey
import kotlinx.collections.immutable.ImmutableSet

@Composable
fun SaionApp(
    appViewModel: AppViewModel,
    pendingInvitationLinkStore: PendingInvitationLinkStore,
    rootEntryBuilders: ImmutableSet<NavEntryBuilder<AppNavKey>>,
    modifier: Modifier = Modifier,
) {
    val appState = rememberSaionAppState()
    val uiState by appViewModel.uiState.collectAsState()
    val currentDestination = appState.navigationState.current

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

        LaunchedEffect(Unit) {
            pendingInvitationLinkStore.events.collect {
                if (appState.navigationState.current == MainNavKey) {
                    pendingInvitationLinkStore.consume()?.let { token ->
                        appState.navigationState.replaceAll(MainNavKey, InvitationAcceptNavKey(token))
                    }
                }
            }
        }

        LaunchedEffect(currentDestination) {
            if (currentDestination == MainNavKey) {
                pendingInvitationLinkStore.consume()?.let { token ->
                    appState.navigationState.replaceAll(MainNavKey, InvitationAcceptNavKey(token))
                }
            }
        }

        StartupNavigationCoordinator(
            uiState = uiState,
            navigationState = appState.navigationState,
        )

        SaionScaffold(
            modifier = modifier,
            systemBarInset = SystemBarInset.None,
        ) {
            AppRootNavigationHost(
                navigationState = appState.navigationState,
                entryBuilders = rootEntryBuilders,
            )
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
