package com.saion.app.navigation.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.saion.core.navigation.key.AppNavKey
import com.saion.core.navigation.state.NavigationState
import com.saion.core.navigation.state.rememberNavigationState

class SaionAppState(val navigationState: NavigationState<AppNavKey>)

@Composable
fun rememberSaionAppState(): SaionAppState {
    val navigationState = rememberNavigationState<AppNavKey>()
    return remember(navigationState) {
        SaionAppState(
            navigationState = navigationState,
        )
    }
}
