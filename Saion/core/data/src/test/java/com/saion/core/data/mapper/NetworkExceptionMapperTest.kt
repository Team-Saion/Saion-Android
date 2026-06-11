package com.saion.core.data.mapper

import com.saion.core.model.result.AppError
import com.saion.core.model.result.BusinessErrorType
import com.saion.core.network.model.common.ApiResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NetworkExceptionMapperTest {
    @Test
    fun `401 상태 코드는 에러 코드 매핑보다 먼저 인증 만료 에러로 매핑된다`() {
        val response = ApiResponse<Unit>(
            statusCode = 401,
            isSuccess = false,
            data = null,
            errorCode = "X999",
            message = "Expired token",
            timestamp = "2024-01-01T00:00:00",
        )

        val result = response.toAppError()

        assertEquals(AppError.Unauthorized(), result)
        assertNull(result.cause)
    }

    @Test
    fun `403 상태 코드는 알 수 없는 에러로 매핑된다`() {
        val response = ApiResponse<Unit>(
            statusCode = 403,
            isSuccess = false,
            data = null,
            errorCode = "X999",
            message = "Forbidden",
            timestamp = "2024-01-01T00:00:00",
        )

        val result = response.toAppError()

        assertEquals(AppError.Unknown(message = "Forbidden"), result)
        assertNull(result.cause)
    }

    @Test
    fun `에러 코드가 없는 403 상태 코드는 알 수 없는 에러로 매핑된다`() {
        val response = ApiResponse<Unit>(
            statusCode = 403,
            isSuccess = false,
            data = null,
            errorCode = null,
            message = "Forbidden",
            timestamp = "2024-01-01T00:00:00",
        )

        val result = response.toAppError()

        assertEquals(AppError.Unknown(message = "Forbidden"), result)
        assertNull(result.cause)
    }

    @Test
    fun `503 상태 코드는 서비스 불가 에러로 매핑된다`() {
        val response = ApiResponse<Unit>(
            statusCode = 503,
            isSuccess = false,
            data = null,
            errorCode = null,
            message = "Maintenance mode",
            timestamp = "2024-01-01T00:00:00",
        )

        val result = response.toAppError()

        assertEquals(AppError.ServerUnavailable(), result)
        assertNull(result.cause)
    }

    @Test
    fun `특별 취급되지 않는 상태 코드는 서버 에러 코드를 기준으로 매핑된다`() {
        val response = ApiResponse<Unit>(
            statusCode = 409,
            isSuccess = false,
            data = null,
            errorCode = "M409_1",
            message = "Email already exists",
            timestamp = "2024-01-01T00:00:00",
        )

        val result = response.toAppError()

        assertEquals(
            AppError.Business(
                businessType = BusinessErrorType.DUPLICATE,
                rawCode = "M409_1",
                message = "Email already exists",
            ),
            result,
        )
        assertNull(result.cause)
    }

    @Test
    fun `500 상태의 알 수 없는 서버 코드는 서비스 불가 에러로 매핑된다`() {
        val response = ApiResponse<Unit>(
            statusCode = 500,
            isSuccess = false,
            data = null,
            errorCode = "X999",
            message = "Unexpected failure",
            timestamp = "2024-01-01T00:00:00",
        )

        val result = response.toAppError()

        assertEquals(AppError.ServerUnavailable(), result)
        assertNull(result.cause)
    }

    @Test
    fun `4xx 상태의 알 수 없는 코드는 알 수 없는 에러로 매핑된다`() {
        val response = ApiResponse<Unit>(
            statusCode = 422,
            isSuccess = false,
            data = null,
            errorCode = "X422",
            message = "Unprocessable",
            timestamp = "2024-01-01T00:00:00",
        )

        val result = response.toAppError()

        assertEquals(AppError.Unknown(message = "Unprocessable"), result)
        assertNull(result.cause)
    }

    @Test
    fun `410 상태의 알려진 찾을 수 없음 코드는 비즈니스 에러로 매핑된다`() {
        val response = ApiResponse<Unit>(
            statusCode = 410,
            isSuccess = false,
            data = null,
            errorCode = "M410_2",
            message = "Member has withdrawn",
            timestamp = "2024-01-01T00:00:00",
        )

        val result = response.toAppError()

        assertEquals(
            AppError.Business(
                businessType = BusinessErrorType.NOT_FOUND,
                rawCode = "M410_2",
                message = "Member has withdrawn",
            ),
            result,
        )
        assertNull(result.cause)
    }
}
