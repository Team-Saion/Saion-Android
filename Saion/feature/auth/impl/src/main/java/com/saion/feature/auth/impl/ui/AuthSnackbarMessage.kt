package com.saion.feature.auth.impl.ui

import android.content.Context
import androidx.annotation.StringRes
import com.saion.core.model.result.AppError
import com.saion.core.ui.error.getString

internal sealed interface AuthSnackbarMessage {
    data class Res(@param:StringRes val resId: Int) : AuthSnackbarMessage

    data class Text(
        val value: String,
        @param:StringRes val defaultMessageResId: Int,
    ) : AuthSnackbarMessage

    data class Error(
        val error: AppError,
        @param:StringRes val defaultMessageResId: Int,
    ) : AuthSnackbarMessage
}

internal fun AuthSnackbarMessage.resolve(context: Context): String = when (this) {
    is AuthSnackbarMessage.Res -> context.getString(resId)
    is AuthSnackbarMessage.Text -> value.ifBlank { context.getString(defaultMessageResId) }
    is AuthSnackbarMessage.Error -> error.resolveMessage(context, defaultMessageResId)
}

private fun AppError.resolveMessage(
    context: Context,
    @StringRes defaultMessageResId: Int,
): String = when (this) {
    is AppError.Business -> message ?: context.getString(defaultMessageResId)
    is AppError.Unknown -> message ?: context.getString(defaultMessageResId)
    is AppError.NetworkUnavailable,
    is AppError.Timeout,
    is AppError.ServerUnavailable,
    is AppError.Unauthorized,
    -> context.getString(this)
}
