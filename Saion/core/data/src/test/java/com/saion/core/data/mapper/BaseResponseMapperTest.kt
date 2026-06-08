package com.saion.core.data.mapper

import com.saion.core.model.result.AppError
import com.saion.core.network.model.common.ApiResponse
import org.junit.Assert.assertEquals
import org.junit.Test

class BaseResponseMapperTest {
    @Test
    fun `maps invalid social token`() {
        val response = errorResponse(code = "M401_1", message = "Invalid social login token")

        val result = response.toAppError()

        assertEquals(AppError.Unauthorized, result)
    }

    @Test
    fun `maps known server message codes`() {
        val response = errorResponse(code = "M409_1", message = "Email already exists")

        val result = response.toAppError()

        assertEquals(
            AppError.ServerMessage(
                code = "M409_1",
                message = "Email already exists",
            ),
            result,
        )
    }

    @Test
    fun `maps unknown codes to server message`() {
        val response = errorResponse(code = "X999", message = "Unexpected auth failure")

        val result = response.toAppError()

        assertEquals(
            AppError.ServerMessage(
                code = "X999",
                message = "Unexpected auth failure",
            ),
            result,
        )
    }

    @Test
    fun `maps G401 to unauthorized`() {
        val response = errorResponse(code = "G401", message = "Authentication is required")

        val result = response.toAppError()

        assertEquals(AppError.Unauthorized, result)
    }

    @Test
    fun `maps withdrawn member as server message`() {
        val response = errorResponse(code = "M410_2", message = "Member has withdrawn")

        val result = response.toAppError()

        assertEquals(
            AppError.ServerMessage(
                code = "M410_2",
                message = "Member has withdrawn",
            ),
            result,
        )
    }

    private fun errorResponse(
        statusCode: Int = 400,
        code: String,
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
