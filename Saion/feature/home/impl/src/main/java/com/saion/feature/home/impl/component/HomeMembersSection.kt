package com.saion.feature.home.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.saion.core.model.home.CircleMember
import com.saion.core.ui.ext.dashBorder
import com.saion.core.ui.ext.noRippleClickable
import com.saion.ds.icon.SaionIcons
import com.saion.ds.theme.SaionTheme
import com.saion.ds.token.radius.toRoundedCornerShape
import com.saion.feature.home.impl.R
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun HomeMembersSection(
    members: ImmutableList<CircleMember>,
    canInvite: Boolean,
    onInviteClick: () -> Unit,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val meSuffix = stringResource(R.string.home_member_me_suffix)

    Column(modifier = modifier.fillMaxWidth()) {
        HomeSectionHeader(
            title = stringResource(R.string.home_members_title),
            actionLabel = stringResource(R.string.home_view_all),
            onActionClick = onViewAllClick,
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(items = members, key = { member -> member.memberId }) { member ->
                HomeMemberItem(member = member, meSuffix = meSuffix)
            }

            if (canInvite.not()) return@LazyRow
            item { HomeInviteMemberItem(onClick = onInviteClick) }
        }
    }
}

@Composable
private fun HomeMemberItem(
    member: CircleMember,
    meSuffix: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(SaionTheme.radius.component.full.toRoundedCornerShape())
                .background(member.avatarColor.toColorOrDefault()),
        )
        ItemTitle(text = member.nickname + if (member.isMe) meSuffix else "")
    }
}

@Composable
private fun HomeInviteMemberItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = SaionTheme.radius.component.full.toRoundedCornerShape()
    val dashColor = SaionTheme.colors.line.default

    Column(
        modifier = modifier.noRippleClickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(shape = shape)
                .dashBorder(
                    width = 1.5.dp,
                    color = dashColor,
                    shape = shape,
                    on = 10.dp,
                    off = 8.dp,
                )
                .padding(20.dp),
        ) {
            Icon(
                imageVector = SaionIcons.Plus,
                contentDescription = "",
                tint = SaionTheme.colors.line.strong,
            )
        }
        ItemTitle(text = stringResource(R.string.home_invite_family))
    }
}

@Composable
private fun ItemTitle(
    text : String,
) {
    Text(
        text = text,
        style = SaionTheme.typography.label1Subtle,
        color = SaionTheme.colors.label.subtle,
    )
}

private fun String.toColorOrDefault(): Color = runCatching { Color(toColorInt()) }.getOrElse { Color(0xFFE6E6E6) }

@Preview(showBackground = true)
@Composable
private fun HomeMembersSectionPreview() {
    SaionTheme {
        HomeMembersSection(
            members = listOf(
                CircleMember(
                    memberId = "1",
                    nickname = "수빈",
                    avatarColor = "#FFD35C",
                    isMe = true,
                    role = "MEMBER",
                ),
            ).toImmutableList(),
            canInvite = true,
            onInviteClick = {},
            onViewAllClick = {},
        )
    }
}
