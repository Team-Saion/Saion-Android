package com.saion.core.datastore.di

import com.saion.core.datastore.datasource.AuthLocalDataSource
import com.saion.core.datastore.datasource.CurrentCircleLocalDataSource
import com.saion.core.datastore.datasource.DefaultAuthLocalDataSource
import com.saion.core.datastore.datasource.DefaultCurrentCircleLocalDataSource
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

    @Binds
    @Singleton
    internal abstract fun bindCurrentCircleLocalDataSource(impl: DefaultCurrentCircleLocalDataSource): CurrentCircleLocalDataSource
}
