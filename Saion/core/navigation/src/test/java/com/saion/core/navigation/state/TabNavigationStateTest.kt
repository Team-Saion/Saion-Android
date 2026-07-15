package com.saion.core.navigation.state

import androidx.navigation3.runtime.NavBackStack
import com.saion.core.navigation.key.AppNavKey
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TabNavigationStateTest {
    @Test
    fun `탭별 backstack는 독립적으로 유지된다`() {
        val state = tabNavigationStateOf(
            tabs = listOf(TestNavKey.Home, TestNavKey.Schedule, TestNavKey.MyPage),
            initialTab = TestNavKey.Home,
        )

        state.push(TestNavKey.HomeDetail)
        state.selectTab(TestNavKey.Schedule)
        state.push(TestNavKey.ScheduleDetail)
        state.selectTab(TestNavKey.MyPage)
        state.push(TestNavKey.MyPageDetail)

        state.selectTab(TestNavKey.Home)
        assertEquals(
            listOf(TestNavKey.Home, TestNavKey.HomeDetail),
            state.backStackSnapshot(TestNavKey.Home),
        )

        state.selectTab(TestNavKey.Schedule)
        assertEquals(
            listOf(TestNavKey.Schedule, TestNavKey.ScheduleDetail),
            state.backStackSnapshot(TestNavKey.Schedule),
        )

        state.selectTab(TestNavKey.MyPage)
        assertEquals(
            listOf(TestNavKey.MyPage, TestNavKey.MyPageDetail),
            state.backStackSnapshot(TestNavKey.MyPage),
        )
    }

    @Test
    fun `탭 전환은 현재 선택 탭만 바꾸고 기존 스택은 유지한다`() {
        val state = tabNavigationStateOf(
            tabs = listOf(TestNavKey.Home, TestNavKey.Schedule),
            initialTab = TestNavKey.Home,
        )

        state.push(TestNavKey.HomeDetail)
        state.selectTab(TestNavKey.Schedule)

        assertEquals(TestNavKey.Schedule, state.selectedTab)
        assertEquals(listOf(TestNavKey.Schedule), state.backStackSnapshot(TestNavKey.Schedule))
        assertEquals(
            listOf(TestNavKey.Home, TestNavKey.HomeDetail),
            state.backStackSnapshot(TestNavKey.Home),
        )
    }

    @Test
    fun `현재 탭 navigator 조작은 선택된 탭의 backstack에만 적용된다`() {
        val state = tabNavigationStateOf(
            tabs = listOf(TestNavKey.Home, TestNavKey.Schedule),
            initialTab = TestNavKey.Home,
        )

        state.push(TestNavKey.HomeDetail)
        state.selectTab(TestNavKey.Schedule)
        state.replaceAll(TestNavKey.Schedule, TestNavKey.ScheduleDetail)

        assertEquals(
            listOf(TestNavKey.Home, TestNavKey.HomeDetail),
            state.backStackSnapshot(TestNavKey.Home),
        )
        assertEquals(
            listOf(TestNavKey.Schedule, TestNavKey.ScheduleDetail),
            state.backStackSnapshot(TestNavKey.Schedule),
        )
    }

    @Test
    fun `현재 탭에서만 pop이 동작한다`() {
        val state = tabNavigationStateOf(
            tabs = listOf(TestNavKey.Home, TestNavKey.Schedule),
            initialTab = TestNavKey.Home,
        )

        state.push(TestNavKey.HomeDetail)
        state.selectTab(TestNavKey.Schedule)

        assertFalse(state.pop())
        state.push(TestNavKey.ScheduleDetail)
        assertTrue(state.pop())

        assertEquals(listOf(TestNavKey.Schedule), state.backStackSnapshot(TestNavKey.Schedule))
        assertEquals(
            listOf(TestNavKey.Home, TestNavKey.HomeDetail),
            state.backStackSnapshot(TestNavKey.Home),
        )
    }

    private fun tabNavigationStateOf(
        tabs: List<TestNavKey>,
        initialTab: TestNavKey,
    ): TabNavigationState<TestNavKey> = TabNavigationState(
        tabs = tabs,
        initialTab = initialTab,
        backStacks = tabs.associateWith { NavBackStack(it) },
    )

    private fun TabNavigationState<TestNavKey>.backStackSnapshot(tab: TestNavKey): List<TestNavKey> =
        backStackOf(tab).toList()

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
