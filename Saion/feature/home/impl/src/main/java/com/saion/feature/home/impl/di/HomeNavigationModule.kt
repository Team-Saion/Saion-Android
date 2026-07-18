package com.saion.feature.home.impl.di

import androidx.navigation3.runtime.EntryProviderScope
import com.saion.core.navigation.entry.NavEntryBuilder
import com.saion.core.navigation.navigator.AppNavigator
import com.saion.core.navigation.state.TabNavigationState
import com.saion.feature.circlecreate.api.key.CircleCreateNavKey
import com.saion.feature.home.api.key.HomeMemberListNavKey
import com.saion.feature.home.api.key.HomeNavKey
import com.saion.feature.home.api.key.NotificationHistoryNavKey
import com.saion.feature.home.impl.home.HomeScreen
import com.saion.feature.home.impl.memberlist.HomeMemberListScreen
import com.saion.feature.home.impl.notificationhistory.NotificationHistoryScreen
import com.saion.feature.main.api.key.MainTabNavKey
import com.saion.feature.mypage.api.key.NotificationSettingsNavKey
import com.saion.feature.schedule.api.key.ScheduleNavKey
import com.saion.feature.schedule.api.key.ScheduleCreateNavKey
import com.saion.feature.schedule.api.key.ScheduleDetailNavKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
object HomeNavigationModule {
    @Provides
    @IntoSet
    fun provideHomeEntryBuilder(): NavEntryBuilder<MainTabNavKey> =
        NavEntryBuilder { scope: EntryProviderScope<MainTabNavKey>, navigator: AppNavigator<MainTabNavKey> ->
            with(scope) {
                entry<HomeNavKey> {
                    HomeScreen(
                        onNotificationClick = { navigator.push(NotificationHistoryNavKey) },
                        onCreateCircleClick = { navigator.push(CircleCreateNavKey) },
                        onScheduleAddClick = { navigator.push(ScheduleCreateNavKey) },
                        onScheduleListClick = {
                            (navigator as? TabNavigationState<MainTabNavKey>)?.selectTab(ScheduleNavKey)
                        },
                        onScheduleClick = { scheduleId ->
                            navigator.push(ScheduleDetailNavKey(scheduleId = scheduleId))
                        },
                        onMemberListClick = {
                            navigator.push(HomeMemberListNavKey)
                        },
                    )
                }

                entry<NotificationHistoryNavKey> {
                    NotificationHistoryScreen(
                        onBack = { navigator.pop() },
                        onSettingsClick = { navigator.push(NotificationSettingsNavKey) },
                        onNavigateToHome = {
                            val tabNavigator = navigator as? TabNavigationState<MainTabNavKey>
                            if (tabNavigator != null) {
                                tabNavigator.popUpTo(HomeNavKey)
                                tabNavigator.selectTab(HomeNavKey)
                            }
                        },
                        onNavigateToSchedule = {
                            val tabNavigator = navigator as? TabNavigationState<MainTabNavKey>
                            if (tabNavigator != null) {
                                tabNavigator.popUpTo(HomeNavKey)
                                tabNavigator.selectTab(ScheduleNavKey)
                            }
                        },
                        onNavigateToScheduleDetail = { scheduleId ->
                            navigator.push(ScheduleDetailNavKey(scheduleId = scheduleId))
                        },
                    )
                }

                entry<HomeMemberListNavKey> {
                    HomeMemberListScreen(
                        onBack = { navigator.pop() },
                    )
                }
            }
        }
}
