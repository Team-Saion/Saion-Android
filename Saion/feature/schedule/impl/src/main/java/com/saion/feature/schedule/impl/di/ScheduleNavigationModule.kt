package com.saion.feature.schedule.impl.di

import androidx.navigation3.runtime.EntryProviderScope
import com.saion.core.navigation.entry.NavEntryBuilder
import com.saion.core.navigation.navigator.AppNavigator
import com.saion.feature.main.api.key.MainTabNavKey
import com.saion.feature.schedule.api.key.ScheduleNavKey
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
            _: AppNavigator<MainTabNavKey>,
        ->
        with(scope) {
            entry<ScheduleNavKey> {
                ScheduleScreen()
            }
        }
    }
}
