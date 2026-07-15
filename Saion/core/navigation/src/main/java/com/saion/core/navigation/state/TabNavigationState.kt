package com.saion.core.navigation.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.saion.core.navigation.key.AppNavKey
import com.saion.core.navigation.navigator.AppNavigator

@Stable
class TabNavigationState<K : AppNavKey>(
    tabs: List<K>,
    initialTab: K,
    private val backStacks: Map<K, NavBackStack<NavKey>>,
) : AppNavigator<K> {
    val tabs: List<K> = tabs.toList()

    var selectedTab: K by mutableStateOf(initialTab)
        private set

    init {
        require(this.tabs.isNotEmpty()) { "TabNavigationState requires at least one tab." }
        require(backStacks.keys.containsAll(this.tabs)) { "Each tab must have its own back stack." }
        require(this.tabs.contains(initialTab)) { "Initial tab must exist in tabs." }
    }

    @Suppress("UNCHECKED_CAST")
    val currentBackStack: NavBackStack<K>
        get() = checkNotNull(backStacks[selectedTab]) as NavBackStack<K>

    override val current: K?
        get() = currentBackStack.lastOrNull()

    override val canPop: Boolean
        get() = currentBackStack.size > 1

    fun selectTab(tab: K) {
        if (tab == selectedTab) return
        selectedTab = tab
    }

    @Suppress("UNCHECKED_CAST")
    fun backStackOf(tab: K): NavBackStack<K> = checkNotNull(backStacks[tab]) as NavBackStack<K>

    override fun push(key: K) {
        currentBackStack.add(key)
    }

    override fun replace(key: K) {
        if (currentBackStack.isEmpty()) {
            currentBackStack.add(key)
            return
        }
        currentBackStack[currentBackStack.lastIndex] = key
    }

    override fun replaceAll(vararg keys: K) {
        currentBackStack.clear()
        currentBackStack.addAll(keys)
    }

    override fun pop(): Boolean {
        if (!canPop) return false
        currentBackStack.removeLastOrNull() ?: return false
        return true
    }

    override fun popUpTo(
        key: K,
        inclusive: Boolean,
    ): Boolean {
        val targetIndex = currentBackStack.indexOfLast { it == key }
        if (targetIndex == -1) return false

        val removeFrom = if (inclusive) targetIndex else targetIndex + 1
        if (removeFrom >= currentBackStack.size) return false

        repeat(currentBackStack.size - removeFrom) {
            currentBackStack.removeAt(currentBackStack.lastIndex)
        }
        return true
    }
}
