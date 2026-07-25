package com.saion.feature.home.impl.home

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.saion.core.model.circle.CircleSummary
import com.saion.core.model.home.CircleMember
import com.saion.core.model.schedule.ScheduleStatus
import com.saion.core.model.schedule.ScheduleSummary
import com.saion.core.model.schedule.ScheduleUrgencyLevel
import com.saion.core.ui.component.SaionScaffold
import com.saion.core.ui.component.SaionSnackbarHost
import com.saion.core.ui.component.SaionSnackbarVariant
import com.saion.core.ui.component.SystemBarInset
import com.saion.core.ui.component.showSaionSnackbar
import com.saion.core.ui.error.resolveMessage
import com.saion.core.ui.ext.CollectWithLifecycle
import com.saion.ds.component.feedback.SaionConfirmDialog
import com.saion.ds.component.feedback.SaionSpinner
import com.saion.ds.theme.SaionTheme
import com.saion.feature.home.impl.R
import com.saion.feature.home.impl.home.component.HomeDateHeader
import com.saion.feature.home.impl.home.component.HomeFab
import com.saion.feature.home.impl.home.component.HomeHeroCard
import com.saion.feature.home.impl.home.component.HomeMembersSection
import com.saion.feature.home.impl.home.component.HomeScheduleSection
import com.saion.feature.home.impl.home.component.HomeTitleSection
import com.saion.feature.home.impl.home.component.HomeTopBar
import com.saion.feature.home.impl.home.viewmodel.HomeEffect
import com.saion.feature.home.impl.home.viewmodel.HomeIntent
import com.saion.feature.home.impl.home.viewmodel.HomeSnackbarMessage
import com.saion.feature.home.impl.home.viewmodel.HomeState
import com.saion.feature.home.impl.home.viewmodel.HomeViewModel
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun HomeScreen(
    onNotificationClick: () -> Unit = {},
    onCreateCircleClick: () -> Unit = {},
    onJoinCircleClick: () -> Unit = {},
    onScheduleAddClick: () -> Unit = {},
    onScheduleListClick: () -> Unit = {},
    onScheduleClick: (String) -> Unit = {},
    onMemberListClick: () -> Unit = {},
    viewModel: HomeViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    viewModel.uiEffect.CollectWithLifecycle { effect ->
        when (effect) {
            is HomeEffect.ShowSnackbar -> snackbarHostState.showSaionSnackbar(
                message = effect.message.resolve(context),
                variant = effect.message.variant(),
            )
        }
    }

    HomeScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onNotificationClick = onNotificationClick,
        onInviteClick = { viewModel.dispatch(HomeIntent.InviteClicked) },
        onCreateCircleClick = onCreateCircleClick,
        onJoinCircleClick = onJoinCircleClick,
        onScheduleAddClick = onScheduleAddClick,
        onScheduleListClick = onScheduleListClick,
        onScheduleClick = onScheduleClick,
        onHeroScheduleShareClick = { viewModel.dispatch(HomeIntent.HeroScheduleShareClicked) },
        onFamilyNotificationDialogDismiss = { viewModel.dispatch(HomeIntent.DismissFamilyNotificationDialog) },
        onFamilyNotificationConfirm = { viewModel.dispatch(HomeIntent.ConfirmFamilyNotification) },
        onMemberListClick = onMemberListClick,
    )
}

private val HOME_BACKGROUND: Brush = Brush.verticalGradient(
    0f to Color(0xFFFFF9E6),
    1f to Color(0xFFF3F4F2),
)

