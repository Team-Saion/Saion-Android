package com.saion.feature.main.impl.di

import com.saion.core.navigation.entry.NavEntryBuilder
import com.saion.core.navigation.key.AppNavKey
import com.saion.feature.main.impl.navigation.MainRootEntryBuilder
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class MainNavigationModule {
    @Binds
    @IntoSet
    abstract fun bindMainRootEntryBuilder(impl: MainRootEntryBuilder): NavEntryBuilder<AppNavKey>
}
