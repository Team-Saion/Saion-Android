package com.saion.feature.profileedit.impl

import android.content.Context
import androidx.annotation.StringRes
import com.saion.core.model.result.AppError
import com.saion.core.ui.error.resolveMessage

internal sealed interface ProfileEditSnackbarMessage {
    data class Text(
        val value: String,
        @param:StringRes val defaultMessageResId: Int,
    ) : ProfileEditSnackbarMessage

    data class Error(
        val error: AppError,
        @param:StringRes val defaultMessageResId: Int,
    ) : ProfileEditSnackbarMessage
}

internal fun ProfileEditSnackbarMessage.resolve(context: Context): String = when (this) {
    is ProfileEditSnackbarMessage.Text -> value.ifBlank { context.getString(defaultMessageResId) }
    is ProfileEditSnackbarMessage.Error -> error.resolveMessage(context, defaultMessageResId)
}
