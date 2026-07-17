package com.saion.core.share.di

import com.saion.core.share.InvitationShareClient
import com.saion.core.share.internal.KakaoInvitationShareClient
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ShareModule {
    @Binds
    @Singleton
    abstract fun bindInvitationShareClient(impl: KakaoInvitationShareClient): InvitationShareClient
}
