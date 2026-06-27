package com.saion.core.navigation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.saion.core.navigation.entry.NavEntryBuilder
import com.saion.core.navigation.key.AppNavKey
import com.saion.core.navigation.state.NavigationState

@Composable
fun <K : AppNavKey> AppNavigationHost(
    navigationState: NavigationState<K>,
    entryBuilders: Set<NavEntryBuilder<K>>,
) {
    if (navigationState.backStack.isEmpty()) return

    val entryDecorators = listOf<NavEntryDecorator<K>>(
        rememberSaveableStateHolderNavEntryDecorator(),
        rememberViewModelStoreNavEntryDecorator(),
    )

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

    NavDisplay(
        backStack = navigationState.backStack as List<K>,
        onBack = { navigationState.pop() },
        entryDecorators = entryDecorators,
        entryProvider = provider,
    )
}
