package com.saion.core.model.result

sealed interface AppError {
    val cause: Throwable?

    data class NetworkUnavailable(override val cause: Throwable? = null) : AppError

    data class Timeout(override val cause: Throwable? = null) : AppError

    data class Unauthorized(override val cause: Throwable? = null) : AppError

    data class ServerUnavailable(override val cause: Throwable? = null) : AppError

    data class Business(
        override val cause: Throwable? = null,
        val businessType: BusinessErrorType,
        val rawCode: String,
        val message: String?,
    ) : AppError

    data class Unknown(
        override val cause: Throwable? = null,
        val message: String? = null,
    ) : AppError
}
