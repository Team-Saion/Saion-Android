package com.saion.core.model.result

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BusinessErrorTypeTest {
    @Test
    fun `G400 코드는 잘못된 입력 타입을 반환한다`() {
        assertEquals(BusinessErrorType.INVALID_INPUT, BusinessErrorType.from("G400"))
    }

    @Test
    fun `M409_1 코드는 중복 타입을 반환한다`() {
        assertEquals(BusinessErrorType.DUPLICATE, BusinessErrorType.from("M409_1"))
    }

    @Test
    fun `멤버 조회 실패 관련 코드는 찾을 수 없음 타입을 반환한다`() {
        assertEquals(BusinessErrorType.NOT_FOUND, BusinessErrorType.from("G404"))
        assertEquals(BusinessErrorType.NOT_FOUND, BusinessErrorType.from("M404_1"))
        assertEquals(BusinessErrorType.NOT_FOUND, BusinessErrorType.from("M410_1"))
        assertEquals(BusinessErrorType.NOT_FOUND, BusinessErrorType.from("M410_2"))
    }

    @Test
    fun `알 수 없는 코드는 널을 반환한다`() {
        assertNull(BusinessErrorType.from("X999"))
    }
}
