package com.saion.feature.mypage.impl.mypage.viewmodel

import androidx.compose.runtime.Immutable
import com.saion.core.ui.viewmodel.UIState

@Immutable
internal data class MyPageState(
    val nickname: String = "",
    val profileImageUrl: String? = null,
    val avatarColorHex: String = DEFAULT_AVATAR_COLOR_HEX,
    val isLatestVersion: Boolean = true,
    val isLoading: Boolean = true,
) : UIState

internal const val DEFAULT_AVATAR_COLOR_HEX: String = "#E6E6E6"
