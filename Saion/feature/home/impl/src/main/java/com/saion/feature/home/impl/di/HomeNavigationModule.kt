package com.saion.feature.home.impl.di

import androidx.navigation3.runtime.EntryProviderScope
import com.saion.core.navigation.entry.NavEntryBuilder
import com.saion.core.navigation.navigator.AppNavigator
import com.saion.feature.circlecreate.api.key.CircleCreateNavKey
import com.saion.feature.home.api.key.HomeMemberListNavKey
import com.saion.feature.home.api.key.HomeNavKey
import com.saion.feature.home.impl.home.HomeScreen
import com.saion.feature.home.impl.memberlist.HomeMemberListScreen
import com.saion.feature.main.api.key.MainTabNavKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
object HomeNavigationModule {
    @Provides
    @IntoSet
    fun provideHomeEntryBuilder(): NavEntryBuilder<MainTabNavKey> =
        NavEntryBuilder { scope: EntryProviderScope<MainTabNavKey>, navigator: AppNavigator<MainTabNavKey> ->
            with(scope) {
                entry<HomeNavKey> {
                    HomeScreen(
                        onCreateCircleClick = { navigator.push(CircleCreateNavKey) },
                        onMemberListClick = {
                            navigator.push(HomeMemberListNavKey)
                        },
                    )
                }

                entry<HomeMemberListNavKey> {
                    HomeMemberListScreen(
                        onBack = { navigator.pop() },
                    )
                }
            }
        }
}
