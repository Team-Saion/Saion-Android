package com.saion.feature.mypage.impl.mypage.viewmodel

import com.saion.core.ui.viewmodel.UIIntent

internal sealed interface MyPageIntent : UIIntent {
    data object RefreshProfile : MyPageIntent

    data object ClickFeedback : MyPageIntent

    data object ClickLogout : MyPageIntent

    data object DismissLogoutDialog : MyPageIntent

    data object ConfirmLogout : MyPageIntent
}
