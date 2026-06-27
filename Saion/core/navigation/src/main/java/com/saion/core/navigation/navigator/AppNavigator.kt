package com.saion.core.navigation.navigator

import androidx.compose.runtime.Stable
import com.saion.core.navigation.key.AppNavKey

@Stable
interface AppNavigator<K : AppNavKey> {
    val current: K?
    val canPop: Boolean

    fun push(key: K)

    fun replace(key: K)

    fun replaceAll(vararg keys: K)

    fun pop(): Boolean

    fun popUpTo(
        key: K,
        inclusive: Boolean = false,
    ): Boolean

    fun moveToTopLevel(key: K)
}
