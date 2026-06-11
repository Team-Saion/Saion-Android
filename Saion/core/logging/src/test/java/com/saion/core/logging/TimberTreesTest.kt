package com.saion.core.logging

import android.util.Log
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TimberTreesTest {
    @Test
    fun `릴리즈 트리는 상세 추적과 디버그 및 정보 로그를 무시한다`() {
        assertFalse(shouldLogInRelease(Log.VERBOSE))
        assertFalse(shouldLogInRelease(Log.DEBUG))
        assertFalse(shouldLogInRelease(Log.INFO))
    }

    @Test
    fun `릴리즈 트리는 경고 이상 로그를 유지한다`() {
        assertTrue(shouldLogInRelease(Log.WARN))
        assertTrue(shouldLogInRelease(Log.ERROR))
        assertTrue(shouldLogInRelease(Log.ASSERT))
    }

    @Test
    fun `빈 메시지는 예외 스택트레이스로 대체된다`() {
        val exception = IllegalStateException("boom")

        val resolved = " ".resolveWith(exception)

        assertTrue(resolved.contains("IllegalStateException"))
        assertTrue(resolved.contains("boom"))
    }

    @Test
    fun `비어 있지 않은 메시지는 그대로 유지된다`() {
        val resolved = "already formatted".resolveWith(IllegalStateException("boom"))

        assertTrue(resolved == "already formatted")
    }
}
