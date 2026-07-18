package com.saion.feature.schedule.impl.di

import androidx.navigation3.runtime.EntryProviderScope
import com.saion.core.navigation.entry.NavEntryBuilder
import com.saion.core.navigation.navigator.AppNavigator
import com.saion.feature.main.api.key.MainTabNavKey
import com.saion.feature.schedule.api.key.ScheduleCreateNavKey
import com.saion.feature.schedule.api.key.ScheduleDetailNavKey
import com.saion.feature.schedule.api.key.ScheduleNavKey
import com.saion.feature.schedule.impl.ScheduleCreateScreen
import com.saion.feature.schedule.impl.ScheduleDetailScreen
import com.saion.feature.schedule.impl.ScheduleScreen
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
object ScheduleNavigationModule {
    @Provides
    @IntoSet
    fun provideScheduleEntryBuilder(): NavEntryBuilder<MainTabNavKey> = NavEntryBuilder {
            scope: EntryProviderScope<MainTabNavKey>,
            navigator: AppNavigator<MainTabNavKey>,
        ->
        with(scope) {
            entry<ScheduleNavKey> {
                ScheduleScreen(
                    onAddClick = { navigator.push(ScheduleCreateNavKey) },
                    onScheduleClick = { scheduleId ->
                        navigator.push(ScheduleDetailNavKey(scheduleId = scheduleId))
                    },
                )
            }

            entry<ScheduleCreateNavKey> {
                ScheduleCreateScreen(
                    onBack = { navigator.pop() },
                    onCreated = { scheduleId ->
                        navigator.replace(ScheduleDetailNavKey(scheduleId = scheduleId))
                    },
                )
            }

            entry<ScheduleDetailNavKey> { key ->
                ScheduleDetailScreen(
                    scheduleId = key.scheduleId,
                    onBack = { navigator.pop() },
                    onDeleted = {
                        navigator.pop()
                    },
                )
            }
        }
    }
}
