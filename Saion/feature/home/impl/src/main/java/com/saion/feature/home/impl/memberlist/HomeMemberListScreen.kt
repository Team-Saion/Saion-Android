package com.saion.feature.home.impl.memberlist

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.saion.core.model.home.CircleMember
import com.saion.core.ui.component.SaionProfile
import com.saion.core.ui.component.SaionScaffold
import com.saion.core.ui.component.SaionSnackbarHost
import com.saion.core.ui.component.SaionSnackbarVariant
import com.saion.core.ui.component.SystemBarInset
import com.saion.core.ui.component.showSaionSnackbar
import com.saion.core.ui.error.resolveMessage
import com.saion.core.ui.ext.CollectWithLifecycle
import com.saion.ds.component.button.SaionTextButton
import com.saion.ds.component.button.TextButtonSize
import com.saion.ds.component.feedback.SaionConfirmDialog
import com.saion.ds.component.feedback.SaionSpinner
import com.saion.ds.component.navigation.SaionTopBar
import com.saion.ds.component.navigation.TopBarVariant
import com.saion.ds.theme.SaionTheme
import com.saion.feature.home.impl.R
import com.saion.feature.home.impl.memberlist.viewmodel.HomeMemberListEffect
import com.saion.feature.home.impl.memberlist.viewmodel.HomeMemberListIntent
import com.saion.feature.home.impl.memberlist.viewmodel.HomeMemberListSnackbarMessage
import com.saion.feature.home.impl.memberlist.viewmodel.HomeMemberListState
import com.saion.feature.home.impl.memberlist.viewmodel.HomeMemberListStatus
import com.saion.feature.home.impl.memberlist.viewmodel.HomeMemberListViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun HomeMemberListScreen(
    onBack: () -> Unit,
    onLeaveCompleted: () -> Unit,
    viewModel: HomeMemberListViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    viewModel.uiEffect.CollectWithLifecycle { effect ->
        when (effect) {
            is HomeMemberListEffect.ShowSnackbar -> snackbarHostState.showSaionSnackbar(
                message = effect.message.resolve(context),
                variant = effect.message.variant(),
            )

            HomeMemberListEffect.LeaveCompleted -> onLeaveCompleted()
        }
    }

    HomeMemberListScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onLeaveClick = { viewModel.dispatch(HomeMemberListIntent.ClickLeave) },
        onLeaveDismiss = { viewModel.dispatch(HomeMemberListIntent.DismissLeaveDialog) },
        onLeaveConfirm = { viewModel.dispatch(HomeMemberListIntent.ConfirmLeave) },
    )
}

@Composable
private fun HomeMemberListScreen(
    uiState: HomeMemberListState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onLeaveClick: () -> Unit,
    onLeaveDismiss: () -> Unit,
    onLeaveConfirm: () -> Unit,
) {
    SaionScaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SaionSnackbarHost(hostState = snackbarHostState) },
        containerColor = SaionTheme.colors.background.muted,
        systemBarInset = SystemBarInset.None,
        topBar = {
            SaionTopBar(
                containerColor = SaionTheme.colors.background.muted,
                variant = TopBarVariant.Standard(
                    title = null,
                    onBack = onBack,
                ),
                actions = {
                    SaionTextButton(
                        text = stringResource(R.string.home_member_list_leave),
                        size = TextButtonSize.MEDIUM,
                        enabled = uiState.isLeaveLoading.not(),
                        onClick = onLeaveClick,
                        modifier = Modifier.padding(end = 4.dp),
                    )
                },
                modifier = Modifier.statusBarsPadding(),
            )
        },
    ) {
        when (uiState.status) {
            HomeMemberListStatus.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    SaionSpinner()
                }
            }

            HomeMemberListStatus.Empty -> {
                HomeMemberListPlaceholder(
                    text = stringResource(R.string.home_member_list_empty),
                )
            }

            HomeMemberListStatus.Error -> {
                HomeMemberListPlaceholder(
                    text = stringResource(R.string.home_member_list_error_fallback),
                )
            }

            HomeMemberListStatus.Content -> {
                HomeMemberListContent(members = uiState.members)
            }
        }
    }

    if (uiState.showLeaveDialog) {
        SaionConfirmDialog(
            title = stringResource(R.string.home_member_list_leave_dialog_title),
            confirmButtonText = stringResource(R.string.home_member_list_leave),
            onConfirm = onLeaveConfirm,
            dismissButtonText = stringResource(R.string.home_member_list_leave_dialog_cancel),
            onDismiss = onLeaveDismiss,
            isDanger = true,
        )
    }
}

@Composable
private fun HomeMemberListContent(members: ImmutableList<CircleMember>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        items(items = members, key = CircleMember::memberId) { member ->
            HomeMemberListItem(member = member)
        }
    }
}

@Composable
private fun HomeMemberListItem(
    member: CircleMember,
    modifier: Modifier = Modifier,
) {
    val meSuffix = stringResource(R.string.home_member_me_suffix)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SaionProfile(
            nickname = member.nickname,
            textStyle = SaionTheme.typography.label1Strong,
            imageUrl = member.profileImageUrl,
            avatarColorHex = member.avatarColor,
            contentDescription = member.nickname,
            modifier = Modifier.size(40.dp),
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = if (member.isMe) member.nickname + meSuffix else member.nickname,
                style = SaionTheme.typography.title3Strong,
                color = SaionTheme.colors.label.default,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun HomeMemberListPlaceholder(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = SaionTheme.typography.body1,
            color = SaionTheme.colors.label.subtle,
        )
    }
}

private fun HomeMemberListSnackbarMessage.resolve(context: Context): String = when (this) {
    is HomeMemberListSnackbarMessage.Text -> value.ifBlank { context.getString(defaultMessageResId) }
    is HomeMemberListSnackbarMessage.Error -> error.resolveMessage(context, defaultMessageResId)
}

private fun HomeMemberListSnackbarMessage.variant(): SaionSnackbarVariant? = when (this) {
    is HomeMemberListSnackbarMessage.Error -> SaionSnackbarVariant.Negative
    is HomeMemberListSnackbarMessage.Text -> null
}

@Preview(showBackground = true)
@Composable
private fun HomeMemberListScreenPreview() {
    SaionTheme {
        HomeMemberListScreen(
            uiState = HomeMemberListState(
                status = HomeMemberListStatus.Content,
                members = listOf(
                    CircleMember(
                        memberId = "1",
                        nickname = "닉네임",
                        avatarColor = "#5CE0B1",
                        profileImageUrl = null,
                        isMe = true,
                        role = "MEMBER",
                    ),
                    CircleMember(
                        memberId = "2",
                        nickname = "닉네임",
                        avatarColor = "#C784F7",
                        profileImageUrl = null,
                        isMe = false,
                        role = "MEMBER",
                    ),
                ).toImmutableList(),
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onBack = {},
            onLeaveClick = {},
            onLeaveDismiss = {},
            onLeaveConfirm = {},
        )
    }
}