@Composable
private fun HomeScreen(
    uiState: HomeState,
    snackbarHostState: SnackbarHostState,
    onNotificationClick: () -> Unit,
    onInviteClick: () -> Unit,
    onCreateCircleClick: () -> Unit,
    onJoinCircleClick: () -> Unit,
    onScheduleAddClick: () -> Unit,
    onScheduleListClick: () -> Unit,
    onScheduleClick: (String) -> Unit,
    onHeroScheduleShareClick: () -> Unit,
    onFamilyNotificationDialogDismiss: () -> Unit,
    onFamilyNotificationConfirm: () -> Unit,
    onMemberListClick: () -> Unit,
) {
    SaionScaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = HOME_BACKGROUND),
        containerColor = Color.Transparent,
        snackbarHost = { SaionSnackbarHost(hostState = snackbarHostState) },
        systemBarInset = SystemBarInset.None,
        topBar = { HomeTopBar(onNotificationClick = onNotificationClick) },
        floatingActionButton = {
            if (uiState is HomeState.Content) HomeFab(onClick = onScheduleAddClick)
        },
    ) {
        HomeContent(
            uiState = uiState,
            onInviteClick = onInviteClick,
            onCreateCircleClick = onCreateCircleClick,
            onJoinCircleClick = onJoinCircleClick,
            onScheduleAddClick = onScheduleAddClick,
            onScheduleListClick = onScheduleListClick,
            onScheduleClick = onScheduleClick,
            onHeroScheduleShareClick = onHeroScheduleShareClick,
            onMemberListClick = onMemberListClick,
        )
    }

    if (uiState is HomeState.Content && uiState.isFamilyNotificationDialogVisible) {
        SaionConfirmDialog(
            title = stringResource(R.string.home_family_notification_dialog_title),
            confirmButtonText = stringResource(R.string.home_family_notification_dialog_confirm),
            onConfirm = onFamilyNotificationConfirm,
            dismissButtonText = stringResource(R.string.home_family_notification_dialog_cancel),
            onDismiss = onFamilyNotificationDialogDismiss,
        )
    }
}

@Composable
private fun HomeContent(
    uiState: HomeState,
    onInviteClick: () -> Unit,
    onCreateCircleClick: () -> Unit,
    onJoinCircleClick: () -> Unit,
    onScheduleAddClick: () -> Unit,
    onScheduleListClick: () -> Unit,
    onScheduleClick: (String) -> Unit,
    onHeroScheduleShareClick: () -> Unit,
    onMemberListClick: () -> Unit,
) {
    if (uiState == HomeState.Loading) {
        HomeLoadingContent()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        HomeTitleSection(circleName = uiState.circleTitle)
        HomeDateHeader()
        if (uiState.shouldShowHero) {
            HomeHeroCard(
                state = uiState,
                onInviteClick = onInviteClick,
                onCreateCircleClick = onCreateCircleClick,
                onJoinCircleClick = onJoinCircleClick,
                onScheduleClick = onScheduleClick,
                onHeroScheduleShareClick = onHeroScheduleShareClick,
            )
        }

        when (uiState) {
            HomeState.None -> Unit

            is HomeState.Content -> {
                HomeScheduleSection(
                    schedules = uiState.sectionSchedules,
                    shouldShowAddSchedule = uiState.shouldShowAddSchedule,
                    onAddClick = onScheduleAddClick,
                    onViewAllClick = onScheduleListClick,
                    onScheduleClick = onScheduleClick,
                    modifier = Modifier.fillMaxWidth(),
                )
                HomeMembersSection(
                    members = uiState.members,
                    canInvite = uiState.canInvite,
                    isInviteEnabled = uiState.isInviting.not(),
                    onInviteClick = onInviteClick,
                    onViewAllClick = onMemberListClick,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(52.dp))
            }
        }
    }
}

@Composable
private fun HomeLoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp, bottom = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        SaionSpinner()
    }
}

private val HomeState.circleTitle: String
    @Composable get() = when (this) {
        HomeState.None -> stringResource(R.string.home_circle_title_none)
        is HomeState.Content -> circle.name
        HomeState.Loading -> stringResource(R.string.home_circle_title_loading)
    }

private fun HomeSnackbarMessage.resolve(context: Context): String = when (this) {
    is HomeSnackbarMessage.Text -> value.ifBlank { context.getString(defaultMessageResId) }
    is HomeSnackbarMessage.Error -> error.resolveMessage(context, defaultMessageResId)
}

private fun HomeSnackbarMessage.variant(): SaionSnackbarVariant? = when (this) {
    is HomeSnackbarMessage.Error -> SaionSnackbarVariant.Negative
    is HomeSnackbarMessage.Text -> null
}

