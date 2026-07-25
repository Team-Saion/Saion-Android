package com.saion.feature.home.impl.memberlist.viewmodel

import androidx.compose.runtime.Immutable
import com.saion.core.model.home.CircleMember
import com.saion.core.ui.viewmodel.UIState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
internal data class HomeMemberListState(
    val status: HomeMemberListStatus = HomeMemberListStatus.Loading,
    val members: ImmutableList<CircleMember> = persistentListOf(),
    val showLeaveDialog: Boolean = false,
    val isLeaveLoading: Boolean = false,
) : UIState

internal enum class HomeMemberListStatus {
    Loading,
    Empty,
    Error,
    Content,
}
