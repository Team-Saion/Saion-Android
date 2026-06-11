package com.saion.core.data.util

import com.saion.core.model.result.AppError
import com.saion.core.model.result.AppResult
import com.saion.core.network.model.common.ApiResponse
import java.io.IOException
import java.net.SocketTimeoutException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class SafeRequestTest {
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
    fun `성공 응답 본문에 데이터가 없으면 상태 예외를 던진다`() = runBlocking {
        try {
            safeRequest<Unit, Unit>(
                request = {
                    ApiResponse(
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
}
