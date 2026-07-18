package com.saion.feature.profileedit.impl.di

import com.saion.feature.profileedit.impl.DefaultProfileImageReader
import com.saion.feature.profileedit.impl.ProfileImageReader
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface ProfileEditModule {
    @Binds
    fun bindProfileImageReader(impl: DefaultProfileImageReader): ProfileImageReader
}
