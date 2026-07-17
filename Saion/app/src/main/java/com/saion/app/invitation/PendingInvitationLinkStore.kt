package com.saion.app.invitation

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@Singleton
class PendingInvitationLinkStore @Inject constructor() {
    private var pendingToken: String? = null
    private val eventFlow = MutableSharedFlow<String>(
        replay = 0,
        extraBufferCapacity = 1,
    )

    val events: Flow<String> = eventFlow.asSharedFlow()

    fun save(token: String) {
        pendingToken = token
        eventFlow.tryEmit(token)
    }

    fun consume(): String? = pendingToken.also {
        pendingToken = null
    }

    fun peek(): String? = pendingToken
}
