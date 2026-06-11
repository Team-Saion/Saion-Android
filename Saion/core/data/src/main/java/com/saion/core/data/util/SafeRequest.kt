package com.saion.core.data.util

import com.saion.core.data.mapper.toAppError
import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import com.saion.core.network.model.common.ApiResponse
import java.io.IOException
import java.net.SocketTimeoutException
import kotlinx.coroutines.CancellationException

internal suspend fun <T, R> safeRequest(
    request: suspend () -> ApiResponse<T>,
    onSuccess: suspend (T) -> AppResult<R>,
): AppResult<R> = try {
    val response = request()
    if (!response.isSuccess) {
        AppResult.Failure(response.toAppError())
    } else {
        val data = response.data ?: throw IllegalStateException("Successful response body is missing data")
        onSuccess(data)
    }
} catch (exception: CancellationException) {
    throw exception
} catch (exception: SocketTimeoutException) {
    AppResult.Failure(AppError.Timeout(cause = exception))
} catch (exception: IOException) {
    AppResult.Failure(AppError.NetworkUnavailable(cause = exception))
}

internal suspend fun <R> safeRequest(
    request: suspend () -> ApiResponse<Unit>,
    onSuccess: suspend () -> AppResult<R>,
): AppResult<R> = try {
    val response = request()
    if (!response.isSuccess) {
        AppResult.Failure(response.toAppError())
    } else {
        onSuccess()
    }
} catch (exception: CancellationException) {
    throw exception
} catch (exception: SocketTimeoutException) {
    AppResult.Failure(AppError.Timeout(cause = exception))
} catch (exception: IOException) {
    AppResult.Failure(AppError.NetworkUnavailable(cause = exception))
}
