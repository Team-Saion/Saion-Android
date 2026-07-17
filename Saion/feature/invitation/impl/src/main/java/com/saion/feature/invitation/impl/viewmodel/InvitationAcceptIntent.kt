package com.saion.feature.invitation.impl.viewmodel

import com.saion.core.ui.viewmodel.UIIntent

internal sealed interface InvitationAcceptIntent : UIIntent {
    data object Load : InvitationAcceptIntent

    data object AcceptClicked : InvitationAcceptIntent

    data object CloseClicked : InvitationAcceptIntent
}
