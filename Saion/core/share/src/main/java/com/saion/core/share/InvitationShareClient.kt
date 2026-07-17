package com.saion.core.share

import com.saion.core.model.invitation.IssuedInvitation

interface InvitationShareClient {
    suspend fun shareInvitation(
        inviterName: String,
        circleName: String,
        invitation: IssuedInvitation,
    ): InvitationShareResult
}
