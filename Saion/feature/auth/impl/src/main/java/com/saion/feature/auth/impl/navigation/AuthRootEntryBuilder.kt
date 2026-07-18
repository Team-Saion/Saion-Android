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
import com.saion.feature.main.api.key.MainNavKey
import com.saion.feature.profileedit.impl.AuthProfileEditScreen
import com.saion.feature.terms.api.key.TermDetailNavKey
import com.saion.feature.terms.api.key.TermsAgreementNavKey
import com.saion.feature.terms.api.key.TermsMode
import com.saion.feature.terms.impl.TermDetailScreen
import com.saion.feature.terms.impl.TermsScreen
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
        AuthStartStep.TERMS -> TermsAgreementNavKey(mode = TermsMode.AGREEMENT)
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
                                    AuthStartStep.TERMS -> flowNavigator.push(TermsAgreementNavKey(mode = TermsMode.AGREEMENT))
                                }
                            },
                            onNavigateMain = onAuthComplete,
                        )
                    }
                    entry<TermsAgreementNavKey> { key ->
                        TermsScreen(
                            mode = key.mode,
                            onBack = { flowNavigator.replace(LoginNavKey) },
                            onComplete = { flowNavigator.replace(ProfileEditNavKey) },
                            onOpenTerm = { _, url ->
                                flowNavigator.push(TermDetailNavKey(url = url))
                            },
                        )
                    }
                    entry<TermDetailNavKey> { key ->
                        TermDetailScreen(
                            url = key.url,
                            onBack = { flowNavigator.pop() },
                        )
                    }
                    entry<ProfileEditNavKey> {
                        AuthProfileEditScreen(
                            onBack = { flowNavigator.replace(TermsAgreementNavKey(mode = TermsMode.AGREEMENT)) },
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
