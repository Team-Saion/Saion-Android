package com.saion.core.navigation.store

import com.saion.core.navigation.key.AppNavKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object PendingNotificationNavigationStore {
    private var pendingDestination: AppNavKey? = null
    private val eventFlow = MutableSharedFlow<AppNavKey>(
        replay = 0,
        extraBufferCapacity = 1,
    )

    val events: Flow<AppNavKey> = eventFlow.asSharedFlow()

    fun save(destination: AppNavKey) {
        pendingDestination = destination
        eventFlow.tryEmit(destination)
    }

    fun consume(): AppNavKey? = pendingDestination.also {
        pendingDestination = null
    }

    fun peek(): AppNavKey? = pendingDestination
}
