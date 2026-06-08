package com.saion.core.network.di

import com.saion.core.network.datasource.AuthRemoteDataSource
import com.saion.core.network.datasource.AuthRemoteDataSourceImpl
import com.saion.core.network.datasource.MemberRemoteDataSource
import com.saion.core.network.datasource.MemberRemoteDataSourceImpl
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
    abstract fun bindAuthRemoteDataSource(authRemoteDataSourceImpl: AuthRemoteDataSourceImpl): AuthRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindMemberRemoteDataSource(memberRemoteDataSourceImpl: MemberRemoteDataSourceImpl): MemberRemoteDataSource
}