@Preview
@Composable
private fun HomeScreenNonePreview() {
    SaionTheme {
        HomeScreen(
            uiState = HomeState.None,
            snackbarHostState = remember { SnackbarHostState() },
            onNotificationClick = {},
            onInviteClick = {},
            onCreateCircleClick = {},
            onJoinCircleClick = {},
            onScheduleAddClick = {},
            onScheduleListClick = {},
            onScheduleClick = {},
            onHeroScheduleShareClick = {},
            onFamilyNotificationDialogDismiss = {},
            onFamilyNotificationConfirm = {},
            onMemberListClick = {},
        )
    }
}

@Preview
@Composable
private fun HomeScreenLoadingPreview() {
    SaionTheme {
        HomeScreen(
            uiState = HomeState.Loading,
            snackbarHostState = remember { SnackbarHostState() },
            onNotificationClick = {},
            onInviteClick = {},
            onCreateCircleClick = {},
            onJoinCircleClick = {},
            onScheduleAddClick = {},
            onScheduleListClick = {},
            onScheduleClick = {},
            onHeroScheduleShareClick = {},
            onFamilyNotificationDialogDismiss = {},
            onFamilyNotificationConfirm = {},
            onMemberListClick = {},
        )
    }
}

@Preview
@Composable
private fun HomeScreenContentPreview() {
    SaionTheme {
        HomeScreen(
            uiState = HomeState.Content(
                circle = CircleSummary(
                    circleId = "circle-1",
                    name = "비니네",
                    ownerId = "owner-1",
                ),
                members = listOf(
                    CircleMember(
                        memberId = "1",
                        nickname = "수빈",
                        avatarColor = "#FFE3A3",
                        profileImageUrl = null,
                        isMe = true,
                        role = "MEMBER",
                    ),
                    CircleMember(
                        memberId = "2",
                        nickname = "아빠",
                        avatarColor = "#D8EEFF",
                        profileImageUrl = null,
                        isMe = false,
                        role = "MEMBER",
                    ),
                ).toImmutableList(),
                canInvite = true,
                isInviting = false,
                mainSchedule = ScheduleSummary(
                    scheduleId = "schedule-0",
                    title = "오늘 저녁",
                    startDate = "2026-07-17",
                    endDate = "2026-07-17",
                    startTime = "18:00",
                    endTime = "19:00",
                    isAllDay = false,
                    needConfirm = false,
                    status = ScheduleStatus.UPCOMING,
                    urgencyLevel = ScheduleUrgencyLevel.URGENT,
                    progressRate = 0,
                    dday = 0,
                ),
                schedules = listOf(
                    ScheduleSummary(
                        scheduleId = "schedule-1",
                        title = "가족 식사",
                        startDate = "2026-07-20",
                        endDate = "2026-07-20",
                        startTime = "18:00",
                        endTime = "20:00",
                        isAllDay = false,
                        needConfirm = false,
                        status = ScheduleStatus.UPCOMING,
                        urgencyLevel = ScheduleUrgencyLevel.URGENT,
                        progressRate = 0,
                        dday = 3,
                    ),
                    ScheduleSummary(
                        scheduleId = "schedule-2",
                        title = "장보기",
                        startDate = "2026-07-22",
                        endDate = "2026-07-22",
                        startTime = "15:00",
                        endTime = "16:00",
                        isAllDay = false,
                        needConfirm = false,
                        status = ScheduleStatus.UPCOMING,
                        urgencyLevel = ScheduleUrgencyLevel.NORMAL,
                        progressRate = 0,
                        dday = 5,
                    ),
                ).toImmutableList(),
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onNotificationClick = {},
            onInviteClick = {},
            onCreateCircleClick = {},
            onJoinCircleClick = {},
            onScheduleAddClick = {},
            onScheduleListClick = {},
            onScheduleClick = {},
            onHeroScheduleShareClick = {},
            onFamilyNotificationDialogDismiss = {},
            onFamilyNotificationConfirm = {},
            onMemberListClick = {},
        )
    }
}
