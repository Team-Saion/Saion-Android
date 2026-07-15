package com.saion.feature.home.impl.viewmodel

import com.saion.core.ui.viewmodel.UIIntent

internal sealed interface HomeIntent : UIIntent {
    data object Load : HomeIntent
}
