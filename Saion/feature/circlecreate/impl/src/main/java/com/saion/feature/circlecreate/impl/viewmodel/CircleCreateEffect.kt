package com.saion.feature.circlecreate.impl.viewmodel

import androidx.annotation.StringRes
import com.saion.core.model.result.AppError
import com.saion.core.ui.viewmodel.UIEffect

internal sealed interface CircleCreateEffect : UIEffect {
    data object Close : CircleCreateEffect

    data class ShowSnackbar(val message: CircleCreateSnackbarMessage) : CircleCreateEffect
}

internal sealed interface CircleCreateSnackbarMessage {
    data class Res(@param:StringRes val resId: Int) : CircleCreateSnackbarMessage

    data class Text(
        val value: String,
        @param:StringRes val defaultMessageResId: Int,
    ) : CircleCreateSnackbarMessage

    data class Error(
        val error: AppError,
        @param:StringRes val defaultMessageResId: Int,
    ) : CircleCreateSnackbarMessage
}
