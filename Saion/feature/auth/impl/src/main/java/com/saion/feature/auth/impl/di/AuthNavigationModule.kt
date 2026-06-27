package com.saion.feature.auth.impl.di

import com.saion.core.navigation.entry.NavEntryBuilder
import com.saion.core.navigation.key.AppNavKey
import com.saion.feature.auth.impl.navigation.AuthRootEntryBuilder
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthNavigationModule {
    @Binds
    @IntoSet
    abstract fun bindAuthRootEntryBuilder(impl: AuthRootEntryBuilder): NavEntryBuilder<AppNavKey>
}
