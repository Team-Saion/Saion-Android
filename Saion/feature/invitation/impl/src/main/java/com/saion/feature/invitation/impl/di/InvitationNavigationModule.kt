package com.saion.feature.invitation.impl.di

import androidx.navigation3.runtime.EntryProviderScope
import com.saion.core.navigation.entry.NavEntryBuilder
import com.saion.core.navigation.key.AppNavKey
import com.saion.core.navigation.navigator.AppNavigator
import com.saion.feature.invitation.api.key.InvitationAcceptNavKey
import com.saion.feature.invitation.impl.InvitationAcceptScreen
import com.saion.feature.main.api.key.MainNavKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
object InvitationNavigationModule {
    @Provides
    @IntoSet
    fun provideInvitationEntryBuilder(): NavEntryBuilder<AppNavKey> =
        NavEntryBuilder { scope: EntryProviderScope<AppNavKey>, navigator: AppNavigator<AppNavKey> ->
            with(scope) {
                entry<InvitationAcceptNavKey> { key ->
                    InvitationAcceptScreen(
                        token = key.token,
                        onClose = {
                            if (!navigator.pop()) {
                                navigator.replaceAll(MainNavKey)
                            }
                        },
                        onComplete = {
                            navigator.replaceAll(MainNavKey)
                        },
                    )
                }
            }
        }
}
