package com.saion.core.local.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.saion.core.local.datasource.CoreLocalDataSource
import com.saion.core.local.datasource.CorePreferenceLocalDataSource
import com.saion.core.local.datastore.CoreLocalPreferences
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalModule {
    @Binds
    @Singleton
    abstract fun bindCoreLocalDataSource(
        corePreferenceLocalDataSource: CorePreferenceLocalDataSource,
    ): CoreLocalDataSource

    companion object {
        @Provides
        @Singleton
        fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
            PreferenceDataStoreFactory.create {
                context.preferencesDataStoreFile(CoreLocalPreferences.FILE_NAME)
            }
    }
}
