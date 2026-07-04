package com.saion.app.navigation.shared

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.saion.core.navigation.entry.NavEntryBuilder
import com.saion.core.navigation.key.AppNavKey
import com.saion.core.navigation.state.NavigationState
import com.saion.core.navigation.ui.AppNavigationHost
import com.saion.core.ui.transition.LocalSharedElementAnimatedContentScope
import com.saion.core.ui.transition.LocalSharedElementTransitionScope
import kotlinx.collections.immutable.ImmutableSet

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppRootNavigationHost(
    navigationState: NavigationState<AppNavKey>,
    entryBuilders: ImmutableSet<NavEntryBuilder<AppNavKey>>,
) {
    SharedTransitionLayout {
        CompositionLocalProvider(
            LocalSharedElementTransitionScope provides this,
        ) {
            AppNavigationHost(
                navigationState = navigationState,
                entryBuilders = entryBuilders,
                sharedTransitionScope = this@SharedTransitionLayout,
                transitionSpec = noOpRootTransitionSpec(),
                popTransitionSpec = noOpRootTransitionSpec(),
                extraEntryDecorators = listOf(rootSharedAnimatedScopeDecorator()),
            )
        }
    }
}

private fun noOpRootTransitionSpec(): AnimatedContentTransitionScope<Scene<AppNavKey>>.() -> ContentTransform = {
    EnterTransition.None togetherWith ExitTransition.None using SizeTransform(clip = false)
}

private fun rootSharedAnimatedScopeDecorator(): NavEntryDecorator<AppNavKey> = NavEntryDecorator { entry ->
    CompositionLocalProvider(
        LocalSharedElementAnimatedContentScope provides LocalNavAnimatedContentScope.current as AnimatedContentScope,
    ) {
        entry.Content()
    }
}
