package com.saion.feature.home.impl.memberlist.viewmodel

import androidx.compose.runtime.Immutable
import com.saion.core.model.home.CircleMember
import com.saion.core.ui.viewmodel.UIState
import kotlinx.collections.immutable.ImmutableList

@Immutable
internal sealed interface HomeMemberListState : UIState {
    data object Loading : HomeMemberListState

    data object Empty : HomeMemberListState

    data object Error : HomeMemberListState

    data class Content(val members: ImmutableList<CircleMember>) : HomeMemberListState
}
