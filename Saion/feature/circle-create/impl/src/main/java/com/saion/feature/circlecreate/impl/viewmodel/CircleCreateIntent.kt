package com.saion.feature.circlecreate.impl.viewmodel

import com.saion.core.ui.viewmodel.UIIntent

internal sealed interface CircleCreateIntent : UIIntent {
    data class NameChanged(val value: String) : CircleCreateIntent

    data object SubmitClicked : CircleCreateIntent
}
