package com.saion.feature.auth.api.key

import com.saion.core.navigation.key.AppNavKey
import kotlinx.serialization.Serializable

@Serializable
data class AuthNavKey(val startStep: AuthStartStep = AuthStartStep.LOGIN) : AppNavKey
