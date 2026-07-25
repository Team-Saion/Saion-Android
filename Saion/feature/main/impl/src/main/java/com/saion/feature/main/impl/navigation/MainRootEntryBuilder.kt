package com.saion.feature.main.impl.navigation

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import com.saion.core.domain.usecase.circle.ObserveResolvedCurrentCircleUseCase
import com.saion.core.domain.usecase.circle.ResolvedCurrentCircle
import com.saion.core.navigation.entry.NavEntryBuilder
import com.saion.core.navigation.key.AppNavKey
import com.saion.core.navigation.navigator.AppNavigator
import com.saion.core.navigation.state.rememberTabNavigationState
import com.saion.core.navigation.ui.AppTabNavigationHost
import com.saion.core.ui.component.SaionScaffold
import com.saion.core.ui.component.SaionSnackbarHost
import com.saion.core.ui.component.SaionSnackbarVariant
import com.saion.core.ui.component.SystemBarInset
import com.saion.core.ui.component.showSaionSnackbar
import com.saion.ds.component.navigation.SaionBottomNavItem
import com.saion.ds.component.navigation.SaionBottomNavigation
import com.saion.ds.icon.SaionIcons
import com.saion.feature.home.api.key.HomeNavKey
import com.saion.feature.main.api.key.MainNavKey
import com.saion.feature.main.api.key.MainTabNavKey
import com.saion.feature.main.impl.R
import com.saion.feature.mypage.api.key.MyPageNavKey
import com.saion.feature.schedule.api.key.ScheduleNavKey
import javax.inject.Inject
import kotlin.jvm.JvmSuppressWildcards
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.launch

class MainRootEntryBuilder @Inject constructor(
    private val tabEntryBuilders: Set<@JvmSuppressWildcards NavEntryBuilder<MainTabNavKey>>,
    private val observeResolvedCurrentCircleUseCase: ObserveResolvedCurrentCircleUseCase,
) :
    NavEntryBuilder<AppNavKey> {
    override fun build(
        scope: EntryProviderScope<AppNavKey>,
        navigator: AppNavigator<AppNavKey>,
    ) {
        with(scope) {
            entry<MainNavKey> {
                MainRoute(
                    tabEntryBuilders = tabEntryBuilders.toImmutableSet(),
                    observeResolvedCurrentCircleUseCase = observeResolvedCurrentCircleUseCase,
                )
            }
        }
    }
}

@Composable
private fun MainRoute(
    tabEntryBuilders: ImmutableSet<NavEntryBuilder<MainTabNavKey>>,
    observeResolvedCurrentCircleUseCase: ObserveResolvedCurrentCircleUseCase,
) {
    val context = LocalContext.current
    val navigationState = rememberTabNavigationState(
        HomeNavKey,
        ScheduleNavKey,
        MyPageNavKey,
    )
    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var isExitSnackbarVisible by remember { mutableStateOf(false) }
    val resolvedCurrentCircle by remember(observeResolvedCurrentCircleUseCase) {
        observeResolvedCurrentCircleUseCase()
    }.collectAsStateWithLifecycle(initialValue = null)
    val hasJoinedCircle = resolvedCurrentCircle is ResolvedCurrentCircle.Available
    val scheduleTabBlockedMessage = stringResource(R.string.main_schedule_tab_requires_circle)
    val exitAppMessage = stringResource(R.string.main_exit_app_on_back_press)
    val items = listOf(
        TabItem(HomeNavKey, stringResource(R.string.main_tab_home), SaionIcons.Home),
        TabItem(ScheduleNavKey, stringResource(R.string.main_tab_schedule), SaionIcons.Schedule),
        TabItem(MyPageNavKey, stringResource(R.string.main_tab_mypage), SaionIcons.Person),
    )
    val rootTabs = items.map(TabItem::navKey).toSet()
    val shouldShowBottomBar = navigationState.current in rootTabs
    val shouldHandleExitOnBack = shouldShowBottomBar && !navigationState.canPop

    LaunchedEffect(navigationState.selectedTab, navigationState.current, shouldHandleExitOnBack) {
        isExitSnackbarVisible = false
        snackbarHostState.currentSnackbarData?.dismiss()
    }

    BackHandler(enabled = shouldHandleExitOnBack) {
        if (isExitSnackbarVisible) {
            context.findActivity()?.finish()
            return@BackHandler
        }
        coroutineScope.launch {
            isExitSnackbarVisible = true
            snackbarHostState.showSaionSnackbar(
                message = exitAppMessage,
                variant = SaionSnackbarVariant.Cautionary,
                onDismiss = {
                    isExitSnackbarVisible = false
                },
            )
            isExitSnackbarVisible = false
        }
    }

    SaionScaffold(
        modifier = Modifier.fillMaxSize(),
        systemBarInset = SystemBarInset.None,
        snackbarHost = {
            SaionSnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            if (shouldShowBottomBar) {
                SaionBottomNavigation {
                    items.forEach { item ->
                        SaionBottomNavItem(
                            imageVector = item.imageVector,
                            title = item.title,
                            isSelected = navigationState.selectedTab == item.navKey,
                            onClick = {
                                if (item.navKey == ScheduleNavKey && !hasJoinedCircle) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSaionSnackbar(
                                            message = scheduleTabBlockedMessage,
                                            variant = SaionSnackbarVariant.Cautionary,
                                        )
                                    }
                                } else {
                                    navigationState.selectTab(item.navKey)
                                }
                            },
                        )
                    }
                }
            }
        },
    ) {
        AppTabNavigationHost(
            navigationState = navigationState,
            entryBuilders = tabEntryBuilders,
        )
    }
}

@Immutable
private data class TabItem(
    val navKey: MainTabNavKey,
    val title: String,
    val imageVector: ImageVector,
)

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
