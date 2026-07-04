package com.saion.app.navigation

import androidx.navigation3.runtime.EntryProviderScope
import com.saion.app.navigation.key.SplashNavKey
import com.saion.app.ui.splash.SplashScreen
import com.saion.core.navigation.entry.NavEntryBuilder
import com.saion.core.navigation.key.AppNavKey
import com.saion.core.navigation.navigator.AppNavigator
import javax.inject.Inject

class AppRootEntryBuilder @Inject constructor() : NavEntryBuilder<AppNavKey> {
    override fun build(
        scope: EntryProviderScope<AppNavKey>,
        navigator: AppNavigator<AppNavKey>,
    ) {
        with(scope) {
            entry<SplashNavKey> {
                SplashScreen()
            }
        }
    }
}
