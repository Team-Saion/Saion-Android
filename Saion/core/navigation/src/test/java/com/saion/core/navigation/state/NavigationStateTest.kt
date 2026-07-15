package com.saion.core.navigation.state

import androidx.navigation3.runtime.NavBackStack
import com.saion.core.navigation.key.AppNavKey
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NavigationStateTest {
    @Test
    fun `push는 스택 끝에 key를 추가한다`() {
        val state = navigationStateOf(TestNavKey.Home)

        state.push(TestNavKey.HomeDetail)

        assertEquals(
            listOf(TestNavKey.Home, TestNavKey.HomeDetail),
            state.backStackSnapshot(),
        )
        assertEquals(TestNavKey.HomeDetail, state.current)
    }

    @Test
    fun `replace는 마지막 key를 교체한다`() {
        val state = navigationStateOf(TestNavKey.Home)

        state.push(TestNavKey.HomeDetail)
        state.replace(TestNavKey.HomeSettings)

        assertEquals(listOf(TestNavKey.Home, TestNavKey.HomeSettings), state.backStackSnapshot())
        assertEquals(TestNavKey.HomeSettings, state.current)
    }

    @Test
    fun `replaceAll은 스택 전체를 교체한다`() {
        val state = navigationStateOf(TestNavKey.Home)

        state.push(TestNavKey.HomeDetail)
        state.replaceAll(TestNavKey.Schedule, TestNavKey.ScheduleDetail)

        assertEquals(
            listOf(TestNavKey.Schedule, TestNavKey.ScheduleDetail),
            state.backStackSnapshot(),
        )
        assertEquals(TestNavKey.ScheduleDetail, state.current)
    }

    @Test
    fun `pop은 마지막 key를 제거한다`() {
        val state = navigationStateOf(TestNavKey.Home)

        state.push(TestNavKey.HomeDetail)
        assertTrue(state.pop())

        assertEquals(listOf(TestNavKey.Home), state.backStackSnapshot())
        assertEquals(TestNavKey.Home, state.current)
    }

    @Test
    fun `루트 하나만 남으면 pop은 실패한다`() {
        val state = navigationStateOf(TestNavKey.Home)

        assertFalse(state.pop())
        assertEquals(listOf(TestNavKey.Home), state.backStackSnapshot())
    }

    @Test
    fun `popUpTo는 대상 위의 key를 제거한다`() {
        val state = navigationStateOf(TestNavKey.Home)

        state.push(TestNavKey.HomeDetail)
        state.replace(TestNavKey.HomeSettings)
        state.push(TestNavKey.HomeSubDetail)
        assertTrue(state.popUpTo(TestNavKey.HomeSettings))

        assertEquals(
            listOf(TestNavKey.Home, TestNavKey.HomeSettings),
            state.backStackSnapshot(),
        )
        assertFalse(state.popUpTo(TestNavKey.HomeSettings))
    }

    private fun navigationStateOf(vararg keys: TestNavKey): NavigationState<TestNavKey> =
        NavigationState(NavBackStack(*keys))

    private fun NavigationState<TestNavKey>.backStackSnapshot(): List<TestNavKey> =
        backStack.map { it as TestNavKey }

    private enum class TestNavKey : AppNavKey {
        Home,
        HomeDetail,
        HomeSettings,
        HomeSubDetail,
        Schedule,
        ScheduleDetail,
        MyPage,
        MyPageDetail,
    }
}
