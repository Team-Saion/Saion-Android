package com.saion.core.network.di

import com.saion.core.network.api.AuthService
import com.saion.core.network.api.MemberService
import com.saion.core.network.api.TermService
import com.saion.core.network.client.HttpClientFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    internal fun provideHttpClient(factory: HttpClientFactory): HttpClient = factory.create()

    @Provides
    @Singleton
    internal fun provideAuthService(httpClient: HttpClient): AuthService = AuthService(client = httpClient)

    @Provides
    @Singleton
    internal fun provideMemberService(httpClient: HttpClient): MemberService = MemberService(client = httpClient)

    @Provides
    @Singleton
    internal fun provideTermService(httpClient: HttpClient): TermService = TermService(client = httpClient)
}
