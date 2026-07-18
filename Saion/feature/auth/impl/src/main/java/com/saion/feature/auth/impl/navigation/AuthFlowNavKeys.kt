package com.saion.feature.auth.impl.navigation

import com.saion.core.navigation.key.AppNavKey
import kotlinx.serialization.Serializable

internal sealed interface AuthFlowNavKey : AppNavKey

@Serializable
internal data object LoginNavKey : AuthFlowNavKey

@Serializable
internal data object ProfileEditNavKey : AuthFlowNavKey
