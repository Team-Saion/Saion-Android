package com.saion.feature.home.impl.home

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.saion.core.ui.component.SaionScaffold
import com.saion.core.ui.component.SystemBarInset
import com.saion.core.ui.error.getString
import com.saion.core.ui.error.resolveMessage
import com.saion.core.ui.ext.CollectWithLifecycle
import com.saion.ds.component.feedback.SaionSpinner
import com.saion.ds.theme.SaionTheme
import com.saion.feature.home.impl.R
import com.saion.feature.home.impl.home.component.HomeDateHeader
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
            is HomeEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message.resolve(context))
        }
    }

    HomeScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onNotificationClick = onNotificationClick,
        onInviteClick = { viewModel.dispatch(HomeIntent.InviteClicked) },
        onCreateCircleClick = onCreateCircleClick,
        onScheduleAddClick = onScheduleAddClick,
        onScheduleListClick = onScheduleListClick,
        onScheduleClick = onScheduleClick,
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
    onScheduleAddClick: () -> Unit,
    onScheduleListClick: () -> Unit,
    onScheduleClick: (String) -> Unit,
    onMemberListClick: () -> Unit,
) {
    SaionScaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = HOME_BACKGROUND),
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        systemBarInset = SystemBarInset.None,
        topBar = { HomeTopBar(onNotificationClick = onNotificationClick) },
    ) {
        HomeContent(
            uiState = uiState,
            onInviteClick = onInviteClick,
            onCreateCircleClick = onCreateCircleClick,
            onScheduleAddClick = onScheduleAddClick,
            onScheduleListClick = onScheduleListClick,
            onScheduleClick = onScheduleClick,
            onMemberListClick = onMemberListClick,
        )
    }
}

@Composable
private fun HomeContent(
    uiState: HomeState,
    onInviteClick: () -> Unit,
    onCreateCircleClick: () -> Unit,
    onScheduleAddClick: () -> Unit,
    onScheduleListClick: () -> Unit,
    onScheduleClick: (String) -> Unit,
    onMemberListClick: () -> Unit,
) {
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
        HomeHeroCard(
            state = uiState,
            onInviteClick = onInviteClick,
            onCreateCircleClick = onCreateCircleClick,
        )

        when (uiState) {
            HomeState.Loading -> SaionSpinner()

            HomeState.None -> Unit

            is HomeState.Content -> {
                HomeScheduleSection(
                    mainSchedule = uiState.mainSchedule,
                    totalScheduleCount = uiState.totalScheduleCount,
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
            }
        }
    }
}

private val HomeState.circleTitle: String
    @Composable get() = when (this) {
        HomeState.Loading -> stringResource(R.string.home_circle_title_loading)
        HomeState.None -> stringResource(R.string.home_circle_title_none)
        is HomeState.Content -> circle.name
    }

private fun HomeSnackbarMessage.resolve(context: Context): String = when (this) {
    is HomeSnackbarMessage.Text -> value.ifBlank { context.getString(defaultMessageResId) }
    is HomeSnackbarMessage.Error -> error.resolveMessage(context, defaultMessageResId)
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
            onScheduleAddClick = {},
            onScheduleListClick = {},
            onScheduleClick = {},
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
                    scheduleId = "schedule-1",
                    title = "가족 식사",
                    startDate = "2026-06-28",
                    endDate = "2026-06-28",
                    startTime = "18:00",
                    endTime = "20:00",
                    isAllDay = false,
                    needConfirm = false,
                    status = ScheduleStatus.UPCOMING,
                    progressRate = 0,
                    dday = 0,
                ),
                schedules = emptyList<ScheduleSummary>().toImmutableList(),
                totalScheduleCount = 3L,
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onNotificationClick = {},
            onInviteClick = {},
            onCreateCircleClick = {},
            onScheduleAddClick = {},
            onScheduleListClick = {},
            onScheduleClick = {},
            onMemberListClick = {},
        )
    }
}
