package com.saion.core.ui.error

import android.content.Context
import androidx.annotation.StringRes
import com.saion.core.model.result.AppError

inline fun <MESSAGE> AppError.toSnackbarMessage(
    @StringRes defaultMessageResId: Int,
    textMessage: (value: String, defaultMessageResId: Int) -> MESSAGE,
    errorMessage: (error: AppError, defaultMessageResId: Int) -> MESSAGE,
): MESSAGE = when (this) {
    is AppError.Business -> textMessage(message.orEmpty(), defaultMessageResId)
    is AppError.Unknown -> textMessage(message.orEmpty(), defaultMessageResId)
    is AppError.NetworkUnavailable,
    is AppError.Timeout,
    is AppError.ServerUnavailable,
    is AppError.Unauthorized,
    -> errorMessage(this, defaultMessageResId)
}

fun AppError.resolveMessage(
    context: Context,
    @StringRes defaultMessageResId: Int,
    fallbackToDefaultForSystemErrors: Boolean = false,
): String = when (this) {
    is AppError.Business -> message ?: context.getString(defaultMessageResId)
    is AppError.Unknown -> message ?: context.getString(defaultMessageResId)
    is AppError.NetworkUnavailable,
    is AppError.Timeout,
    is AppError.ServerUnavailable,
    is AppError.Unauthorized,
    -> if (fallbackToDefaultForSystemErrors) context.getString(defaultMessageResId) else context.getString(this)
}
