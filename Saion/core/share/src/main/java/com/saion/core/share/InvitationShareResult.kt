package com.saion.core.share

sealed interface InvitationShareResult {
    data object Success : InvitationShareResult

    data class Failure(val message: String? = null) : InvitationShareResult
}
