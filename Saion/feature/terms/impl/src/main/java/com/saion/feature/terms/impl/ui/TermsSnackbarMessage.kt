package com.saion.feature.terms.impl.ui

import android.content.Context
import androidx.annotation.StringRes
import com.saion.core.model.result.AppError
import com.saion.core.ui.error.resolveMessage

internal sealed interface TermsSnackbarMessage {
    data class Res(@param:StringRes val resId: Int) : TermsSnackbarMessage

    data class Text(
        val value: String,
        @param:StringRes val defaultMessageResId: Int,
    ) : TermsSnackbarMessage

    data class Error(
        val error: AppError,
        @param:StringRes val defaultMessageResId: Int,
    ) : TermsSnackbarMessage
}

internal fun TermsSnackbarMessage.resolve(context: Context): String = when (this) {
    is TermsSnackbarMessage.Res -> context.getString(resId)
    is TermsSnackbarMessage.Text -> value.ifBlank { context.getString(defaultMessageResId) }
    is TermsSnackbarMessage.Error -> error.resolveMessage(context, defaultMessageResId)
}
