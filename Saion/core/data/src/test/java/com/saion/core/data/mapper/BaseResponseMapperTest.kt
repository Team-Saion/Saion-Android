package com.saion.core.data.mapper

import com.saion.core.model.result.AppError
import com.saion.core.model.result.BusinessErrorType
import com.saion.core.network.model.common.ApiResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BaseResponseMapperTest {
    @Test
    fun `유효하지 않은 소셜 토큰 응답은 인증 만료 에러로 매핑된다`() {
        val response = errorResponse(statusCode = 401, code = "M401_1", message = "Invalid social login token")

        val result = response.toAppError()

        assertEquals(AppError.Unauthorized(), result)
        assertNull(result.cause)
    }

    @Test
    fun `중복 이메일 응답은 중복 비즈니스 에러로 매핑된다`() {
        val response = errorResponse(statusCode = 409, code = "M409_1", message = "Email already exists")

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
    fun `알 수 없는 400 코드 응답은 알 수 없는 앱 에러로 매핑된다`() {
        val response = errorResponse(statusCode = 400, code = "X999", message = "Unexpected auth failure")

        val result = response.toAppError()

        assertEquals(AppError.Unknown(message = "Unexpected auth failure"), result)
        assertNull(result.cause)
    }

    @Test
    fun `G401 응답은 상태 코드 기준으로 인증 만료 에러로 매핑된다`() {
        val response = errorResponse(statusCode = 401, code = "G401", message = "Authentication is required")

        val result = response.toAppError()

        assertEquals(AppError.Unauthorized(), result)
        assertNull(result.cause)
    }

    @Test
    fun `탈퇴한 멤버 응답은 찾을 수 없음 비즈니스 에러로 매핑된다`() {
        val response = errorResponse(statusCode = 410, code = "M410_2", message = "Member has withdrawn")

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

    @Test
    fun `G400 응답은 잘못된 입력 비즈니스 에러로 매핑된다`() {
        val response = errorResponse(statusCode = 400, code = "G400", message = "Invalid input")

        val result = response.toAppError()

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
    fun `G404 응답은 찾을 수 없음 비즈니스 에러로 매핑된다`() {
        val response = errorResponse(statusCode = 404, code = "G404", message = "Resource not found")

        val result = response.toAppError()

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
    fun `G500 응답은 서비스 불가 에러로 매핑된다`() {
        val response = errorResponse(statusCode = 500, code = "G500", message = "Internal server error")

        val result = response.toAppError()

        assertEquals(AppError.ServerUnavailable(), result)
        assertNull(result.cause)
    }

    @Test
    fun `멤버를 찾을 수 없는 응답은 찾을 수 없음 비즈니스 에러로 매핑된다`() {
        val response = errorResponse(statusCode = 404, code = "M404_1", message = "Member not found")

        val result = response.toAppError()

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
    fun `이미 삭제된 멤버 응답은 찾을 수 없음 비즈니스 에러로 매핑된다`() {
        val response = errorResponse(statusCode = 410, code = "M410_1", message = "Member is already deleted")

        val result = response.toAppError()

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
    fun `에러 코드가 없는 응답은 알 수 없는 에러로 매핑된다`() {
        val response = errorResponse(code = null, message = "Unexpected failure")

        val result = response.toAppError()

        assertEquals(AppError.Unknown(message = "Unexpected failure"), result)
        assertNull(result.cause)
    }

    @Test
    fun `알 수 없는 404 코드 응답은 알 수 없는 앱 에러로 매핑된다`() {
        val response = errorResponse(statusCode = 404, code = "X404", message = "Unexpected not found")

        val result = response.toAppError()

        assertEquals(AppError.Unknown(message = "Unexpected not found"), result)
        assertNull(result.cause)
    }

    @Test
    fun `알 수 없는 409 코드 응답은 알 수 없는 앱 에러로 매핑된다`() {
        val response = errorResponse(statusCode = 409, code = "X409", message = "Unexpected conflict")

        val result = response.toAppError()

        assertEquals(AppError.Unknown(message = "Unexpected conflict"), result)
        assertNull(result.cause)
    }

    @Test
    fun `알 수 없는 410 코드 응답은 알 수 없는 앱 에러로 매핑된다`() {
        val response = errorResponse(statusCode = 410, code = "X410", message = "Unexpected gone")

        val result = response.toAppError()

        assertEquals(AppError.Unknown(message = "Unexpected gone"), result)
        assertNull(result.cause)
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
