package com.saion.core.auth.di

import com.saion.core.auth.AuthSdkInitializer
import com.saion.core.auth.SocialAuthClient
import com.saion.core.auth.internal.DefaultAuthSdkInitializer
import com.saion.core.auth.internal.KakaoSdkInitializer
import com.saion.core.auth.internal.KakaoSocialAuthClient
import com.saion.core.auth.internal.ProviderSdkInitializer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AuthModule {
    @Binds
    @Singleton
    abstract fun bindAuthSdkInitializer(impl: DefaultAuthSdkInitializer): AuthSdkInitializer

    @Binds
    @IntoSet
    abstract fun bindProviderSdkInitializer(impl: KakaoSdkInitializer): ProviderSdkInitializer

    @Binds
    @Singleton
    abstract fun bindSocialAuthClient(impl: KakaoSocialAuthClient): SocialAuthClient
}
