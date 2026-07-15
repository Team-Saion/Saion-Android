package com.saion.feature.main.impl.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.EntryProviderScope
import com.saion.core.navigation.entry.NavEntryBuilder
import com.saion.core.navigation.key.AppNavKey
import com.saion.core.navigation.navigator.AppNavigator
import com.saion.core.navigation.state.rememberTabNavigationState
import com.saion.core.navigation.ui.AppTabNavigationHost
import com.saion.core.ui.component.SaionScaffold
import com.saion.core.ui.component.SystemBarInset
import com.saion.ds.component.navigation.SaionBottomNavItem
import com.saion.ds.component.navigation.SaionBottomNavigation
import com.saion.ds.icon.SaionIcons
import com.saion.feature.home.api.key.HomeNavKey
import com.saion.feature.main.api.key.MainNavKey
import com.saion.feature.main.api.key.MainTabNavKey
import com.saion.feature.main.impl.R
import com.saion.feature.mypage.api.key.MyPageNavKey
import com.saion.feature.schedule.api.key.ScheduleNavKey
import javax.inject.Inject
import kotlin.jvm.JvmSuppressWildcards
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableSet

class MainRootEntryBuilder @Inject constructor(private val tabEntryBuilders: Set<@JvmSuppressWildcards NavEntryBuilder<MainTabNavKey>>) :
    NavEntryBuilder<AppNavKey> {
    override fun build(
        scope: EntryProviderScope<AppNavKey>,
        navigator: AppNavigator<AppNavKey>,
    ) {
        with(scope) {
            entry<MainNavKey> {
                MainRoute(
                    tabEntryBuilders = tabEntryBuilders.toImmutableSet(),
                )
            }
        }
    }
}

@Composable
private fun MainRoute(tabEntryBuilders: ImmutableSet<NavEntryBuilder<MainTabNavKey>>) {
    val navigationState = rememberTabNavigationState(
        HomeNavKey,
        ScheduleNavKey,
        MyPageNavKey,
    )
    val items = listOf(
        TabItem(HomeNavKey, stringResource(R.string.main_tab_home), SaionIcons.Home),
        TabItem(ScheduleNavKey, stringResource(R.string.main_tab_schedule), SaionIcons.Schedule),
        TabItem(MyPageNavKey, stringResource(R.string.main_tab_mypage), SaionIcons.Person),
    )

    SaionScaffold(
        modifier = Modifier.fillMaxSize(),
        systemBarInset = SystemBarInset.None,
        bottomBar = {
            SaionBottomNavigation {
                items.forEach { item ->
                    SaionBottomNavItem(
                        imageVector = item.imageVector,
                        title = item.title,
                        isSelected = navigationState.selectedTab == item.navKey,
                        onClick = { navigationState.selectTab(item.navKey) },
                    )
                }
            }
        },
    ) {
        AppTabNavigationHost(
            navigationState = navigationState,
            entryBuilders = tabEntryBuilders,
        )
    }
}

@Immutable
private data class TabItem(
    val navKey: MainTabNavKey,
    val title: String,
    val imageVector: ImageVector,
)
