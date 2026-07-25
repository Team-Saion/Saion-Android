package com.saion.core.ui.event

sealed interface GlobalUiEvent {
    data object SessionExpired : GlobalUiEvent
    data class ShowInvitation(val token: String) : GlobalUiEvent
}
