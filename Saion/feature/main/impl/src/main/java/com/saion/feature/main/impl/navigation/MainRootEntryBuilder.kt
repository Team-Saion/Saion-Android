package com.saion.feature.main.impl.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import com.saion.core.navigation.entry.NavEntryBuilder
import com.saion.core.navigation.key.AppNavKey
import com.saion.core.navigation.navigator.AppNavigator
import com.saion.core.navigation.state.rememberNavigationState
import com.saion.core.navigation.ui.AppNavigationHost
import com.saion.core.ui.component.SaionScaffold
import com.saion.feature.home.api.key.HomeNavKey
import com.saion.feature.main.api.key.MainNavKey
import com.saion.feature.main.api.key.MainTabNavKey
import com.saion.feature.mypage.api.key.MyPageNavKey
import com.saion.feature.notification.api.key.NotificationNavKey
import com.saion.feature.search.api.key.SearchNavKey
import javax.inject.Inject
import kotlin.jvm.JvmSuppressWildcards

class MainRootEntryBuilder @Inject constructor(
    private val tabEntryBuilders: Set<@JvmSuppressWildcards NavEntryBuilder<MainTabNavKey>>,
) : NavEntryBuilder<AppNavKey> {
    override fun build(
        scope: EntryProviderScope<AppNavKey>,
        navigator: AppNavigator<AppNavKey>,
    ) {
        with(scope) {
            entry<MainNavKey> {
                MainRoute(
                    tabEntryBuilders = tabEntryBuilders,
                )
            }
        }
    }
}

@Composable
private fun MainRoute(tabEntryBuilders: Set<NavEntryBuilder<MainTabNavKey>>) {
    val navigationState = rememberNavigationState<MainTabNavKey>(HomeNavKey)

    val items = remember {
        listOf(
            TabItem(HomeNavKey, "Home"),
            TabItem(SearchNavKey, "Search"),
            TabItem(NotificationNavKey, "Alerts"),
            TabItem(MyPageNavKey, "My"),
        )
    }

    SaionScaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                items.forEach { item ->
                    NavigationBarItem(
                        selected = navigationState.current == item.key,
                        onClick = { navigationState.moveToTopLevel(item.key) },
                        icon = { Text(text = item.label.take(1)) },
                        label = { Text(text = item.label) },
                    )
                }
            }
        },
    ) {
        AppNavigationHost(
            navigationState = navigationState,
            entryBuilders = tabEntryBuilders,
        )
    }
}

private data class TabItem(
    val key: MainTabNavKey,
    val label: String,
)
