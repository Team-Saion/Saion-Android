package com.saion.core.navigation.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay
import com.saion.core.navigation.entry.NavEntryBuilder
import com.saion.core.navigation.key.AppNavKey
import com.saion.core.navigation.state.NavigationState
import kotlinx.collections.immutable.ImmutableSet

@Composable
fun <K : AppNavKey> AppNavigationHost(
    navigationState: NavigationState<K>,
    entryBuilders: ImmutableSet<NavEntryBuilder<K>>,
    sharedTransitionScope: SharedTransitionScope? = null,
    transitionSpec: (AnimatedContentTransitionScope<Scene<K>>.() -> ContentTransform)? = null,
    popTransitionSpec: (AnimatedContentTransitionScope<Scene<K>>.() -> ContentTransform)? = null,
    extraEntryDecorators: List<NavEntryDecorator<K>> = emptyList(),
) {
    if (navigationState.backStack.isEmpty()) return

    val entryDecorators = listOf<NavEntryDecorator<K>>(
        rememberSaveableStateHolderNavEntryDecorator(),
        rememberViewModelStoreNavEntryDecorator(),
    ) + extraEntryDecorators

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

    if (transitionSpec != null && popTransitionSpec != null) {
        NavDisplay(
            backStack = navigationState.backStack as List<K>,
            onBack = { navigationState.pop() },
            entryDecorators = entryDecorators,
            sharedTransitionScope = sharedTransitionScope,
            transitionSpec = transitionSpec,
            popTransitionSpec = popTransitionSpec,
            entryProvider = provider,
        )
    } else {
        NavDisplay(
            backStack = navigationState.backStack as List<K>,
            onBack = { navigationState.pop() },
            entryDecorators = entryDecorators,
            sharedTransitionScope = sharedTransitionScope,
            entryProvider = provider,
        )
    }
}
