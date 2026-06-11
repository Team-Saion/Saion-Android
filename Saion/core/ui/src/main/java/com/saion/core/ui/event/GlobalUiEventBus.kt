package com.saion.core.ui.event

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

object GlobalUiEventBus {
    private val eventsChannel = Channel<GlobalUiEvent>(capacity = Channel.BUFFERED)

    val events: Flow<GlobalUiEvent> = eventsChannel.receiveAsFlow()

    fun emit(event: GlobalUiEvent) {
        check(eventsChannel.trySend(event).isSuccess) {
            "Failed to emit GlobalUiEvent: $event"
        }
    }
}
