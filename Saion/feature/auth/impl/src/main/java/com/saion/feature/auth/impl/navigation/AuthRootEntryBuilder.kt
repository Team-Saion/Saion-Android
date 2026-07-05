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
import kotlinx.collections.immutable.persistentSetOf

class AuthRootEntryBuilder @Inject constructor() : NavEntryBuilder<AppNavKey> {
    override fun build(
        scope: EntryProviderScope<AppNavKey>,
        navigator: AppNavigator<AppNavKey>,
    ) {
        with(scope) {
            entry<AuthNavKey> { key ->
                AuthRoute(
                    startStep = key.startStep,
                    showIntroTransition = key.showIntroTransition,
                    onAuthComplete = { navigator.replaceAll(MainNavKey) },
                )
            }
        }
    }
}

@Composable
private fun AuthRoute(
    startStep: AuthStartStep,
    showIntroTransition: Boolean,
    onAuthComplete: () -> Unit,
) {
    val startKey = when (startStep) {
        AuthStartStep.LOGIN -> LoginNavKey
        AuthStartStep.TERMS -> TermsNavKey
    }
    val navigationState = rememberNavigationState(startKey)
    val entryBuilders = remember(onAuthComplete, showIntroTransition) {
        persistentSetOf(
            NavEntryBuilder { builderScope, flowNavigator ->
                with(builderScope) {
                    entry<LoginNavKey> {
                        LoginScreen(
                            showIntroTransition = showIntroTransition,
                            onNavigateNext = { startStep ->
                                when (startStep) {
                                    AuthStartStep.LOGIN -> flowNavigator.replace(LoginNavKey)
                                    AuthStartStep.TERMS -> flowNavigator.replace(TermsNavKey)
                                }
                            },
                            onNavigateMain = onAuthComplete,
                        )
                    }
                    entry<TermsNavKey> {
                        TermsScreen(
                            onBack = { flowNavigator.pop() },
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
