package com.saion.core.data.util

import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import com.saion.core.model.result.BusinessErrorType
import com.saion.core.network.model.common.ApiResponse
import java.io.IOException
import java.net.SocketTimeoutException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class SafeRequestTest {
    @Test
    fun `성공 응답 본문에 데이터가 없으면 상태 예외를 던진다`() = runBlocking {
        try {
            safeRequest(
                request = {
                    ApiResponse<String>(
                        statusCode = 200,
                        isSuccess = true,
                        data = null,
                        errorCode = null,
                        message = null,
                        timestamp = "2024-01-01T00:00:00",
                    )
                },
                onSuccess = { AppResult.Success(Unit) },
            )
            fail("Expected IllegalStateException")
        } catch (exception: IllegalStateException) {
            assertEquals("Successful response body is missing data", exception.message)
        }
    }

    @Test
    fun `취소 예외는 다시 던진다`() = runBlocking {
        try {
            safeRequest<Unit, Unit>(
                request = { throw CancellationException("cancelled") },
                onSuccess = { AppResult.Success(Unit) },
            )
            fail("Expected CancellationException")
        } catch (exception: CancellationException) {
            assertEquals("cancelled", exception.message)
        }
    }

    @Test
    fun `소켓 타임아웃 예외는 타임아웃 에러로 매핑된다`() = runBlocking {
        val exception = SocketTimeoutException("timeout")
        val result = safeRequest<Unit, Unit>(
            request = { throw exception },
            onSuccess = { AppResult.Success(Unit) },
        )

        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.Timeout)
        assertEquals(exception, error.cause)
    }

    @Test
    fun `입출력 예외는 네트워크 연결 불가 에러로 매핑된다`() = runBlocking {
        val exception = IOException("offline")
        val result = safeRequest<Unit, Unit>(
            request = { throw exception },
            onSuccess = { AppResult.Success(Unit) },
        )

        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertTrue(error is AppError.NetworkUnavailable)
        assertEquals(exception, error.cause)
    }

    @Test
    fun `401 상태 코드는 비즈니스 코드와 관계없이 인증 만료 에러로 매핑된다`() {
        val response = errorResponse(statusCode = 401, code = "M401_1", message = "Invalid social login token")

        val result = requestFailureError(response)

        assertEquals(AppError.Unauthorized(), result)
        assertNull(result.cause)
    }

    @Test
    fun `401 상태 코드는 G401 코드보다 우선하여 인증 만료 에러로 매핑된다`() {
        val response = errorResponse(statusCode = 401, code = "G401", message = "Authentication is required")

        val result = requestFailureError(response)

        assertEquals(AppError.Unauthorized(), result)
        assertNull(result.cause)
    }

    @Test
    fun `401 상태 코드는 알 수 없는 코드보다 우선하여 인증 만료 에러로 매핑된다`() {
        val response = ApiResponse<Unit>(
            statusCode = 401,
            isSuccess = false,
            data = null,
            errorCode = "X999",
            message = "Expired token",
            timestamp = "2024-01-01T00:00:00",
        )

        val result = requestFailureError(response)

        assertEquals(AppError.Unauthorized(), result)
        assertNull(result.cause)
    }

    @Test
    fun `400 상태의 G400 코드는 잘못된 입력 비즈니스 에러로 매핑된다`() {
        val response = errorResponse(statusCode = 400, code = "G400", message = "Invalid input")

        val result = requestFailureError(response)

        assertEquals(
            AppError.Business(
                businessType = BusinessErrorType.INVALID_INPUT,
                rawCode = "G400",
                message = "Invalid input",
            ),
            result,
        )
        assertNull(result.cause)
    }

    @Test
    fun `404 상태의 G404 코드는 찾을 수 없음 비즈니스 에러로 매핑된다`() {
        val response = errorResponse(statusCode = 404, code = "G404", message = "Resource not found")

        val result = requestFailureError(response)

        assertEquals(
            AppError.Business(
                businessType = BusinessErrorType.NOT_FOUND,
                rawCode = "G404",
                message = "Resource not found",
            ),
            result,
        )
        assertNull(result.cause)
    }

    @Test
    fun `404 상태의 M404_1 코드는 찾을 수 없음 비즈니스 에러로 매핑된다`() {
        val response = errorResponse(statusCode = 404, code = "M404_1", message = "Member not found")

        val result = requestFailureError(response)

        assertEquals(
            AppError.Business(
                businessType = BusinessErrorType.NOT_FOUND,
                rawCode = "M404_1",
                message = "Member not found",
            ),
            result,
        )
        assertNull(result.cause)
    }

    @Test
    fun `409 상태의 M409_1 코드는 중복 비즈니스 에러로 매핑된다`() {
        val response = errorResponse(statusCode = 409, code = "M409_1", message = "Email already exists")

        val result = requestFailureError(response)

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
    fun `409 상태는 특별 취급되지 않으면 서버 에러 코드를 기준으로 매핑된다`() {
        val response = ApiResponse<Unit>(
            statusCode = 409,
            isSuccess = false,
            data = null,
            errorCode = "M409_1",
            message = "Email already exists",
            timestamp = "2024-01-01T00:00:00",
        )

        val result = requestFailureError(response)

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
    fun `410 상태의 M410_1 코드는 찾을 수 없음 비즈니스 에러로 매핑된다`() {
        val response = errorResponse(statusCode = 410, code = "M410_1", message = "Member is already deleted")

        val result = requestFailureError(response)

        assertEquals(
            AppError.Business(
                businessType = BusinessErrorType.NOT_FOUND,
                rawCode = "M410_1",
                message = "Member is already deleted",
            ),
            result,
        )
        assertNull(result.cause)
    }

    @Test
    fun `410 상태의 M410_2 코드는 찾을 수 없음 비즈니스 에러로 매핑된다`() {
        val response = errorResponse(statusCode = 410, code = "M410_2", message = "Member has withdrawn")

        val result = requestFailureError(response)

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

    @Test
    fun `400 상태의 알 수 없는 코드는 알 수 없는 에러로 매핑된다`() {
        val response = errorResponse(statusCode = 400, code = "X999", message = "Unexpected auth failure")

        val result = requestFailureError(response)

        assertEquals(AppError.Unknown(message = "Unexpected auth failure"), result)
        assertNull(result.cause)
    }

    @Test
    fun `403 상태의 알 수 없는 코드는 알 수 없는 에러로 매핑된다`() {
        val response = ApiResponse<Unit>(
            statusCode = 403,
            isSuccess = false,
            data = null,
            errorCode = "X999",
            message = "Forbidden",
            timestamp = "2024-01-01T00:00:00",
        )

        val result = requestFailureError(response)

        assertEquals(AppError.Unknown(message = "Forbidden"), result)
        assertNull(result.cause)
    }

    @Test
    fun `403 상태에서 에러 코드가 없으면 알 수 없는 에러로 매핑된다`() {
        val response = ApiResponse<Unit>(
            statusCode = 403,
            isSuccess = false,
            data = null,
            errorCode = null,
            message = "Forbidden",
            timestamp = "2024-01-01T00:00:00",
        )

        val result = requestFailureError(response)

        assertEquals(AppError.Unknown(message = "Forbidden"), result)
        assertNull(result.cause)
    }

    @Test
    fun `404 상태의 알 수 없는 코드는 알 수 없는 에러로 매핑된다`() {
        val response = errorResponse(statusCode = 404, code = "X404", message = "Unexpected not found")

        val result = requestFailureError(response)

        assertEquals(AppError.Unknown(message = "Unexpected not found"), result)
        assertNull(result.cause)
    }

    @Test
    fun `409 상태의 알 수 없는 코드는 알 수 없는 에러로 매핑된다`() {
        val response = errorResponse(statusCode = 409, code = "X409", message = "Unexpected conflict")

        val result = requestFailureError(response)

        assertEquals(AppError.Unknown(message = "Unexpected conflict"), result)
        assertNull(result.cause)
    }

    @Test
    fun `410 상태의 알 수 없는 코드는 알 수 없는 에러로 매핑된다`() {
        val response = errorResponse(statusCode = 410, code = "X410", message = "Unexpected gone")

        val result = requestFailureError(response)

        assertEquals(AppError.Unknown(message = "Unexpected gone"), result)
        assertNull(result.cause)
    }

    @Test
    fun `422 상태의 알 수 없는 코드는 알 수 없는 에러로 매핑된다`() {
        val response = ApiResponse<Unit>(
            statusCode = 422,
            isSuccess = false,
            data = null,
            errorCode = "X422",
            message = "Unprocessable",
            timestamp = "2024-01-01T00:00:00",
        )

        val result = requestFailureError(response)

        assertEquals(AppError.Unknown(message = "Unprocessable"), result)
        assertNull(result.cause)
    }

    @Test
    fun `400 상태에서 에러 코드가 없으면 알 수 없는 에러로 매핑된다`() {
        val response = errorResponse(code = null, message = "Unexpected failure")

        val result = requestFailureError(response)

        assertEquals(AppError.Unknown(message = "Unexpected failure"), result)
        assertNull(result.cause)
    }

    @Test
    fun `500 상태 코드는 서비스 불가 에러로 매핑된다`() {
        val response = errorResponse(statusCode = 500, code = "G500", message = "Internal server error")

        val result = requestFailureError(response)

        assertEquals(AppError.ServerUnavailable(), result)
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

        val result = requestFailureError(response)

        assertEquals(AppError.ServerUnavailable(), result)
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

        val result = requestFailureError(response)

        assertEquals(AppError.ServerUnavailable(), result)
        assertNull(result.cause)
    }

    private fun requestFailureError(response: ApiResponse<Unit>): AppError = runBlocking {
        val result = safeRequest<Unit, Unit>(
            request = { response },
            onSuccess = { _: Unit ->
                error("onSuccess should not be called")
            },
        )

        assertTrue(result is AppResult.Failure)
        (result as AppResult.Failure).error
    }

    private fun errorResponse(
        statusCode: Int = 400,
        code: String?,
        message: String,
    ): ApiResponse<Unit> = ApiResponse(
        statusCode = statusCode,
        isSuccess = false,
        data = null,
        errorCode = code,
        message = message,
        timestamp = "2024-01-01T00:00:00",
    )
}
