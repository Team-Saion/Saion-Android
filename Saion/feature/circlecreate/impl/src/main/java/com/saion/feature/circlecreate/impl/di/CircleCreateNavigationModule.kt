package com.saion.feature.circlecreate.impl.di

import androidx.navigation3.runtime.EntryProviderScope
import com.saion.core.navigation.entry.NavEntryBuilder
import com.saion.core.navigation.navigator.AppNavigator
import com.saion.feature.circlecreate.api.key.CircleCreateNavKey
import com.saion.feature.circlecreate.impl.CircleCreateScreen
import com.saion.feature.main.api.key.MainTabNavKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
object CircleCreateNavigationModule {
    @Provides
    @IntoSet
    fun provideCircleCreateEntryBuilder(): NavEntryBuilder<MainTabNavKey> =
        NavEntryBuilder { scope: EntryProviderScope<MainTabNavKey>, navigator: AppNavigator<MainTabNavKey> ->
            with(scope) {
                entry<CircleCreateNavKey> {
                    CircleCreateScreen(
                        onClose = { navigator.pop() },
                    )
                }
            }
        }
}
