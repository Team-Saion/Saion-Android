package com.saion.core.network.di

import com.saion.core.network.datasource.AuthRemoteDataSource
import com.saion.core.network.datasource.DefaultAuthRemoteDataSource
import com.saion.core.network.datasource.DefaultMemberRemoteDataSource
import com.saion.core.network.datasource.MemberRemoteDataSource
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
    abstract fun bindAuthRemoteDataSource(impl: DefaultAuthRemoteDataSource): AuthRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindMemberRemoteDataSource(impl: DefaultMemberRemoteDataSource): MemberRemoteDataSource
}
