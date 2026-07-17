package com.saion.feature.invitation.impl.viewmodel

import androidx.compose.runtime.Immutable
import com.saion.core.model.invitation.InvitationDetail
import com.saion.core.ui.viewmodel.UIState

@Immutable
internal data class InvitationAcceptState(
    val isLoading: Boolean = true,
    val isAccepting: Boolean = false,
    val detail: InvitationDetail? = null,
    val isLoadFailed: Boolean = false,
) : UIState
