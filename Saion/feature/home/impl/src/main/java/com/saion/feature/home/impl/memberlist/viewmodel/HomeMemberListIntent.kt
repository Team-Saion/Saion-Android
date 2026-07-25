package com.saion.feature.home.impl.memberlist.viewmodel

import com.saion.core.ui.viewmodel.UIIntent

internal sealed interface HomeMemberListIntent : UIIntent {
    data object ClickLeave : HomeMemberListIntent

    data object DismissLeaveDialog : HomeMemberListIntent

    data object ConfirmLeave : HomeMemberListIntent
}
