package com.saion.core.data.mapper

import com.saion.core.model.result.AppError
import com.saion.core.network.model.common.ApiResponse
import org.junit.Assert.assertEquals
import org.junit.Test

class NetworkExceptionMapperTest {
    @Test
    fun `maps 401 status to unauthorized before error code mapping`() {
        val response = ApiResponse<Unit>(
            statusCode = 401,
            isSuccess = false,
            data = null,
            errorCode = "X999",
            message = "Expired token",
            timestamp = "2024-01-01T00:00:00",
        )

        val result = response.toAppError()

        assertEquals(AppError.Unauthorized, result)
    }

    @Test
    fun `maps 503 status to maintenance`() {
        val response = ApiResponse<Unit>(
            statusCode = 503,
            isSuccess = false,
            data = null,
            errorCode = null,
            message = "Maintenance mode",
            timestamp = "2024-01-01T00:00:00",
        )

        val result = response.toAppError()

        assertEquals(AppError.Maintenance, result)
    }

    @Test
    fun `maps server code when status is not special`() {
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
            AppError.ServerMessage(
                code = "M409_1",
                message = "Email already exists",
            ),
            result,
        )
    }

    @Test
    fun `maps unknown server code to server message`() {
        val response = ApiResponse<Unit>(
            statusCode = 500,
            isSuccess = false,
            data = null,
            errorCode = "X999",
            message = "Unexpected failure",
            timestamp = "2024-01-01T00:00:00",
        )

        val result = response.toAppError()

        assertEquals(
            AppError.ServerMessage(
                code = "X999",
                message = "Unexpected failure",
            ),
            result,
        )
    }
}
