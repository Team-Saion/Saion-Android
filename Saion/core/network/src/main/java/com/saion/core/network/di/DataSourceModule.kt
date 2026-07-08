package com.saion.core.network.di

import com.saion.core.network.datasource.AuthRemoteDataSource
import com.saion.core.network.datasource.CircleRemoteDataSource
import com.saion.core.network.datasource.DefaultAuthRemoteDataSource
import com.saion.core.network.datasource.DefaultCircleRemoteDataSource
import com.saion.core.network.datasource.DefaultInvitationRemoteDataSource
import com.saion.core.network.datasource.DefaultMemberRemoteDataSource
import com.saion.core.network.datasource.DefaultScheduleRemoteDataSource
import com.saion.core.network.datasource.DefaultTermRemoteDataSource
import com.saion.core.network.datasource.InvitationRemoteDataSource
import com.saion.core.network.datasource.MemberRemoteDataSource
import com.saion.core.network.datasource.ScheduleRemoteDataSource
import com.saion.core.network.datasource.TermRemoteDataSource
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
    abstract fun bindCircleRemoteDataSource(impl: DefaultCircleRemoteDataSource): CircleRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindInvitationRemoteDataSource(impl: DefaultInvitationRemoteDataSource): InvitationRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindScheduleRemoteDataSource(impl: DefaultScheduleRemoteDataSource): ScheduleRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindMemberRemoteDataSource(impl: DefaultMemberRemoteDataSource): MemberRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindTermRemoteDataSource(impl: DefaultTermRemoteDataSource): TermRemoteDataSource
}
