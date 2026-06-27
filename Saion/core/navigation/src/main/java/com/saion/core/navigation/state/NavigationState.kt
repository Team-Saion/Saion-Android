package com.saion.core.navigation.state

import androidx.compose.runtime.Stable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.saion.core.navigation.key.AppNavKey
import com.saion.core.navigation.navigator.AppNavigator

@Stable
class NavigationState<K : AppNavKey>(val backStack: NavBackStack<NavKey>) : AppNavigator<K> {
    @Suppress("UNCHECKED_CAST")
    private val typedBackStack: NavBackStack<K>
        get() = backStack as NavBackStack<K>

    override val current: K?
        get() = typedBackStack.lastOrNull()

    override val canPop: Boolean
        get() = typedBackStack.size > 1

    override fun push(key: K) {
        typedBackStack.add(key)
    }

    override fun replace(key: K) {
        if (typedBackStack.isEmpty()) {
            typedBackStack.add(key)
            return
        }
        typedBackStack[typedBackStack.lastIndex] = key
    }

    override fun replaceAll(vararg keys: K) {
        typedBackStack.clear()
        typedBackStack.addAll(keys)
    }

    override fun pop(): Boolean {
        if (!canPop) return false
        typedBackStack.removeLastOrNull() ?: return false
        return true
    }

    override fun popUpTo(
        key: K,
        inclusive: Boolean,
    ): Boolean {
        val targetIndex = typedBackStack.indexOfLast { it == key }
        if (targetIndex == -1) return false

        val removeFrom = if (inclusive) targetIndex else targetIndex + 1
        if (removeFrom >= typedBackStack.size) return false

        repeat(typedBackStack.size - removeFrom) {
            typedBackStack.removeAt(typedBackStack.lastIndex)
        }
        return true
    }

    override fun moveToTopLevel(key: K) {
        if (current == key) return
        replaceAll(key)
    }
}
