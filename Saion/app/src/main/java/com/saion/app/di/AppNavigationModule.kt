package com.saion.app.di

import com.saion.app.navigation.AppRootEntryBuilder
import com.saion.core.navigation.entry.NavEntryBuilder
import com.saion.core.navigation.key.AppNavKey
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class AppNavigationModule {
    @Binds
    @IntoSet
    abstract fun bindAppRootEntryBuilder(impl: AppRootEntryBuilder): NavEntryBuilder<AppNavKey>
}
