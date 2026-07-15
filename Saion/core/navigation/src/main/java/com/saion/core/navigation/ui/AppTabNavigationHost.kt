package com.saion.core.navigation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.saion.core.navigation.entry.NavEntryBuilder
import com.saion.core.navigation.key.AppNavKey
import com.saion.core.navigation.state.TabNavigationState
import kotlinx.collections.immutable.ImmutableSet

@Composable
fun <K : AppNavKey> AppTabNavigationHost(
    navigationState: TabNavigationState<K>,
    entryBuilders: ImmutableSet<NavEntryBuilder<K>>,
    extraEntryDecorators: List<NavEntryDecorator<K>> = emptyList(),
) {
    val provider = remember(entryBuilders, navigationState) {
        entryProvider {
            entryBuilders.forEach { builder ->
                builder.build(
                    scope = this,
                    navigator = navigationState,
                )
            }
        }
    }

    val entriesByTab = LinkedHashMap<K, List<NavEntry<K>>>(navigationState.tabs.size)
    for (tab in navigationState.tabs) {
        val decorators = listOf<NavEntryDecorator<K>>(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ) + extraEntryDecorators

        entriesByTab[tab] = rememberDecoratedNavEntries(
            backStack = navigationState.backStackOf(tab),
            entryDecorators = decorators,
            entryProvider = provider,
        )
    }

    NavDisplay(
        entries = checkNotNull(entriesByTab[navigationState.selectedTab]),
        onBack = { navigationState.pop() },
    )
}
