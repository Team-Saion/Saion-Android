package com.saion.feature.mypage.impl.di

import androidx.navigation3.runtime.EntryProviderScope
import com.saion.core.navigation.entry.NavEntryBuilder
import com.saion.core.navigation.navigator.AppNavigator
import com.saion.feature.main.api.key.MainTabNavKey
import com.saion.feature.mypage.api.key.MyPageNavKey
import com.saion.feature.mypage.impl.mypage.MyPageScreen
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
object MyPageNavigationModule {
    @Provides
    @IntoSet
    fun provideMyPageEntryBuilder(): NavEntryBuilder<MainTabNavKey> =
        NavEntryBuilder { scope: EntryProviderScope<MainTabNavKey>, _: AppNavigator<MainTabNavKey> ->
            with(scope) {
                entry<MyPageNavKey> {
                    MyPageScreen()
                }
            }
        }
}
