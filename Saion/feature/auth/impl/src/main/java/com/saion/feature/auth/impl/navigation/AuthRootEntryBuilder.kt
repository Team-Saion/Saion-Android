package com.saion.feature.auth.impl.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.EntryProviderScope
import com.saion.core.navigation.entry.NavEntryBuilder
import com.saion.core.navigation.key.AppNavKey
import com.saion.core.navigation.navigator.AppNavigator
import com.saion.core.navigation.state.rememberNavigationState
import com.saion.core.navigation.ui.AppNavigationHost
import com.saion.feature.auth.api.key.AuthNavKey
import com.saion.feature.auth.api.key.AuthStartStep
import com.saion.feature.auth.impl.login.LoginScreen
import com.saion.feature.auth.impl.nickname.NicknameScreen
import com.saion.feature.auth.impl.terms.TermsScreen
import com.saion.feature.main.api.key.MainNavKey
import javax.inject.Inject

class AuthRootEntryBuilder @Inject constructor() : NavEntryBuilder<AppNavKey> {
    override fun build(
        scope: EntryProviderScope<AppNavKey>,
        navigator: AppNavigator<AppNavKey>,
    ) {
        with(scope) {
            entry<AuthNavKey> { key ->
                AuthRoute(
                    startStep = key.startStep,
                    onAuthComplete = { navigator.replaceAll(MainNavKey) },
                )
            }
        }
    }
}

@Composable
private fun AuthRoute(
    startStep: AuthStartStep,
    onAuthComplete: () -> Unit,
) {
    val startKey = when (startStep) {
        AuthStartStep.LOGIN -> LoginNavKey
        AuthStartStep.TERMS -> TermsNavKey
    }
    val navigationState = rememberNavigationState(startKey)
    val entryBuilders = remember(onAuthComplete) {
        setOf(
            NavEntryBuilder { builderScope, flowNavigator ->
                with(builderScope) {
                    entry<LoginNavKey> {
                        LoginScreen(
                            onLoginSuccess = { flowNavigator.replace(TermsNavKey) },
                        )
                    }
                    entry<TermsNavKey> {
                        TermsScreen(
                            onContinue = { flowNavigator.replace(NicknameNavKey) },
                        )
                    }
                    entry<NicknameNavKey> {
                        NicknameScreen(
                            onComplete = { onAuthComplete() },
                        )
                    }
                }
            },
        )
    }

    AppNavigationHost(
        navigationState = navigationState,
        entryBuilders = entryBuilders,
    )
}
