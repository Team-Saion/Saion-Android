package com.saion.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import com.saion.core.datastore.datastore.authDataStore
import com.saion.core.datastore.datastore.circleListDataStore
import com.saion.core.datastore.datastore.currentCircleDataStore
import com.saion.core.datastore.datastore.memberProfileDataStore
import com.saion.core.datastore.datastore.notificationSettingDataStore
import com.saion.core.datastore.model.AuthTokens
import com.saion.core.datastore.model.CircleListCache
import com.saion.core.datastore.model.CurrentCircle
import com.saion.core.datastore.model.MemberProfileCache
import com.saion.core.datastore.model.NotificationSettingCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatastoreModule {

    @Provides
    @Singleton
    internal fun provideAuthTokenDataStore(@ApplicationContext context: Context): DataStore<AuthTokens> = context.authDataStore

    @Provides
    @Singleton
    internal fun provideCurrentCircleDataStore(@ApplicationContext context: Context): DataStore<CurrentCircle> = context.currentCircleDataStore

    @Provides
    @Singleton
    internal fun provideCircleListDataStore(@ApplicationContext context: Context): DataStore<CircleListCache> = context.circleListDataStore

    @Provides
    @Singleton
    internal fun provideNotificationSettingDataStore(
        @ApplicationContext context: Context,
    ): DataStore<NotificationSettingCache> = context.notificationSettingDataStore

    @Provides
    @Singleton
    internal fun provideMemberProfileDataStore(
        @ApplicationContext context: Context,
    ): DataStore<MemberProfileCache> = context.memberProfileDataStore
}
