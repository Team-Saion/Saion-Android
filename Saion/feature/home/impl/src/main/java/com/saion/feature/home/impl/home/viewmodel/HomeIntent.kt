package com.saion.feature.home.impl.home.viewmodel

import com.saion.core.ui.viewmodel.UIIntent

internal sealed interface HomeIntent : UIIntent {
    data object InviteClicked : HomeIntent
    data object HeroScheduleShareClicked : HomeIntent
}
