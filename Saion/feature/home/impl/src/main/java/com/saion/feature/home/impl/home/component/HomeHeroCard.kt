package com.saion.feature.home.impl.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.saion.core.model.circle.CircleSummary
import com.saion.core.model.home.CircleMember
import com.saion.core.model.schedule.ScheduleStatus
import com.saion.core.model.schedule.ScheduleSummary
import com.saion.core.model.schedule.ScheduleUrgencyLevel
import com.saion.ds.component.button.ButtonSize
import com.saion.ds.component.button.ButtonVariant
import com.saion.ds.component.button.SaionButton
import com.saion.ds.theme.SaionTheme
import com.saion.ds.token.radius.toRoundedCornerShape
import com.saion.feature.home.impl.R
import com.saion.feature.home.impl.home.viewmodel.HomeState
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun HomeHeroCard(
    state: HomeState,
    onInviteClick: () -> Unit,
    onCreateCircleClick: () -> Unit,
    onScheduleClick: (String) -> Unit,
    onHeroScheduleShareClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (val content = state.toHeroCardContent(onInviteClick = onInviteClick, onCreateCircleClick = onCreateCircleClick)) {
        is HeroCardContent.Default -> HomeDefaultHeroCard(
            content = content,
            modifier = modifier,
        )

        is HeroCardContent.Schedule -> HomeScheduleHeroCard(
            schedule = content.schedule,
            onScheduleClick = { onScheduleClick(content.schedule.scheduleId) },
            onShareClick = onHeroScheduleShareClick,
            shouldShowShareButton = state is HomeState.Content && state.canRequestFamilyNotification,
            isShareEnabled = state is HomeState.Content && state.isRequestingFamilyNotification.not(),
            modifier = modifier,
        )
    }
}

@Composable
private fun HomeDefaultHeroCard(
    content: HeroCardContent.Default,
    modifier: Modifier = Modifier,
) {
    HeroCardSurface(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_letter),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .aspectRatio(120 / 95f),
                    contentScale = ContentScale.Fit,
                )
            }

            Text(
                text = content.title,
                style = SaionTheme.typography.title1,
                color = SaionTheme.colors.label.default,
            )

            content.buttonText?.let { buttonText ->
                SaionButton(
                    text = buttonText,
                    onClick = content.onClick,
                    modifier = Modifier.fillMaxWidth(),
                    variant = ButtonVariant.PRIMARY,
                    size = ButtonSize.LARGE,
                    enabled = content.isEnabled,
                )
            }
        }
    }
}

@Composable
internal fun HeroCardSurface(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = SaionTheme.radius.container.xLarge.toRoundedCornerShape()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .dropShadow(shadow = SaionTheme.shadow.container, shape = shape)
            .background(SaionTheme.colors.background.default)
            .padding(20.dp),
        content = content,
    )
}

@Immutable
private sealed interface HeroCardContent {
    data class Default(
        val title: String,
        val buttonText: String?,
        val onClick: () -> Unit,
        val isEnabled: Boolean = true,
    ) : HeroCardContent

    data class Schedule(val schedule: ScheduleSummary) : HeroCardContent
}

@Composable
private fun HomeState.toHeroCardContent(
    onInviteClick: () -> Unit,
    onCreateCircleClick: () -> Unit,
): HeroCardContent = when (this) {
    HomeState.None -> HeroCardContent.Default(
        title = stringResource(R.string.home_hero_empty_title),
        buttonText = stringResource(R.string.home_hero_create_circle),
        onClick = onCreateCircleClick,
    )

    is HomeState.Content -> heroSchedule?.let { schedule ->
        HeroCardContent.Schedule(schedule = schedule)
    } ?: HeroCardContent.Default(
        title = stringResource(R.string.home_hero_invite_title),
        buttonText = if (canInvite) stringResource(R.string.home_hero_send_invitation) else null,
        onClick = onInviteClick,
        isEnabled = isInviting.not(),
    )

    HomeState.Loading -> error("Loading state should not render HomeHeroCard.")
}

@Preview(showBackground = true)
@Composable
private fun HomeHeroCardNonePreview() {
    SaionTheme {
        HomeHeroCard(
            state = HomeState.None,
            onInviteClick = {},
            onCreateCircleClick = {},
            onScheduleClick = {},
            onHeroScheduleShareClick = {},
            modifier = Modifier.padding(24.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeHeroCardSchedulePreview() {
    SaionTheme {
        HomeHeroCard(
            state = HomeState.Content(
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
                ).toImmutableList(),
                canInvite = true,
                isInviting = false,
                mainSchedule = ScheduleSummary(
                    scheduleId = "schedule-1",
                    title = "엄마 생신",
                    startDate = "2026-07-24",
                    endDate = "2026-07-24",
                    startTime = "12:00",
                    endTime = "23:00",
                    isAllDay = false,
                    needConfirm = false,
                    status = ScheduleStatus.UPCOMING,
                    urgencyLevel = ScheduleUrgencyLevel.URGENT,
                    progressRate = 40,
                    dday = 7,
                ),
                schedules = emptyList<ScheduleSummary>().toImmutableList(),
            ),
            onInviteClick = {},
            onCreateCircleClick = {},
            onScheduleClick = {},
            onHeroScheduleShareClick = {},
            modifier = Modifier.padding(24.dp),
        )
    }
}
