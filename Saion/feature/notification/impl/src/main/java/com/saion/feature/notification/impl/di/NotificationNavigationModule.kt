package com.saion.feature.notification.impl.di

import androidx.navigation3.runtime.EntryProviderScope
import com.saion.core.navigation.entry.NavEntryBuilder
import com.saion.core.navigation.navigator.AppNavigator
import com.saion.feature.main.api.key.MainTabNavKey
import com.saion.feature.notification.api.key.NotificationNavKey
import com.saion.feature.notification.impl.ui.NotificationScreen
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
object NotificationNavigationModule {
    @Provides
    @IntoSet
    fun provideNotificationEntryBuilder(): NavEntryBuilder<MainTabNavKey> = NavEntryBuilder {
            scope: EntryProviderScope<MainTabNavKey>,
            _: AppNavigator<MainTabNavKey>,
        ->
        with(scope) {
            entry<NotificationNavKey> {
                NotificationScreen()
            }
        }
    }
}
