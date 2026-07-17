package com.saion.core.ui.event

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object CircleCreatedEventBus {
    private val eventsFlow = MutableSharedFlow<Unit>(
        replay = 0,
        extraBufferCapacity = 1,
    )

    val events: Flow<Unit> = eventsFlow.asSharedFlow()

    fun emit() {
        check(eventsFlow.tryEmit(Unit)) {
            "Failed to emit CircleCreated event"
        }
    }
}
