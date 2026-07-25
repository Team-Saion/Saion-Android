package com.saion.feature.circlecreate.impl.viewmodel

import com.saion.core.ui.viewmodel.UIEffect

internal sealed interface CircleJoinEffect : UIEffect {
    data class OpenInvitation(val token: String) : CircleJoinEffect
}
