package com.saion.core.data.mapper

import com.saion.core.model.result.AppError
import com.saion.core.model.result.BusinessErrorType
import com.saion.core.network.model.common.ApiResponse

fun ApiResponse<*>.toAppError(): AppError = when (statusCode) {
    401 -> AppError.Unauthorized()
    in 400..499 -> errorCode?.toBusinessAppError(fallbackMessage = message) ?: AppError.Unknown(message = message)
    in 500..599 -> AppError.ServerUnavailable()
    else -> AppError.Unknown(message = message)
}

private fun String.toBusinessAppError(fallbackMessage: String?): AppError = when (val type = BusinessErrorType.from(this)) {
    null -> AppError.Unknown(message = fallbackMessage)

    else -> AppError.Business(
        businessType = type,
        rawCode = this,
        message = fallbackMessage,
    )
}
