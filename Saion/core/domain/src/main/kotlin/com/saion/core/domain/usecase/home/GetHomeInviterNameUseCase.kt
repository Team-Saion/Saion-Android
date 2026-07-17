package com.saion.core.domain.usecase.home

import com.saion.core.model.home.CircleMember
import javax.inject.Inject

class GetHomeInviterNameUseCase @Inject constructor() {
    operator fun invoke(members: List<CircleMember>): String = members
        .firstOrNull { it.isMe }
        ?.nickname
        ?.takeIf(String::isNotBlank)
        ?: DEFAULT_INVITER_NAME
}

private const val DEFAULT_INVITER_NAME = "가족"
