package com.saion.core.data.util

import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import com.saion.core.model.result.BusinessErrorType
import com.saion.core.network.model.common.ApiResponse
import java.io.IOException
import java.net.SocketTimeoutException
import kotlinx.coroutines.CancellationException

internal suspend inline fun <reified T, R> safeRequest(
    request: suspend () -> ApiResponse<T>,
    onSuccess: suspend (T) -> AppResult<R>,
): AppResult<R> = try {
    val response = request()
    if (!response.isSuccess) {
        AppResult.Failure(response.toAppError())
    } else {
        val data = response.data ?: when {
            Unit is T -> Unit
            else -> throw IllegalStateException("Successful response body is missing data")
        }
        onSuccess(data)
    }
} catch (exception: CancellationException) {
    throw exception
} catch (exception: SocketTimeoutException) {
    AppResult.Failure(AppError.Timeout(cause = exception))
} catch (exception: IOException) {
    AppResult.Failure(AppError.NetworkUnavailable(cause = exception))
}

private fun ApiResponse<*>.toAppError(): AppError = when (statusCode) {
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
