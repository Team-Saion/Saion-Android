package com.saion.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import com.saion.core.datastore.datastore.authDataStore
import com.saion.core.datastore.model.AuthTokens
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
}
