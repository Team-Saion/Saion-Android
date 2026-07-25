package com.saion.feature.circlecreate.impl.viewmodel

import com.saion.core.ui.viewmodel.UIIntent

internal sealed interface CircleJoinIntent : UIIntent {
    data class CodeChanged(val value: String) : CircleJoinIntent

    data object SubmitClicked : CircleJoinIntent
}
