package com.saion.core.data.di

import com.saion.core.data.datasource.AuthTokenStoreImpl
import com.saion.core.data.repository.AuthRepositoryImpl
import com.saion.core.data.repository.CircleRepositoryImpl
import com.saion.core.data.repository.InvitationRepositoryImpl
import com.saion.core.data.repository.MemberRepositoryImpl
import com.saion.core.data.repository.TermRepositoryImpl
import com.saion.core.domain.repository.AuthRepository
import com.saion.core.domain.repository.CircleRepository
import com.saion.core.domain.repository.InvitationRepository
import com.saion.core.domain.repository.MemberRepository
import com.saion.core.domain.repository.TermRepository
import com.saion.core.network.auth.AuthTokenStore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindCircleRepository(impl: CircleRepositoryImpl): CircleRepository

    @Binds
    @Singleton
    abstract fun bindInvitationRepository(impl: InvitationRepositoryImpl): InvitationRepository

    @Binds
    @Singleton
    abstract fun bindMemberRepository(impl: MemberRepositoryImpl): MemberRepository

    @Binds
    @Singleton
    abstract fun bindTermRepository(impl: TermRepositoryImpl): TermRepository

    @Binds
    @Singleton
    abstract fun bindAuthTokenStore(impl: AuthTokenStoreImpl): AuthTokenStore
}
