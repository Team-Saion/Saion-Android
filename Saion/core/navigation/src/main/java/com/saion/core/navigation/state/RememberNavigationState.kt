package com.saion.core.navigation.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.saion.core.navigation.key.AppNavKey

@Composable
fun <K : AppNavKey> rememberNavigationState(vararg initialBackStack: K): NavigationState<K> {
    val navKeys = remember(initialBackStack.contentToString()) {
        initialBackStack.map { it as NavKey }.toTypedArray()
    }
    val backStack = rememberNavBackStack(*navKeys)
    return remember(backStack) {
        NavigationState(backStack = backStack)
    }
}
