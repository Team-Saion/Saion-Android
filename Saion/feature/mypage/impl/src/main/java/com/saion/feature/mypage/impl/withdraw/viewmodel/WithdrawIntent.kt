package com.saion.feature.mypage.impl.withdraw.viewmodel

import com.saion.core.ui.viewmodel.UIIntent

internal sealed interface WithdrawIntent : UIIntent {
    data class ReasonChanged(val value: String) : WithdrawIntent

    data object ClickWithdraw : WithdrawIntent

    data object DismissWithdrawDialog : WithdrawIntent

    data object ConfirmWithdraw : WithdrawIntent
}
