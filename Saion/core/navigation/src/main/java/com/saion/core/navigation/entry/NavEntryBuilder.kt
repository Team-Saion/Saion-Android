package com.saion.core.navigation.entry

import androidx.navigation3.runtime.EntryProviderScope
import com.saion.core.navigation.key.AppNavKey
import com.saion.core.navigation.navigator.AppNavigator

fun interface NavEntryBuilder<K : AppNavKey> {
    fun build(
        scope: EntryProviderScope<K>,
        navigator: AppNavigator<K>,
    )
}
