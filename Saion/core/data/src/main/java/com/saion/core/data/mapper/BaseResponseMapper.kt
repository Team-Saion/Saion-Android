package com.saion.core.data.mapper

import com.saion.core.model.result.AppError
import com.saion.core.network.model.common.ApiResponse

fun ApiResponse<*>.toAppError(): AppError {
    if (statusCode == 401) return AppError.Unauthorized
    if (statusCode == 503) return AppError.Maintenance

    return errorCode.toAppError(fallbackMessage = message)
}

private fun String?.toAppError(fallbackMessage: String?): AppError = when (this) {
    "G400" -> AppError.InvalidInput

    "G401",
    "M401_1",
    -> AppError.Unauthorized

    else -> AppError.ServerMessage(code = this, message = fallbackMessage)
}
