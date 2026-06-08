package com.saion.core.model.result

sealed interface AppError {
    // 인증 필요
    data object Unauthorized : AppError

    // 잘못된 입력
    data object InvalidInput : AppError

    data object Network : AppError

    // status code가 503인 경우
    data object Maintenance : AppError

    // 메시지 처리만 하면 되는 에러
    data class ServerMessage(
        val code: String?,
        val message: String?,
    ) : AppError
}
