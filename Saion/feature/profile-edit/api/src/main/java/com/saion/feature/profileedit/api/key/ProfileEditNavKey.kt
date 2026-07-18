package com.saion.feature.profileedit.api.key

import com.saion.feature.main.api.key.MainTabNavKey
import kotlinx.serialization.Serializable

@Serializable
data class ProfileEditNavKey(
    val entryPoint: ProfileEditEntryPoint = ProfileEditEntryPoint.MY_PAGE,
) : MainTabNavKey
