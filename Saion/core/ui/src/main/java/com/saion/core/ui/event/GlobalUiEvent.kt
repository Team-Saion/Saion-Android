package com.saion.core.ui.event

sealed interface GlobalUiEvent {
    data object SessionExpired : GlobalUiEvent

    data object CircleCreated : GlobalUiEvent
}
