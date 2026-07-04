package com.saion.app.navigation.startup

import com.saion.feature.auth.api.key.AuthStartStep

sealed interface AppStartDestination {
    data object SplashThenLogin : AppStartDestination

    data class Auth(val startStep: AuthStartStep) : AppStartDestination

    data object Main : AppStartDestination
}
