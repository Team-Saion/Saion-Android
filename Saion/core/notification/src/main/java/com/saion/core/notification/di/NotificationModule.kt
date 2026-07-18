package com.saion.core.notification.di

import com.saion.core.notification.NotificationLifecycleManager
import com.saion.core.notification.NotificationLifecycleManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class NotificationModule {
    @Binds
    @Singleton
    abstract fun bindNotificationLifecycleManager(impl: NotificationLifecycleManagerImpl): NotificationLifecycleManager
}
