package com.saion.feature.mypage.impl.di

import androidx.navigation3.runtime.EntryProviderScope
import com.saion.core.navigation.entry.NavEntryBuilder
import com.saion.core.navigation.navigator.AppNavigator
import com.saion.feature.main.api.key.MainTabNavKey
import com.saion.feature.mypage.api.key.MyPageNavKey
import com.saion.feature.mypage.impl.mypage.MyPageScreen
import com.saion.feature.terms.api.key.TermDetailNavKey
import com.saion.feature.terms.api.key.TermsAgreementNavKey
import com.saion.feature.terms.api.key.TermsMode
import com.saion.feature.terms.impl.TermDetailScreen
import com.saion.feature.terms.impl.TermsScreen
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
        NavEntryBuilder { scope: EntryProviderScope<MainTabNavKey>, navigator: AppNavigator<MainTabNavKey> ->
            with(scope) {
                entry<MyPageNavKey> {
                    MyPageScreen(
                        onTermsClick = { navigator.push(TermsAgreementNavKey(mode = TermsMode.READ_ONLY)) },
                    )
                }
                entry<TermsAgreementNavKey> { key ->
                    TermsScreen(
                        mode = key.mode,
                        onBack = { navigator.pop() },
                        onComplete = { navigator.pop() },
                        onOpenTerm = { _, url ->
                            navigator.push(TermDetailNavKey(url = url))
                        },
                    )
                }
                entry<TermDetailNavKey> { key ->
                    TermDetailScreen(
                        url = key.url,
                        onBack = { navigator.pop() },
                    )
                }
            }
        }
}
