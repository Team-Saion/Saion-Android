package com.saion.feature.mypage.impl.withdraw.viewmodel

import androidx.compose.runtime.Immutable
import com.saion.core.ui.viewmodel.UIState

@Immutable
internal data class WithdrawState(
    val reason: String = "",
    val showWithdrawDialog: Boolean = false,
    val isSubmitting: Boolean = false,
) : UIState
