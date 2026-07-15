package com.saion.core.navigation.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.saion.core.navigation.key.AppNavKey

@Composable
fun <K : AppNavKey> rememberTabNavigationState(
    vararg tabs: K,
    initialTab: K = tabs.first(),
): TabNavigationState<K> {
    require(tabs.isNotEmpty()) { "rememberTabNavigationState requires at least one tab." }

    val tabList = remember(tabs.contentToString()) { tabs.toList() }
    val backStacks = LinkedHashMap<K, NavBackStack<NavKey>>(tabList.size)

    for (tab in tabList) {
        backStacks[tab] = rememberNavBackStack(tab as NavKey)
    }

    return remember(tabList, backStacks.values.toList()) {
        TabNavigationState(
            tabs = tabList,
            initialTab = initialTab,
            backStacks = backStacks,
        )
    }
}
