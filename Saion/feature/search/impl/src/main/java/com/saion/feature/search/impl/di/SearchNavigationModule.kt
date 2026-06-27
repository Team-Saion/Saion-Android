package com.saion.feature.search.impl.di

import androidx.navigation3.runtime.EntryProviderScope
import com.saion.core.navigation.entry.NavEntryBuilder
import com.saion.core.navigation.navigator.AppNavigator
import com.saion.feature.main.api.key.MainTabNavKey
import com.saion.feature.search.api.key.SearchNavKey
import com.saion.feature.search.impl.ui.SearchScreen
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
object SearchNavigationModule {
    @Provides
    @IntoSet
    fun provideSearchEntryBuilder(): NavEntryBuilder<MainTabNavKey> = NavEntryBuilder {
            scope: EntryProviderScope<MainTabNavKey>,
            _: AppNavigator<MainTabNavKey>,
        ->
        with(scope) {
            entry<SearchNavKey> {
                SearchScreen()
            }
        }
    }
}
