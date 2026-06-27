package com.saion.feature.main.api.key

import com.saion.core.navigation.key.AppNavKey
import kotlinx.serialization.Serializable

@Serializable
data object MainNavKey : AppNavKey

interface MainTabNavKey : AppNavKey
