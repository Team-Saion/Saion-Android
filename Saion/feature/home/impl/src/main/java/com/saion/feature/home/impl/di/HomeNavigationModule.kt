package com.saion.feature.home.impl.di

import androidx.navigation3.runtime.EntryProviderScope
import com.saion.core.navigation.entry.NavEntryBuilder
import com.saion.core.navigation.navigator.AppNavigator
import com.saion.feature.home.api.key.HomeNavKey
import com.saion.feature.home.impl.ui.HomeScreen
import com.saion.feature.main.api.key.MainTabNavKey
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
        NavEntryBuilder { scope: EntryProviderScope<MainTabNavKey>, _: AppNavigator<MainTabNavKey> ->
            with(scope) {
                entry<HomeNavKey> {
                    HomeScreen()
                }
            }
        }
}
