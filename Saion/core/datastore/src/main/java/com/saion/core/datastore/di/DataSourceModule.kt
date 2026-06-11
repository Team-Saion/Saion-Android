package com.saion.core.datastore.di

import com.saion.core.datastore.datasource.AuthLocalDataSource
import com.saion.core.datastore.datasource.DefaultAuthLocalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Binds
    @Singleton
    internal abstract fun bindAuthLocalDataSource(impl: DefaultAuthLocalDataSource): AuthLocalDataSource
}
