package com.saion.core.network.model.common

import com.saion.core.network.client.NetworkJson
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText

data class ApiResponse<T>(
    val statusCode: Int,
    val isSuccess: Boolean,
    val data: T?,
    val errorCode: String?,
    val message: String?,
    val timestamp: String,
) {
    companion object {
        internal suspend inline fun <reified T> HttpResponse.toApiResponse(): ApiResponse<T> =
            with(NetworkJson.instance.decodeFromString<BaseResponse<T>>(bodyAsText())) {
                ApiResponse(
                    statusCode = status.value,
                    isSuccess = success,
                    data = data,
                    errorCode = errorCode,
                    message = message,
                    timestamp = timestamp,
                )
            }
    }
}
