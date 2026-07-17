package com.saion.feature.home.impl.home.viewmodel

import androidx.annotation.StringRes
import com.saion.core.model.result.AppError
import com.saion.core.ui.viewmodel.UIEffect

internal sealed interface HomeEffect : UIEffect {
    data class ShowSnackbar(val message: HomeSnackbarMessage) : HomeEffect
}

internal sealed interface HomeSnackbarMessage {
    data class Text(
        val value: String,
        @param:StringRes val defaultMessageResId: Int,
    ) : HomeSnackbarMessage

    data class Error(
        val error: AppError,
        @param:StringRes val defaultMessageResId: Int,
    ) : HomeSnackbarMessage
}
